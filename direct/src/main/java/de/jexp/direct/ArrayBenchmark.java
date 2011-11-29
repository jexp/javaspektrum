package de.jexp.direct;

/* javac ArrayBenchmark.java
 * java -Xbootclasspath/a:. ArrayBenchmark
 */

import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import sun.misc.Cleaner;
import sun.misc.Unsafe;

import static de.jexp.direct.Direct.getUnsafe;

class RawBuffer {
    public static final Unsafe unsafe = getUnsafe();

    // order is important
    final long address;
    final int sizeBytes;
    final int size;

    private static class Deallocator
        implements Runnable
    {
        private long address;
        private Deallocator(long address) {
            this.address = address;
        }
        public void run() {
            if (address == 0)
                return;
            unsafe.freeMemory(address);
            address = 0;
        }
    }

    protected RawBuffer(int sizeBytes, int size) {
        this.address = unsafe.allocateMemory(sizeBytes);
        this.sizeBytes = sizeBytes;
        this.size = size;
        // use a phantom reference and cleaner to deallocate
        // the underlying memory, similar to DirectByteBuffer
        Cleaner.create(this, new Deallocator(address));
    }

    public int size() {
        return size;
    }
}

final class CustomIntBuffer
    extends RawBuffer
{
    public static int BYTES_PER_ELEMENT = 4;

    public CustomIntBuffer(int capacity) {
        super(capacity * 4, capacity);
    }

    public int get(int index) {
        if (index < 0 || index >= size)
            throw new IndexOutOfBoundsException();
        return unsafe.getInt(address + index*BYTES_PER_ELEMENT);
    }

    public int getUnchecked(int index) {
        return unsafe.getInt(address + index*BYTES_PER_ELEMENT);
    }

    public void put(int index, int value) {
        if (index < 0 || index >= size)
            throw new IndexOutOfBoundsException();
        unsafe.putInt(address + index*BYTES_PER_ELEMENT, value);
    }

    public void putUnchecked(int index, int value) {
        unsafe.putInt(address + index*BYTES_PER_ELEMENT, value);
    }

    public void getInto(int[] dest, int origin, int count) {
        if (origin < 0 || (origin+count) > size)
            throw new IndexOutOfBoundsException(String.format("%d + %d >= %d",origin,count,size));

        long ptr = address + origin * BYTES_PER_ELEMENT;

        for (int i = 0; i < count; ++i) {
            dest[i] = unsafe.getInt(ptr);
            ptr += 4;
        }
    }

    public void getIntoUsingCopyMemory(int[] dest, int origin, int count) {
        if (origin < 0 || (origin+count) > size)
            throw new IndexOutOfBoundsException(String.format("%d + %d >= %d",origin,count,size));

        long ptr = address + origin * BYTES_PER_ELEMENT;

        assert unsafe.arrayIndexScale(int[].class) == BYTES_PER_ELEMENT;

        unsafe.copyMemory(null, ptr,
                          dest, BYTES_PER_ELEMENT,
                          count * BYTES_PER_ELEMENT);
    }
}

public class ArrayBenchmark {
    static int numIterations = 5;

