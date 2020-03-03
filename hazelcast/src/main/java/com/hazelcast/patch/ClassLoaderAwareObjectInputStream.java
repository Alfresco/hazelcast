package com.hazelcast.patch;

import com.hazelcast.impl.FactoryImpl;
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

        FactoryImpl currentFactory = ThreadContext.get().getCurrentFactory();

        if(currentFactory != null){
            SerializationClassNameFilter serializationClassNameFilter = new SerializationClassNameFilter(
                    currentFactory.getConfig()
                            .getSerializationConfig().getJavaSerializationFilterConfig()
            );
            serializationClassNameFilter.filter(clazzName);
        }


        return AbstractSerializer.loadClass(clazzName);
    }
}
