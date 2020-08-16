package de.jexp;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.stream.IntStream;

class DataTest {

    @Test
    void readSize1() {
        Data data = new Data(10);
        for (int i=0;i<255;i++) {
            data.write(i,0,1);
            Assertions.assertEquals(i, data.read(0,1));
        }
        for (int i=0;i<Short.MAX_VALUE;i+=10) {
            data.write(i,1,2);
            Assertions.assertEquals(i, data.read(1,2));
        }
        for (long i = 0; i<Integer.MAX_VALUE; i+=Integer.MAX_VALUE / 10000) {
            data.write(i,6,4);
            Assertions.assertEquals(i, data.read(6 ,4));
        }
    }
}