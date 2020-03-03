/*
 * Copyright (c) 2008-2018, Hazelcast, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hazelcast.nio.serialization;

import com.hazelcast.config.Config;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.nio.HazelcastSerializationException;
import example.TestSerializable;
import example.TestExternalizable;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

/**
 * Tests untrusted deserialization protection.
 *
 * <pre>
 * Given: 2 node cluster is started.
 * </pre>
 */
public class DeserializationProtectionTest {

    HazelcastInstance member;
    HazelcastInstance member2;

    @Before
    public void start () throws InterruptedException {
        member = Hazelcast.newHazelcastInstance(new Config().setInstanceName("Member1"));
        member2 = Hazelcast.newHazelcastInstance(new Config().setInstanceName("Member2"));
        Thread.sleep(5000);
    }

    @After
    public void stop () {
        Hazelcast.shutdownAll();
    }

    @Test
    public void testFailsOnBlacklistedClass () {

        member.getConfig().getSerializationConfig()
                .getJavaSerializationFilterConfig()
                .getBlacklist()
                .addClasses(TestSerializable.class.getName());


        member2.getMap("test").put("key", new TestSerializable());
        try {
            member.getMap("test").get("key");
            member2.getMap("test").get("key");
            fail("Deserialization should have failed");
        } catch (HazelcastSerializationException e) {
            assertFalse(TestSerializable.IS_DESERIALIZED);
        }
    }

    @Test
    public void testFailsOnBlacklistedPackage () {

        member.getConfig().getSerializationConfig()
                .getJavaSerializationFilterConfig()
                .getBlacklist()
                .addClasses(TestSerializable.class.getPackage().getName());

        member2.getMap("test").put("key", new TestSerializable());
        try {
            member.getMap("test").get("key");
            fail("Deserialization should have failed");
        } catch (HazelcastSerializationException e) {
            assertFalse(TestSerializable.IS_DESERIALIZED);
        }
    }


    @Test
    public void tesNotFailsOnWhitelistedClass () {

        member.getConfig().getSerializationConfig()
                .getJavaSerializationFilterConfig()
                .getWhitelist()
                .addClasses(ArrayList.class.getName());


        member2.getMap("test").put("key", new ArrayList<Object>());
        member.getMap("test").get("key");
        assertTrue(true);
    }

    @Test
    public void tesDefaultWhitelistDisabled () {

        member.getConfig().getSerializationConfig()
                .getJavaSerializationFilterConfig()
                .setDefaultsDisabled(true);

        member2.getMap("test").put("key", new ArrayList<Object>());
        member.getMap("test").get("key");
        assertTrue(true);
    }

    @Test
    public void testPrefixWhitelisted () {

        member.getConfig().getSerializationConfig()
                .getJavaSerializationFilterConfig()
                .getWhitelist()
                .addPrefixes("example.");

        member2.getMap("test").put("key", new TestSerializable());
        member.getMap("test").get("key");
        assertTrue(true);
    }

    @Test
    public void testExternalizableBlackListed () {

        member.getConfig().getSerializationConfig()
                .getJavaSerializationFilterConfig()
                .getBlacklist()
                .addClasses(TestExternalizable.class.getName());

        member2.getMap("test").put("key", new TestExternalizable());
        try {
            member.getMap("test").get("key");
            fail("Deserialization should have failed");
        } catch (HazelcastSerializationException e) {
            assertFalse(TestExternalizable.isDeserialized);
        }
    }

}