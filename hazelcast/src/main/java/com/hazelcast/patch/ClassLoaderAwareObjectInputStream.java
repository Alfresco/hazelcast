package com.hazelcast.patch;

import com.hazelcast.nio.AbstractSerializer;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;

public class ClassLoaderAwareObjectInputStream extends ObjectInputStream {

    private final static ClassNameFilter classFilter = new SerializationClassNameFilter(
            new AlfBlackListClassFilter(),
            new AlfWhiteListClassFilter(),
            true
    );

    public ClassLoaderAwareObjectInputStream (InputStream in) throws IOException {
        super(in);
    }

    @Override
    protected Class<?> resolveClass (final ObjectStreamClass desc) throws ClassNotFoundException {
        String clazzName = desc.getName();
        classFilter.filter(clazzName);
        return AbstractSerializer.loadClass(clazzName);
    }
}
