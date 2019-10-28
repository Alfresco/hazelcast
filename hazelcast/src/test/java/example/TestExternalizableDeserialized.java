package example;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public class TestExternalizableDeserialized implements Externalizable {

    public static volatile boolean isDeserialized = false;

    // field used for indexing in IMap
    private String name = "foo";

    public void writeExternal (ObjectOutput out) throws IOException {

    }

    public void readExternal (ObjectInput in) throws IOException, ClassNotFoundException {
        isDeserialized = true;
    }
}