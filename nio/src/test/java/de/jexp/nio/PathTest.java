package de.jexp.nio;

import org.junit.Test;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.file.*;
import java.nio.file.attribute.UserPrincipal;
import java.util.Arrays;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.Assert.assertEquals;

/**
 * @author mh
 * @since 25.05.14
 */
public class PathTest {

    public static final int PORT = 4711;

    @Test
    public void testFileOnPath() throws Exception {
        FileSystem fs = FileSystems.getDefault();
        Path path = fs.getPath("pom.xml");
        assertEquals(path, Paths.get("pom.xml"));

    }

    static final int MB = 1024 * 1024;

    @Test
    public void testMeasureReadFile() throws Exception {
        File file = new File("target/big.file");
        if (!file.exists()) createFile(file, 4000L * MB);
        measureReadFileInputStream(file);
    }

    @Test
    public void testMeasureReadFileChannel() throws Exception {
        File file = new File("target/big.file");
        if (!file.exists()) createFile(file, 4000L * MB);
        measureReadFileFileChannel(file);
    }

    @Test
    public void testMeasureReadAsyncFileChannel() throws Exception {
        File file = new File("target/big.file");
        if (!file.exists()) createFile(file, 4000L * MB);
        measureReadFileAsyncFileChannel(file);
    }

    public long measureReadFileInputStream(File file) throws Exception {
        byte[] buffer = new byte[MB * 16];
        BufferedInputStream is = new BufferedInputStream(new FileInputStream(file), MB);
        int read;
        long count = 0, sum = 0;
        long time = System.currentTimeMillis();
        while ((read = is.read(buffer)) != -1) {
            for (int i = 0; i < read; i++) {
                count++;
                sum += buffer[i];
            }
        }
        is.close();
        System.out.printf("reading %s time %d ms, size %d MB%n", file, (System.currentTimeMillis() - time), count / MB);
        return sum;

    }

    public long measureReadFileFileChannel(File file) throws Exception {
        ByteBuffer buffer = ByteBuffer.allocate(MB * 16);
        FileChannel channel = new RandomAccessFile(file, "r").getChannel();
        int read;
        long count = 0, sum = 0;
        long time = System.currentTimeMillis();
        while ((read = channel.read(buffer)) != -1) {
            buffer.flip();
            for (int i = 0; i < read; i++) {
                count++;
                sum += buffer.get();
            }
            buffer.clear();
        }
        channel.close();
        System.out.printf("reading %s time %d ms, size %d MB%n", file, (System.currentTimeMillis() - time), count / MB);
        return sum;

    }

    @Test
    public void testServer() throws Exception {
        InetSocketAddress address = new InetSocketAddress(PORT);
        AsynchronousServerSocketChannel server =
                AsynchronousServerSocketChannel.open().bind(address);

        String attachment = "Accepted Connection on " + address;
        server.accept(attachment, new CompletionHandlerFI<AsynchronousSocketChannel, String>() {
            @Override
            public void done(Throwable exc, AsynchronousSocketChannel worker, String attachment) throws Exception {
                if (exc != null)
                    System.err.println("Exception while listening on " + attachment + " " + exc.getMessage());
                else {
                    try {
                        ByteBuffer buffer = ByteBuffer.allocate(1000);
                        int bytesRead;
                        while ((bytesRead = worker.read(buffer).get(10, TimeUnit.SECONDS)) != -1) {
                            String content = new String(buffer.array());
                            System.out.println("Message: " + content + " bytes-read " + bytesRead);
                            ByteBuffer response = ByteBuffer.wrap(content.toUpperCase().getBytes());
                            worker.write(response).get();
                        }
                    } finally {
                        worker.close();
                    }
                }
            }
        });
        Thread.currentThread().join();
        server.close();
    }

    @Test
    public void testClient() throws Exception {
        AsynchronousSocketChannel client = AsynchronousSocketChannel.open();
        client.connect(new InetSocketAddress(PORT)).get();

        ByteBuffer buffer = ByteBuffer.wrap("ping".getBytes());
        Integer bytesWritten = client.write(buffer).get();
        System.out.println("Message: " + new String(buffer.array()) + " bytes-written " + bytesWritten);
        buffer.flip();
        Integer bytesRead = client.read(buffer).get();
        System.out.println("Response: " + new String(buffer.array()) + " bytes-read " + bytesRead);

        client.close();
    }

