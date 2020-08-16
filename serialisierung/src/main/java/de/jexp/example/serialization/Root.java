package de.jexp.example.serialization;

import java.io.Serializable;
import java.util.Arrays;

public class Root implements Serializable {
    String _string;
    int _int = Integer.MIN_VALUE + 1;
    Integer _integer = Integer.MAX_VALUE -1;
    Long _long = Long.MAX_VALUE - 1;
    int[] _int_array;
    long[] _long_array;
    Child[] children;

    public static Root create(int count) {
        Root root = new Root();
        char[] chars = new char[count];
        Arrays.fill(chars,'A');
        root._string = String.valueOf(chars);
        root._int_array = new int[count];
        Arrays.fill(root._int_array,42);
        root._long_array = new long[count];
        Arrays.fill(root._long_array, Integer.MAX_VALUE ^ 2);
        root.children = new Child[count];
        for (int i = 0; i < count; i++) {
            root.children[i] = Child.create(count);
        }
        return root;
    }
}
