

package com.hazelcast.patch;

import example.TestDeserialized;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.fail;


public class SerializationFilterTest extends TestSerializableFilterAware {

    @AfterClass
    public static void afterClass () {

    }

    @Before
    public void before () throws Exception {
        resetFilter();
    }


    @Test
    public void testBlackListClassIsPriorToWhiteListClass () throws Exception {
        SerializationClassNameFilter filter = getFilter();

        filter.getBlacklist().addClasses(TestDeserialized.class.getName());
        filter.getWhitelist().addClasses(TestDeserialized.class.getName());

        try {
            filter.filter(TestDeserialized.class.getName()); //expected error
            fail("Deserialization should have failed");
        } catch (SecurityException e) {
        }
    }

    @Test
    public void testBlackListClassPriorToWhitelistPackage () throws Exception {
        SerializationClassNameFilter classFilter = getFilter();

        classFilter.getBlacklist().addPackages(TestDeserialized.class.getPackage().getName());
        classFilter.getWhitelist().addClasses(TestDeserialized.class.getName());

        try {
            classFilter.filter(TestDeserialized.class.getName()); //expected error
            fail("Deserialization should have failed");
        } catch (SecurityException e) {
        }
    }

    @Test
    public void testWhiteListFilterSingleClass () throws Exception {
        SerializationClassNameFilter classFilter = getFilter();

        classFilter.getWhitelist().addClasses(TestDeserialized.class.getName());

        try {
            classFilter.filter(TestDeserialized.class.getName()); //expected error

        } catch (SecurityException e) {
            fail("Deserialization should not have failed");
        }
    }

    @Test
    public void testWhiteListFilterPackages () throws Exception {
        disableDefaultWhitelist();

        SerializationClassNameFilter classFilter = getFilter();

        classFilter.getWhitelist().addPackages(TestDeserialized.class.getPackage().getName(), "java.lang");

        try {
            classFilter.filter(TestDeserialized.class.getName());
            classFilter.filter(Object.class.getName());

        } catch (SecurityException e) {
            fail("Deserialization should not have failed");
        }
    }


    @Test
    public void testDefaultWhiteList () throws Exception {
        SerializationClassNameFilter filter = getFilter();

        try {
            filter.filter(Object.class.getName());
            filter.filter(ArrayList[].class.getName());
            filter.filter("[Lhazelcast.com");

        } catch (SecurityException e) {
            fail("Deserialization should not have failed");
        }
    }

    @Test
    public void testAlfrescoWhiteList () throws Exception {
        SerializationClassNameFilter filter = getFilter();

        try {
            filter.filter("org.alfresco.util.Pair");
            filter.filter("[Lorg.alfresco.util.Pair");

        } catch (SecurityException e) {
            fail("Deserialization should not have failed");
        }
    }
}