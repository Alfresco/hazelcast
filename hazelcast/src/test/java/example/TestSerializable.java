package example;

import java.io.Serializable;

public class TestSerializable implements Serializable {
    private static final long serialVersionUID = 1L;
    public static volatile boolean IS_DESERIALIZED = false;

    private void writeObject (java.io.ObjectOutputStream out) {
    }

    private void readObject (java.io.ObjectInputStream in) {
        IS_DESERIALIZED = true;
    }
}