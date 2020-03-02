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

    @Before
    @After
    public void startStop () {
        Hazelcast.shutdownAll();
    }

    @Test
    public void testFailsOnBlacklistedClass () {
        Config config = new Config();
        config.setInstanceName("Member1");

        config.getSerializationConfig()
                .getJavaSerializationFilterConfig()
                .getBlacklist()
                .addClasses(TestSerializable.class.getName());

        HazelcastInstance member = Hazelcast.newHazelcastInstance(config);

        HazelcastInstance member2 = Hazelcast.newHazelcastInstance(new Config().setInstanceName("Member2"));

        member2.getMap("test").put("key", new TestSerializable());
        member.getMap("test").get("key");
        member2.getMap("test").get("key");
        assertFalse(TestSerializable.IS_DESERIALIZED);
    }

    @Test
    public void testFailsOnBlacklistedPackage () {
        Config config = new Config();

        config.getSerializationConfig()
                .getJavaSerializationFilterConfig()
                .getBlacklist()
                .addClasses(TestSerializable.class.getPackage().getName());

        HazelcastInstance member = Hazelcast.newHazelcastInstance(config);
        HazelcastInstance member2 = Hazelcast.newHazelcastInstance(new Config());

        member2.getMap("test").put("key", new TestSerializable());
        member.getMap("test").get("key");
        assertFalse(TestSerializable.IS_DESERIALIZED);
    }


    @Test
    public void tesNotFailsOnWhitelistedClass () {
        Config config = new Config();

        config.getSerializationConfig()
                .getJavaSerializationFilterConfig()
                .getWhitelist()
                .addClasses(ArrayList.class.getName());

        HazelcastInstance member = Hazelcast.newHazelcastInstance(config);
        HazelcastInstance member2 = Hazelcast.newHazelcastInstance(new Config());

        member2.getMap("test").put("key", new ArrayList<Object>());
        member.getMap("test").get("key");
        assertTrue(true);
    }

    @Test
    public void tesDefaultWhitelistDisabled () {
        Config config = new Config();

        config.getSerializationConfig()
                .getJavaSerializationFilterConfig()
                .setDefaultsDisabled(true);

        HazelcastInstance member = Hazelcast.newHazelcastInstance(config);
        HazelcastInstance member2 = Hazelcast.newHazelcastInstance(new Config());

        member2.getMap("test").put("key", new ArrayList<Object>());
        member.getMap("test").get("key");
        assertTrue(true);
    }

    @Test
    public void testPrefixWhitelisted () {
        Config config = new Config();

        config.getSerializationConfig()
                .getJavaSerializationFilterConfig()
                .getWhitelist()
                .addPrefixes("example.");

        HazelcastInstance member = Hazelcast.newHazelcastInstance(config);
        HazelcastInstance member2 = Hazelcast.newHazelcastInstance(new Config());

        member2.getMap("test").put("key", new TestSerializable());
        member.getMap("test").get("key");
        assertTrue(true);
    }

    @Test
    public void testExternalizableBlackListed () {
        Config config = new Config();

        config.getSerializationConfig()
                .getJavaSerializationFilterConfig()
                .getBlacklist()
                .addClasses(TestExternalizable.class.getName());

        HazelcastInstance member = Hazelcast.newHazelcastInstance(config);
        HazelcastInstance member2 = Hazelcast.newHazelcastInstance(new Config());

        member2.getMap("test").put("key", new TestExternalizable());
        member.getMap("test").get("key");
        assertFalse(TestExternalizable.isDeserialized);
    }

}