package de.jexp.example.serialization;

import java.io.Serializable;

public class Child implements Serializable {

    double _double;

    public static Child create(int count) {
        Child child = new Child();
        child._double = count * count / Math.PI;
        return child;
    }
}
