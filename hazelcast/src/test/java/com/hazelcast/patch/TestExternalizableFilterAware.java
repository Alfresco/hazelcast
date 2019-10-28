package com.hazelcast.patch;

import com.hazelcast.core.Hazelcast;
import com.hazelcast.nio.DefaultSerializer;
import example.TestExternalizableDeserialized;
import org.junit.AfterClass;
import org.junit.Before;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class TestExternalizableFilterAware {

    protected static SerializationClassNameFilter getFilter () throws Exception {
        Field classFilterField = DefaultSerializer.class.getDeclaredField("classFilter");

        classFilterField.setAccessible(true);

        return (SerializationClassNameFilter) classFilterField.get(DefaultSerializer.class);
    }

    protected static void disableDefaultWhitelist () throws Exception {
        SerializationClassNameFilter filter = getFilter();

        Field useDefaultWhitelistField = filter.getClass().getDeclaredField("useDefaultWhitelist");
        useDefaultWhitelistField.setAccessible(true);

        Field modifiersField = Field.class.getDeclaredField("modifiers");
        modifiersField.setAccessible(true);
        modifiersField.setInt(useDefaultWhitelistField, useDefaultWhitelistField.getModifiers() & ~Modifier.FINAL);

        useDefaultWhitelistField.set(filter, false);
    }

    protected static void resetFilter () throws Exception {
        Field classFilterField = DefaultSerializer.class.getDeclaredField("classFilter");

        classFilterField.setAccessible(true);

        Field modifiersField = Field.class.getDeclaredField("modifiers");
        modifiersField.setAccessible(true);
        modifiersField.setInt(classFilterField, classFilterField.getModifiers() & ~Modifier.FINAL);

        classFilterField.set(classFilterField, new SerializationClassNameFilter(
                new AlfBlackListClassFilter(),
                new AlfWhiteListClassFilter(),
                true
        ));
    }

    @AfterClass
    public static final void stopHazelcastInstances () throws Exception {
        Hazelcast.shutdownAll();
        TestExternalizableDeserialized.isDeserialized = false;
        resetFilter();
    }

    @Before
    public void killAllHazelcastInstances () throws Exception {
        Hazelcast.shutdownAll();
        TestExternalizableDeserialized.isDeserialized = false;
        resetFilter();
    }

}
