package org.opengroup.osdu.core.common.storage;

import org.opengroup.osdu.core.common.http.HttpClient;
import org.opengroup.osdu.core.common.model.http.DpsHeaders;

public class StorageFactory implements IStorageFactory {
    public StorageFactory(StorageAPIConfig config) {
        if (config == null) {
            throw new IllegalArgumentException("StorageAPIConfig cannot be empty");
        }
        this.config = config;
    }

    private final StorageAPIConfig config;

    @Override
    public IStorageService create(DpsHeaders headers) {
        if (headers == null) {
            throw new NullPointerException("headers cannot be null");
        }
        return new StorageService(this.config, new HttpClient(), headers);
    }
}
