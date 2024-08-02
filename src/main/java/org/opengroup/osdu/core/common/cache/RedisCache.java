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

import io.lettuce.core.ClientOptions;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisException;
import io.lettuce.core.RedisURI;
import io.lettuce.core.SetArgs;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import io.lettuce.core.codec.CompressionCodec;
import io.lettuce.core.codec.RedisCodec;
import java.time.Duration;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

@Slf4j
public class RedisCache<K, V> implements IRedisCache<K, V>, AutoCloseable {

    private final StatefulRedisConnection<K, V> connection;
    private final RedisClient client;
    private final RedisCommands<K, V> commands;
    private final int expireLengthSeconds;

    public RedisCache(String host, int port, int expTimeSeconds, int database, ClientOptions clientOptions,
                      Class<K> classOfK, Class<V> classOfV) {
        RedisURI uri = new RedisURI(host, port, Duration.ofSeconds(30));
        uri.setDatabase(database);
        client = RedisClient.create(uri);
        if (clientOptions != null) {
            client.setOptions(clientOptions);
        }
        connection = client.connect(this.getCodec(classOfK, classOfV));
        commands = connection.sync();
        expireLengthSeconds = expTimeSeconds;
    }

    public RedisCache(String host, int port, String password, int expTimeSeconds, int database,
        ClientOptions clientOptions, Class<K> classOfK, Class<V> classOfV) {
        RedisURI uri = RedisURI.Builder
            .redis(host, port)
            .withTimeout(Duration.ofSeconds(expTimeSeconds))
            .withPassword(password.toCharArray())
            .withDatabase(database)
            .withSsl(true)
            .build();

        client = RedisClient.create(uri);
        if (clientOptions != null) {
            client.setOptions(clientOptions);
        }
        connection = client.connect(this.getCodec(classOfK, classOfV));
        commands = connection.sync();
        expireLengthSeconds = expTimeSeconds;
    }

    public RedisCache(String host, int port, String password, int expTimeSeconds, int commandExecutionTimeout, int database,
                      ClientOptions clientOptions, Class<K> classOfK, Class<V> classOfV) {
        // Timeout parameter in RedisURL class sets the command timeout for synchronous command execution. A zero timeout value indicates to not time out.
        // https://lettuce.io/core/release/api/io/lettuce/core/RedisURI.html#getTimeout--
        RedisURI uri = RedisURI.Builder
            .redis(host, port)
            .withTimeout(Duration.ofSeconds(commandExecutionTimeout))
            .withPassword(password.toCharArray())
            .withDatabase(database)
            .withSsl(true)
            .build();

        client = RedisClient.create(uri);
        if (clientOptions != null) {
            client.setOptions(clientOptions);
        }
        connection = client.connect(this.getCodec(classOfK, classOfV));
        commands = connection.sync();
        expireLengthSeconds = expTimeSeconds;
    }

    public RedisCache(String host, int port, String password, int expTimeSeconds, int database, boolean withSsl,
        ClientOptions clientOptions, Class<K> classOfK, Class<V> classOfV) {
        RedisURI uri = RedisURI.Builder
            .redis(host, port)
            .withTimeout(Duration.ofSeconds(expTimeSeconds))
            .withPassword(password.toCharArray())
            .withDatabase(database)
            .withSsl(withSsl)
            .build();

        client = RedisClient.create(uri);
        if (clientOptions != null) {
            client.setOptions(clientOptions);
        }
        connection = client.connect(this.getCodec(classOfK, classOfV));
        commands = connection.sync();
        expireLengthSeconds = expTimeSeconds;
    }

    public RedisCache(String host, int port, int expTimeSeconds, int database,
                      Class<K> classOfK, Class<V> classOfV) {
        this(host, port, expTimeSeconds, database, null, classOfK, classOfV);
    }

    public RedisCache(String host, int port, String password, int expTimeSeconds, int database,
                      Class<K> classOfK, Class<V> classOfV) {
        this(host, port, password, expTimeSeconds, database, null, classOfK, classOfV);
    }

    public RedisCache(String host, int port, String password, int expTimeSeconds, int database, boolean withSsl,
        Class<K> classOfK, Class<V> classOfV) {
        this(host, port, password, expTimeSeconds, database, withSsl, null, classOfK, classOfV);
    }

    public RedisCache(String host, int port, int expTimeSeconds, Class<K> classOfK, Class<V> classOfV) {
        this(host, port, expTimeSeconds, 0, classOfK, classOfV);
    }

