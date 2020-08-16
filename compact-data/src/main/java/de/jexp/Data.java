package de.jexp;

public class Data {
    private final byte[] data;
    public Data(int size) {
        this.data = new byte[size];
    }
    long read(int offset, int len) {
        long result = 0;
        for (int idx = offset; idx < offset + len; idx++) {
            result = result << 8 | (0xFF & data[idx]);
        }
        return result;
    }

    void write(long value, int offset, int len) {
        for (int idx = offset + len - 1; idx >= offset; idx--) {
            data[idx] = (byte) (0xFF & value);
            value >>>= 8;
        }
    }

    @Override
    public String toString() {
        return "Data["+data.length+"]";
    }
}
