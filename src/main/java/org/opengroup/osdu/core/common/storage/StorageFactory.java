package org.opengroup.osdu.core.common.storage;

import org.opengroup.osdu.core.common.http.HttpClient;
import org.opengroup.osdu.core.common.http.json.HttpResponseBodyMapper;
import org.opengroup.osdu.core.common.model.http.DpsHeaders;

public class StorageFactory implements IStorageFactory {
    public StorageFactory(StorageAPIConfig config, HttpResponseBodyMapper bodyMapper) {
        if (config == null) {
            throw new IllegalArgumentException("StorageAPIConfig cannot be empty");
        }
        this.config = config;
        this.bodyMapper = bodyMapper;
    }

    private final StorageAPIConfig config;
    private final HttpResponseBodyMapper bodyMapper;

    @Override
    public IStorageService create(DpsHeaders headers) {
        if (headers == null) {
            throw new NullPointerException("headers cannot be null");
        }
        return new StorageService(this.config, new HttpClient(), headers, bodyMapper);
    }
}
