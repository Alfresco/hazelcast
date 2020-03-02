package com.hazelcast.patch;

import com.hazelcast.impl.ThreadContext;
import com.hazelcast.nio.AbstractSerializer;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;

public class ClassLoaderAwareObjectInputStream extends ObjectInputStream {


    public ClassLoaderAwareObjectInputStream (InputStream in) throws IOException {
        super(in);
    }

    @Override
    protected Class<?> resolveClass (final ObjectStreamClass desc) throws ClassNotFoundException {
        String clazzName = desc.getName();

        SerializationClassNameFilter serializationClassNameFilter = new SerializationClassNameFilter(
                ThreadContext.get().getCurrentFactory().getConfig()
                        .getSerializationConfig().getJavaSerializationFilterConfig()
        );
        System.out.println("Class to deserialize=" + clazzName + "; ThreadName=" + Thread.currentThread().getName() + "; " + ThreadContext.get().getCurrentFactory().getConfig().getInstanceName());

        serializationClassNameFilter.filter(clazzName);

        return AbstractSerializer.loadClass(clazzName);
    }
}
