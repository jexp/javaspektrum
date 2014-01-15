package de.jexp.example.serialization;

import java.io.IOException;
import java.io.OutputStream;

/**
 * @author mh
 * @since 17.11.13
 */
public class CountingOutputStream extends OutputStream {
    private int count;

    @Override
    public void write(int b) throws IOException {
        count++;
    }

    @Override
    public void write(byte[] b) throws IOException {
        count += b.length;
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        count += len;
    }

    public int getCount() {
        return count;
    }
}
