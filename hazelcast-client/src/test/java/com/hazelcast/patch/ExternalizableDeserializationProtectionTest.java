/*
 * Copyright (c) 2008-2019, Hazelcast, Inc. All Rights Reserved.
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

import com.hazelcast.client.ClientConfig;
import com.hazelcast.client.HazelcastClient;
import com.hazelcast.config.Config;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.nio.HazelcastSerializationException;
import example.TestExternalizableDeserialized;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

/**
 * Tests untrusted deserialization protection for Externalizables.
 *
 * <pre>
 * Given: Hazelcast member and clients are started.
 * </pre>
 */
public class ExternalizableDeserializationProtectionTest extends TestExternalizableFilterAware {


    @Test
    public void testExternalizableProtectedOnMember() throws Exception {

        HazelcastInstance member = Hazelcast.newHazelcastInstance(new Config());
        HazelcastInstance client = HazelcastClient.newHazelcastClient(new ClientConfig());

        client.getMap("test").put("key", new TestExternalizableDeserialized());
        try {
            member.getMap("test").get("key");
            fail("Deserialization should have failed");
        } catch (HazelcastSerializationException e) {
            assertFalse(TestExternalizableDeserialized.isDeserialized);
        }
    }

    @Test
    public void testExternalizableProtectedOnClient() throws Exception {
        HazelcastInstance member = Hazelcast.newHazelcastInstance(new Config());
        HazelcastInstance client = HazelcastClient.newHazelcastClient(new ClientConfig());

        member.getMap("test").put("key", new TestExternalizableDeserialized());
        try {
            client.getMap("test").get("key");
            fail("Deserialization should have failed");
        } catch (HazelcastSerializationException e) {
            assertFalse(TestExternalizableDeserialized.isDeserialized);
        }
    }


    @Test
    public void testExternalizableProtectedBetweenClients() throws Exception {
        HazelcastInstance member = Hazelcast.newHazelcastInstance(new Config());
        HazelcastInstance client = HazelcastClient.newHazelcastClient(new ClientConfig());
        HazelcastInstance client2 = HazelcastClient.newHazelcastClient(new ClientConfig());

        client.getMap("test").put("key", new TestExternalizableDeserialized());
        try {
            client2.getMap("test").get("key");
            fail("Deserialization should have failed");
        } catch (HazelcastSerializationException e) {
            assertFalse(TestExternalizableDeserialized.isDeserialized);
        }
    }
}