    public long measureReadFileAsyncFileChannel(File file) throws Exception {
        int bufferSize = MB;
        long fileSize = file.length();

        // count-down-latch for waiting for total completion
        int segments = (int) (fileSize / bufferSize);
        if ((long) segments * bufferSize < fileSize) segments++;
        CountDownLatch latch = new CountDownLatch(segments);

        System.out.printf("segments = %d file size %d buffer size %d%n", segments, fileSize, bufferSize);

        // preparing a pool of buffers
        BlockingQueue<ByteBuffer> buffers = new ArrayBlockingQueue<>(100);
        for (int i = 0; i < 100; i++) {
            buffers.offer(ByteBuffer.allocate(bufferSize));
        }

        long time = System.currentTimeMillis();
        final AtomicLong sum = new AtomicLong();
        final AtomicLong totalCount = new AtomicLong();
        AsynchronousFileChannel channel = AsynchronousFileChannel.open(Paths.get(file.getAbsolutePath()), StandardOpenOption.READ);
        for (long position = 0; position < fileSize; position += bufferSize) {
            final ByteBuffer buffer = buffers.poll(5, TimeUnit.SECONDS);
            buffer.clear();
            String attachment = "Position " + position + " Segment " + position / bufferSize;
            channel.read(buffer, position, attachment, new CompletionHandlerFI<Integer, String>() {
                @Override
                public void done(Throwable exc, Integer bytesRead, String attachment) {
                    if (exc != null) {
                        System.err.println("Error" + exc);
                        return;
                    }
                    buffer.flip();
                    int localSum = 0;
                    for (int i = 0; i < bytesRead; i++) {
                        localSum += buffer.get();
                    }
                    long totalSum = sum.addAndGet(localSum);
                    totalCount.addAndGet(bytesRead);
                    System.out.printf("%s bytes read: %d bytes, localSum: %d totalSum (currently): %d latches %d total-count %d%n", attachment, bytesRead, localSum, totalSum, latch.getCount(), totalCount.get());
                    buffers.offer(buffer);
                    latch.countDown();
                }
            });
        }
        latch.await();
        channel.close();
        System.out.printf("reading %s time %d ms, size %d MB%n total-read %d total-sum %d", file, (System.currentTimeMillis() - time), fileSize / MB, totalCount.get(), sum.get());
        return sum.get();
    }

    public long createFile(File file, long size) throws Exception {
        ByteBuffer buffer = ByteBuffer.allocate(4 * MB);
        for (int i = 0; i < MB; i++) buffer.putInt(i);
        FileChannel channel = new RandomAccessFile(file, "rw").getChannel();
        long written = 0;
        long time = System.currentTimeMillis();
        while (written < size) {
            buffer.flip();
            written += channel.write(buffer);
        }
        channel.close();
        System.out.printf("writing %s time %d ms, size %d MB%n", file, (System.currentTimeMillis() - time), written / MB);
        return written;

    }

    @Test
    public void testWatchService() throws Exception {
        WatchService watchService = FileSystems.getDefault().newWatchService();
        Path source = Paths.get("src/main/resources");
        Path target = Paths.get("target");
        source.register(watchService, StandardWatchEventKinds.ENTRY_MODIFY);
        for (; ; ) {
            WatchKey key = watchService.poll(10, TimeUnit.SECONDS);
            if (key == null) break; // timeout
            for (WatchEvent evt : key.pollEvents()) {
                Path file = (Path) evt.context();
                Path sourceFile = source.resolve(file);
                System.out.println("Event " + evt.kind() + " file " + file + " sourceFile " + sourceFile);
                Path targetFile = target.resolve(sourceFile);
                Files.createDirectories(targetFile.getParent());
                Files.copy(sourceFile, targetFile);
                key.reset();
            }
        }
    }

    static abstract class CompletionHandlerFI<V, A> implements CompletionHandler<V, A> {
        @Override
        public final void completed(V result, A attachment) {
            try {
                done(null, result, attachment);
            } catch (Exception e) {
                throw new RuntimeException("Error during completion handling", e);
            }
        }

        @Override
        public final void failed(Throwable exc, A attachment) {
            try {
                done(null, null, attachment);
            } catch (Exception e) {
                throw new RuntimeException("Error during failure handling", e);
            }
        }

        public abstract void done(Throwable exc, V result, A attachment) throws Exception;
    }

    @Test
    public void testAsyncWrite() throws Exception {
        Path path = Paths.get("target/test.txt");
        AsynchronousFileChannel channel = AsynchronousFileChannel.open(path, StandardOpenOption.CREATE_NEW);
        // StandardOpenOption.READ, StandardOpenOption.WRITE,
        // StandardOpenOption.CREATE, StandardOpenOption.DELETE_ON_CLOSE
//        CompletionHandlerFI<Integer,String> handler = (errror, count, attachment) -> { };
//        channel.write(ByteBuffer.wrap("Hello World".getBytes()),0,"Write 1", handler);
//        channel.read()
//        channel.close();
//
//        AsynchronousChannelGroup group =
//                AsynchronousChannelGroup.withFixedThreadPool(32, Executors.defaultThreadFactory());
//        AsynchronousFileChannel.open(path, EnumSet.of(StandardOpenOption.CREATE_NEW),group.);
//        AsynchronousServerSocketChannel channel =
//                AsynchronousServerSocketChannel.open(tenThreadGroup);
    }


    @Test
    public void testOtherMethods() throws Exception {
        FileSystem fs = FileSystems.getDefault();
        Path path = fs.getPath("pom.xml");
        System.out.println("fs.supportedFileAttributeViews() = " + fs.supportedFileAttributeViews());
        fs.getRootDirectories().forEach(System.out::println);
        UserPrincipal principal = fs.getUserPrincipalLookupService().lookupPrincipalByName("mh");
        System.out.println("principal = " + principal);
        // todo is available?
        // todo metadata
        path.register(new WatchService() {
            @Override
            public void close() throws IOException {

            }

            @Override
            public WatchKey poll() {
                return null;
            }

            @Override
            public WatchKey poll(long timeout, TimeUnit unit) throws InterruptedException {
                return null;
            }

            @Override
            public WatchKey take() throws InterruptedException {
                return null;
            }
        }, StandardWatchEventKinds.ENTRY_MODIFY);

    }

    // todo WatchService
}
