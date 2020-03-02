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

import com.hazelcast.client.ClientConfig;
import com.hazelcast.client.HazelcastClient;
import com.hazelcast.config.Config;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.nio.HazelcastSerializationException;
import example.TestExternalizable;
import example.TestSerializable;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

public class ClientDeserializationProtectionTest  {
    HazelcastInstance hzInst;

    @After
    public void stop () {
        Hazelcast.shutdownAll();
    }

    @Before
    public void start () {
        hzInst = Hazelcast.newHazelcastInstance(new Config().setInstanceName("Instance1"));
    }

    @Test
    public void testFailsOnBlacklistedClass () throws InterruptedException {

        hzInst.getConfig().getSerializationConfig()
                .getJavaSerializationFilterConfig()
                .setDefaultsDisabled(true);


        HazelcastInstance hzClient = HazelcastClient.newHazelcastClient(new ClientConfig());


        hzInst.getMap("test").put("key", new TestSerializable());
        try {
            hzClient.getMap("test").get("key");
            hzInst.getMap("test").get("key");
            fail("Deserialization should have failed");
        } catch (HazelcastSerializationException e) {
            assertFalse(TestSerializable.IS_DESERIALIZED);
        }
    }


}