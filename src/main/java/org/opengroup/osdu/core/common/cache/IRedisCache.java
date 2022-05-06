/*
 * Copyright 2022 Microsoft Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.opengroup.osdu.core.common.cache;

import com.lambdaworks.redis.codec.RedisCodec;

/**
 * Interface that extends ICache with Redis specific functionalities.
 * @param <K>
 * @param <O>
 */
public interface IRedisCache<K, O> extends ICache<K, O> {

    /**
     * Puts entry in cache with ttl measured in milliseconds
     */
    void put(K key, long ttl, O value);
    /**
     * Updates a key's ttl in milliseconds
     */
    boolean updateTtl(K key, long ttl);

    /**
     * Gets the ttl for a key in milliseconds
     */
    long getTtl(K key);

    /**
     * Gets redis INFO
     */
    String info();

    /**
     * Increment the integer value of a key by one
     */
    Long increment(K key);

    /**
     * Increment the integer value of a key by the given amount
     */
    Long incrementBy(K key, long amount);

    /**
     * Decrement the integer value of a key by one
     */
    Long decrement(K key);

    /**
     * Decrement the integer value of a key by the given amount
     */
    Long decrementBy(K key, long amount);

    /**
     * Get codec for performing encoding and decoding of key and values present in redis cache
     */
    RedisCodec<K, O> getCodec(Class<K> classOfK, Class<O> classOfO);
}
