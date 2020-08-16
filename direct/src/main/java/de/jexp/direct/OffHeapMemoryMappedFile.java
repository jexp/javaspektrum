package de.jexp.direct;

import java.io.*;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

/**
 * @author mh
 * @since 26.11.11
 */
public class OffHeapMemoryMappedFile {
    public static void main(String[] args) throws IOException {
        final File file = new File("test.bin");
        file.delete();
        final FileOutputStream fos = new FileOutputStream(file);
        for (int i=0;i<10000;i++) {
            fos.write((byte)42);
        }
        fos.close();
        long time = System.currentTimeMillis();
        final RandomAccessFile raf = new RandomAccessFile(file,"rw");
        final MappedByteBuffer buffer = raf.getChannel().map(FileChannel.MapMode.READ_WRITE, 0, raf.length());
        for (int i=0;i<10000;i++) {
            buffer.mark();
            assert 42 == buffer.get();
            buffer.reset();
            buffer.put((byte)11);
            if (i % 1000 == 0) {
                // testweise: zurueckschreiben der Daten erzwingen
                buffer.force();
            }
        }
        raf.close();
        long delta = System.currentTimeMillis() - time;
        System.out.println("delta = " + delta);
        FileInputStream fis = new FileInputStream(file);
        for (int i=0;i<10000;i++) {
            assert 11 == fis.read();
        }
        fis.close();
    }
}
