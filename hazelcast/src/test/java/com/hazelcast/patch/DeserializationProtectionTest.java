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

package com.hazelcast.patch;

import com.hazelcast.config.Config;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.nio.HazelcastSerializationException;
import example.TestDeserialized;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Tests untrusted deserialization protection.
 *
 * <pre>
 * Given: 2 node cluster is started.
 * </pre>
 */
public class DeserializationProtectionTest extends TestSerializableFilterAware {

    /**
     * <pre>
     * When: An untrusted serialized object is stored from client and read from member, the default Whitelist is used.
     * Then: Deserialization fails.
     * </pre>
     */
    @Test
    public void testDefaultDeserializationFilter_readOnMember () {
        HazelcastInstance member = Hazelcast.newHazelcastInstance(new Config());
        HazelcastInstance member2 = Hazelcast.newHazelcastInstance(new Config());

        member2.getMap("test").put("key", new TestDeserialized());
        try {
            member.getMap("test").get("key");
            fail("Deserialization should have failed");
        } catch (HazelcastSerializationException e) {
            assertFalse(TestDeserialized.IS_DESERIALIZED);
        }
    }


    /**
     * <pre>
     * When: Default Whitelist is disabled and classname of the test serialized object is blacklisted. The object is read from client.
     * Then: Deserialization fails.
     * </pre>
     */
    @Test
    public void testClassBlacklisted() throws Exception {
        disableDefaultWhitelist();

        SerializationClassNameFilter classFilter = getFilter();
        classFilter.getBlacklist().addClasses(TestDeserialized.class.getName());

        HazelcastInstance member = Hazelcast.newHazelcastInstance(new Config());
        HazelcastInstance member2 = Hazelcast.newHazelcastInstance(new Config());

        member.getMap("test").put("key", new TestDeserialized());
        try {
            member2.getMap("test").get("key");
            fail("Deserialization should have failed");
        } catch (HazelcastSerializationException e) {
            assertFalse(TestDeserialized.IS_DESERIALIZED);
        }
    }


    /**
     * <pre>
     * When: Deserialization filtering is enabled and classname of test object is whitelisted.
     * Then: The deserialization is possible.
     * </pre>
     */
    @Test
    public void testClassWhitelisted() throws Exception {
        getFilter().getWhitelist().addClasses(TestDeserialized.class.getName());

        HazelcastInstance member = Hazelcast.newHazelcastInstance(new Config());
        HazelcastInstance member2 = Hazelcast.newHazelcastInstance(new Config());

        member.getMap("test").put("key", new TestDeserialized());
        member2.getMap("test").get("key");
        assertTrue(TestDeserialized.IS_DESERIALIZED);
    }

    @Test
    public void testArrayOfBlackListed() throws Exception {
        getFilter().getBlacklist().addClasses(TestDeserialized.class.getName());

        HazelcastInstance member = Hazelcast.newHazelcastInstance(new Config());
        HazelcastInstance member2 = Hazelcast.newHazelcastInstance(new Config());

        member.getMap("test").put("key", new Object[]{new TestDeserialized()});
        try {
            member2.getMap("test").get("key");
            fail("deserialization should have failed");
        }catch (HazelcastSerializationException e){
            assertFalse(TestDeserialized.IS_DESERIALIZED);
        }
    }

}