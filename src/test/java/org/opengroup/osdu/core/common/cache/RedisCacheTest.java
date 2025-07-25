// Copyright 2017-2019, Schlumberger
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package org.opengroup.osdu.core.common.cache;


import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import io.lettuce.core.codec.RedisCodec;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class RedisCacheTest {

    private static final String HOST = "host";
    private static final int PORT = 8080;
    private static final int EXPIRY = 1000;
    @Mock
    private RedisClient redisClient;

    @Mock
    private StatefulRedisConnection<String, Integer> connection;

    @Mock
    private RedisCommands<String, Integer> commands;

    MockedStatic<RedisClient> redisClientMockedStatic;

    @Before
    public void init() {
        redisClientMockedStatic = Mockito.mockStatic(RedisClient.class);
        when(RedisClient.create((RedisURI) Mockito.any())).thenReturn(redisClient);
        when(redisClient.connect((RedisCodec) Mockito.any())).thenReturn(connection);
        when(connection.sync()).thenReturn(commands);
    }

    @Test
    public void should_initialize_redis_cache_and_increment_value_for_key() {
        String key = "key1";
        when(commands.incrby(key, 1)).thenReturn(2L);

        RedisCache<String, Integer> cache = new RedisCache<String, Integer>(HOST, PORT, EXPIRY, 5,
                 null, String.class, Integer.class);

        Long value = cache.increment(key);
        assertEquals(2, value.intValue());
        verify(redisClient, times(1)).connect((RedisCodec) Mockito.any());
        verify(connection, times(1)).sync();
        verify(commands, times(1)).incrby(key, 1);
    }

    @Test
    public void should_initialize_redis_cache_and_increment_value_but_return_zero() {
        String key = "key1";

        RedisCache<String, Integer> cache = new RedisCache<String, Integer>(HOST, PORT, EXPIRY, 5,
                null, String.class, Integer.class);

        Long value = cache.increment(key);
        assertEquals(0, value.intValue());
        verify(redisClient, times(1)).connect((RedisCodec) Mockito.any());
        verify(connection, times(1)).sync();
        verify(commands, times(1)).incrby(key, 1);
    }

    @Test
    public void should_initialize_redis_cache_and_decrement_value_for_key() {
        String key = "key1";
        when(commands.decrby(key, 1)).thenReturn(1L);

        RedisCache<String, Integer> cache = new RedisCache<String, Integer>(HOST, PORT, EXPIRY, 5,
                null, String.class, Integer.class);

        Long value = cache.decrement(key);
        assertEquals(1, value.intValue());
        verify(redisClient, times(1)).connect((RedisCodec) Mockito.any());
        verify(connection, times(1)).sync();
        verify(commands, times(1)).decrby(key, 1);
    }

    @Test
    public void should_initialize_redis_cache_and_decrement_value_but_return_zero() {
        String key = "key1";
        RedisCache<String, Integer> cache = new RedisCache<String, Integer>(HOST, PORT, EXPIRY, 5,
                null, String.class, Integer.class);

        Long value = cache.decrement(key);
        assertEquals(0, value.intValue());
        verify(redisClient, times(1)).connect((RedisCodec) Mockito.any());
        verify(connection, times(1)).sync();
        verify(commands, times(1)).decrby(key, 1);
    }

    @After
    public void dropDown(){
        redisClientMockedStatic.close();
    }
}