    public static void main(String[] args) {
        String test = args.length > 0 ? args[0] : null;

        final int size = 10 * 1000 * 1000;
        int check = 0;

        int[] tempbuf = new int[size];

        if (test == null || test.equals("array")) {
            startTest("native java int[] array");
            int[] buf = new int[size];

            for (int iteration = 0; iteration < numIterations; ++iteration) {
                start("put");
                for (int i = 0; i < size; ++i) {
                    buf[i] = i;
                }
                stop();

                start("get");
                for (int i = 0; i < size; ++i) {
                    check |= buf[i];
                }
                stop();

                start("copy into");
                System.arraycopy(buf, 0, tempbuf, 0, size);
                stop();
            }
            endTest();
        }

        if (test == null || test.equals("heap")) {
            startTest("nio heap buffers (native byte order, int buffers)");
            IntBuffer buf = ByteBuffer.allocate(size * 4).order(ByteOrder.nativeOrder()).asIntBuffer();

            for (int iteration = 0; iteration < numIterations; ++iteration) {
                start("put");
                for (int i = 0; i < size; ++i) {
                    buf.put(i, i);
                }
                stop();

                buf.clear();
                start("put (relative)");
                for (int i = 0; i < size; ++i) {
                    buf.put(i);
                }
                stop();

                buf.rewind();

                start("get");
                for (int i = 0; i < size; ++i) {
                    check |= buf.get(i);
                }
                stop();

                buf.rewind();

                start("get (relative)");
                for (int i = 0; i < size; ++i) {
                    check |= buf.get();
                }
                stop();

                buf.rewind();
                start("copy into");
                buf.get(tempbuf);
                stop();
            }
            endTest();
        }

        if (test == null || test.equals("direct")) {
            startTest("nio direct buffers (native byte order, int buffers)");
            IntBuffer buf = ByteBuffer.allocateDirect(size * 4).order(ByteOrder.nativeOrder()).asIntBuffer();

            for (int iteration = 0; iteration < numIterations; ++iteration) {
                start("put");
                for (int i = 0; i < size; ++i) {
                    buf.put(i, i);
                }
                stop();

                buf.clear();
                start("put (relative)");
                for (int i = 0; i < size; ++i) {
                    buf.put(i);
                }
                stop();

                buf.rewind();

                start("get");
                for (int i = 0; i < size; ++i) {
                    check |= buf.get(i);
                }
                stop();

                buf.rewind();

                start("get (relative)");
                for (int i = 0; i < size; ++i) {
                    check |= buf.get();
                }
                stop();

                buf.rewind();
                start("copy into");
                buf.get(tempbuf);
                stop();
            }
            endTest();
        }

        if (test == null || test.equals("fast")) {
            startTest("custom buffers");
            CustomIntBuffer buf = new CustomIntBuffer(size);

            for (int iteration = 0; iteration < numIterations; ++iteration) {
                start("put");
                for (int i = 0; i < size; ++i) {
                    buf.put(i, i);
                }
                stop();

                start("put unchecked");
                for (int i = 0; i < size; ++i) {
                    buf.putUnchecked(i, i);
                }
                stop();

                start("get");
                for (int i = 0; i < size; ++i) {
                    check |= buf.get(i);
                }
                stop();

                start("get unchecked");
                for (int i = 0; i < size; ++i) {
                    check |= buf.getUnchecked(i);
                }
                stop();

                start("copy into");
                buf.getInto(tempbuf, 0, size);
                stop();

                start("copy into (copyMemory)");
                buf.getIntoUsingCopyMemory(tempbuf, 0, size);
                stop();
            }
            endTest();
        }

        if (test == null || test.equals("mixed")) {
            startTest("mixed int[] and direct");
            int[] buf = new int[size];
            IntBuffer dbuf = ByteBuffer.allocateDirect(size * 4).order(ByteOrder.nativeOrder()).asIntBuffer();

            for (int iteration = 0; iteration < numIterations; ++iteration) {
                start("put-[]");
                for (int i = 0; i < size; ++i) {
                    buf[i] = i;
                }
                stop();

                start("put-direct");
                for (int i = 0; i < size; ++i) {
                    dbuf.put(i, i);
                }
                stop();

                dbuf.clear();
                start("put-direct (relative)");
                for (int i = 0; i < size; ++i) {
                    dbuf.put(i);
                }
                stop();

                dbuf.rewind();

                start("get-[]");
                for (int i = 0; i < size; ++i) {
                    check |= buf[i];
                }
                stop();

                start("get-direct");
                for (int i = 0; i < size; ++i) {
                    check |= dbuf.get(i);
                }
                stop();

                dbuf.rewind();

                start("get-direct (relative)");
                for (int i = 0; i < size; ++i) {
                    check |= dbuf.get();
                }
                stop();
            }
            endTest();
        }
        System.out.printf("check = %d\n", check); // to ensure hotspot doesn't optimize out inner read loops
    }

    static long timeStart;
    static String timeDesc;

    static HashMap<String, Long> resultMap;

    public static void startTest(String testName) {
        System.out.printf("===== %s\n", testName);
        resultMap = new HashMap<String, Long>();
    }

    public static void start(String desc) {
        timeDesc = desc;
        timeStart = System.nanoTime();
    }

    public static void stop() {
        long elapsed = System.nanoTime() - timeStart;
        Long current = resultMap.get(timeDesc);
        if (current == null) {
            // throw away first result, to give the jit a chance to compile
            resultMap.put(timeDesc, 0L);
        } else {
            resultMap.put(timeDesc, current + elapsed);
        }

        // System.out.printf("%30s: %f ms\n", timeDesc, elapsed / 1000000.0);
    }

    public static void endTest() {
        List<String> keys = new ArrayList(resultMap.keySet());
        Collections.sort(keys);
        for (String key : keys) {
            Long value = resultMap.get(key);
            System.out.printf("%30s: %f ms\n", key, value / ((numIterations - 1) * 1000000.0));
        }
    }
}
