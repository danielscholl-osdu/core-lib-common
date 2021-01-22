// Copyright 2021 Schlumberger
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

package org.opengroup.osdu.core.common.storage;

import org.opengroup.osdu.core.common.http.HttpRequest;
import org.opengroup.osdu.core.common.http.HttpResponse;
import org.opengroup.osdu.core.common.http.IHttpClient;
import org.opengroup.osdu.core.common.http.json.HttpResponseBodyMapper;
import org.opengroup.osdu.core.common.http.json.HttpResponseBodyParsingException;
import org.opengroup.osdu.core.common.model.http.DpsHeaders;
import org.opengroup.osdu.core.common.model.storage.*;

import java.util.Collection;
import org.opengroup.osdu.core.common.util.UrlNormalizationUtil;

public class StorageService implements IStorageService {

    private final String rootUrl;
    private final IHttpClient httpClient;
    private final DpsHeaders headers;
    private final HttpResponseBodyMapper bodyMapper;

    StorageService(StorageAPIConfig config,
                   IHttpClient httpClient,
                   DpsHeaders headers,
                   HttpResponseBodyMapper bodyMapper) {
        this.rootUrl = config.getRootUrl();
        this.httpClient = httpClient;
        this.headers = headers;
        this.bodyMapper = bodyMapper;
        if (config.apiKey != null) {
            headers.put("AppKey", config.getApiKey());
        }
    }

    @Override
    public UpsertRecords upsertRecord(Record record) throws StorageException {
        Record[] records = new Record[1];
        records[0] = record;
        return this.upsertRecord(records);
    }

    @Override
    public UpsertRecords upsertRecord(Record[] records) throws StorageException {
        String url = this.createUrl("/records");
        HttpResponse result = this.httpClient.send(
                HttpRequest.put(records).url(url).headers(this.headers.getHeaders()).build());
        return this.getResult(result, UpsertRecords.class);
    }

    @Override
    public Record getRecord(String id) throws StorageException {
        String url = this.createUrl(String.format("/records/%s", id));
        HttpResponse result = this.httpClient.send(
                HttpRequest.get().url(url).headers(this.headers.getHeaders()).build());
        return result.IsNotFoundCode() ? null : this.getResult(result, Record.class);
    }

    @Override
    public MultiRecordInfo getRecords(Collection<String> ids) throws StorageException {
        MultiRecordIds input = new MultiRecordIds();
        input.getRecords().addAll(ids);
        String url = this.createUrl("/query/records");
        HttpResponse result = this.httpClient.send(
                HttpRequest.post(input).url(url).headers(this.headers.getHeaders()).build());
        return result.IsNotFoundCode() ? null : this.getResult(result, MultiRecordInfo.class);
    }

    @Override
    public void deleteRecord(String id) throws StorageException {
        String url = this.createUrl(String.format("/records/%s", id));
        HttpResponse result = this.httpClient.send(
                HttpRequest.delete().url(url).headers(this.headers.getHeaders()).build());
        this.getResult(result, String.class);
    }

    @Override
    public Schema createSchema(Schema schema) throws StorageException {
        String url = this.createUrl("/schemas");
        HttpResponse result = this.httpClient.send(
                HttpRequest.post(schema).url(url).headers(this.headers.getHeaders()).build());
        this.getResult(result, String.class);
        return schema;
    }

    @Override
    public Schema getSchema(String kind) throws StorageException {
        String url = this.createUrl(String.format("/schemas/%s", kind));
        HttpResponse result = this.httpClient.send(
                HttpRequest.get().url(url).headers(this.headers.getHeaders()).build());
        return result.IsNotFoundCode() ? null : this.getResult(result, Schema.class);
    }

    @Override
    public void deleteSchema(String kind) throws StorageException {
        String url = this.createUrl(String.format("/schemas/%s", kind));
        HttpResponse result = this.httpClient.send(
                HttpRequest.delete().url(url).headers(this.headers.getHeaders()).build());
        this.getResult(result, String.class);
    }

    private StorageException generateException(HttpResponse result) {
        return new StorageException(
                "Error making request to Storage service. Check the inner HttpResponse for more info.", result);
    }

    private String createUrl(String pathAndQuery) {
        return UrlNormalizationUtil.normalizeStringUrl(this.rootUrl,pathAndQuery);
    }

    private <T> T getResult(HttpResponse result, Class<T> type) throws StorageException {
        if (result.isSuccessCode()) {
            try {
                return bodyMapper.parseBody(result, type);
            } catch (HttpResponseBodyParsingException e) {
                throw new StorageException("Error parsing response. Check the inner HttpResponse for more info.",
                        result);
            }
        } else {
            throw this.generateException(result);
        }
    }
}