    public RedisCache(String host, int port, int expTimeSeconds, ClientOptions clientOptions, Class<K> classOfK, Class<V> classOfV) {
        this(host, port, expTimeSeconds, 0, clientOptions, classOfK, classOfV);
    }

    public RedisCache(String host, int port, String password, int expTimeSeconds, Class<K> classOfK, Class<V> classOfV) {
        this(host, port, password, expTimeSeconds, 0, classOfK, classOfV);
    }

    public RedisCache(String host, int port, String password, int expTimeSeconds, boolean withSsl,
        Class<K> classOfK, Class<V> classOfV) {
        this(host, port, password, expTimeSeconds, 0, withSsl, null, classOfK, classOfV);
    }

    public RedisCache(String host, int port, String password, int expTimeSeconds, boolean withSsl, ClientOptions clientOptions,
        Class<K> classOfK, Class<V> classOfV) {
        this(host, port, password, expTimeSeconds, 0, withSsl, clientOptions, classOfK, classOfV);
    }

    @Override
    public void put(K key, V value) {
        try {
            SetArgs args = new SetArgs();
            args.ex(expireLengthSeconds);
            commands.set(key, value, args);
        } catch (RedisException e) {
            logErrorMessage(e);
        }
    }

    /**
     * Puts entry in cache with ttl measured in milliseconds
     */
    @Override
    public void put(K key, long ttl, V value) {
        try {
            SetArgs args = new SetArgs();
            args.px(ttl);
            commands.set(key, value, args);
        } catch (RedisException e){
            logErrorMessage(e);
        }
    }

    @Override
    public V get(K key) {
        V value = null;
        try {
            value = commands.get(key);
        } catch (RedisException e){
            logErrorMessage(e);
        }
        return value;
    }

    @Override
    public void delete(K key) {
        try {
            commands.del(key);
        } catch (RedisException e){
            logErrorMessage(e);
        }
    }

    @Override
    public void close() {
        if (connection != null)
            connection.close();
        if (client != null)
            client.shutdown();
    }

    @Override
    public void clearAll() {
        try {
            this.commands.flushdb();
        } catch (RedisException e){
            logErrorMessage(e);
        }
    }

    /**
     * Updates a key's ttl in milliseconds
     */
    @Override
    public boolean updateTtl(K key, long ttl) {
        boolean isUpdate = false;
        try {
            isUpdate = commands.pexpire(key, ttl);
        } catch (RedisException e){
            logErrorMessage(e);
        }
        return isUpdate;
    }

    /**
     * Gets the ttl for a key in milliseconds
     */
    @Override
    public Long getTtl(K key) {
        Long ttl = -1L;
        try {
            ttl = commands.pttl(key);
        } catch (RedisException e){
            logErrorMessage(e);
        }
        return ttl;
    }

    /**
     * Gets redis INFO
     */
    @Override
    public String info() {
        String info = null;
        try {
            info = commands.info();
        } catch (RedisException e){
            logErrorMessage(e);
        }
        return info;
    }

    /**
     * Increment the integer value of a key by one
     */
    @Override
    public Long increment(K key) {
        return this.incrementBy(key, 1L);
    }

    /**
     * Increment the integer value of a key by the given amount
     */
    @Override
    public Long incrementBy(K key, long amount) {
        Long increment = -2L;
        try {
            increment = commands.incrby(key, amount);
        } catch (RedisException e){
            logErrorMessage(e);
        }
        return increment;
    }

    /**
     * Decrement the integer value of a key by one
     */
    @Override
    public Long decrement(K key) {
        return this.decrementBy(key, 1L);
    }

    /**
     * Decrement the integer value of a key by the given amount
     */
    @Override
    public Long decrementBy(K key, long amount) {
        Long decrement = -2L;
        try {
            decrement = commands.decrby(key, amount);
        } catch (RedisException e){
            logErrorMessage(e);
        }
        return decrement;
    }

    @Override
    public RedisCodec<K, V> getCodec(Class<K> classOfK, Class<V> classOfV) {
        return CompressionCodec.valueCompressor(new JsonCodec<>(classOfK, classOfV), CompressionCodec.CompressionType.GZIP);
    }

    private void logErrorMessage(Exception e) {
        StringBuilder errorMessage = new StringBuilder("Redis does not work.");
        if (StringUtils.isNotBlank(e.getMessage())) {
            errorMessage.append(" Reason: ").append(e.getMessage());
        }
        log.error(errorMessage.toString());
    }
}