// Copyright 2017-2020, Schlumberger
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

package org.opengroup.osdu.core.common.partition;

import org.apache.http.HttpEntity;
import org.apache.http.HttpVersion;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicStatusLine;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.opengroup.osdu.core.common.model.http.DpsHeaders;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashMap;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PartitionServiceTest {

    @Mock
    CloseableHttpClient cacheHttpClient;

    public static final String VALID_JSON_RESPONSE = "{ 'endpoint': '1.1.1.1', 'connection': 'test-connection', 'id': 'tenant1'}";
    public static final String PARTITION_ID = "tenant1";
    public static final String PARTITION_NOT_FOUND = "partiton tenant1 not found";
    public static final String BAD_REQUEST = "Bad request";

    @Test
    public void should_return_partition_when_exists() throws PartitionException, IOException {
        PartitionAPIConfig config = PartitionAPIConfig.builder().rootUrl("http://localhost").build();
        DpsHeaders headers = new DpsHeaders();
        CloseableHttpResponse mockResponse = getResponse(200, VALID_JSON_RESPONSE);
        when(cacheHttpClient.execute(any())).thenReturn(mockResponse);

        PartitionService sut = new PartitionService(config, headers, cacheHttpClient);
        PartitionInfo partition = sut.get("tenant1");
        assertNotNull(partition);
        assertNotNull(partition.getProperties());
        assertEquals(3, partition.properties.size());
        assertEquals(PARTITION_ID, partition.properties.get("id"));
    }

    @Test
    public void should_return_exception_when_partition_doesnt_exists() throws IOException {
        PartitionAPIConfig config = PartitionAPIConfig.builder().rootUrl("http://localhost").build();
        DpsHeaders headers = new DpsHeaders();
        CloseableHttpResponse mockResponse = getResponse(404, PARTITION_NOT_FOUND);
        when(cacheHttpClient.execute(any())).thenReturn(mockResponse);
        PartitionService sut = new PartitionService(config, headers, cacheHttpClient);
        try {
            sut.get("tenant1");
            fail("should not be here");
        } catch (PartitionException e) {
            assertNotNull(e);
            assertEquals(PARTITION_NOT_FOUND, e.getMessage());
        }
    }

    @Test
    public void should_return_partition_when_create() throws IOException, PartitionException {
        PartitionAPIConfig config = PartitionAPIConfig.builder().rootUrl("http://localhost").build();
        DpsHeaders headers = new DpsHeaders();
        CloseableHttpResponse mockResponse = getResponse(200, VALID_JSON_RESPONSE);
        when(cacheHttpClient.execute(any())).thenReturn(mockResponse);

        PartitionService sut = new PartitionService(config, headers, cacheHttpClient);
        PartitionInfo input = PartitionInfo.builder()
                .properties(new HashMap<>())
                .build();
        PartitionInfo partition = sut.create(PARTITION_ID, input);
        assertNotNull(partition);
        assertNotNull(partition.getProperties());
        assertEquals(3, partition.properties.size());
        assertEquals(PARTITION_ID, partition.properties.get("id"));
    }

    @Test
    public void should_return_exception_when_creating_with_bad_partition() throws IOException {
        PartitionAPIConfig config = PartitionAPIConfig.builder().rootUrl("http://localhost").build();
        DpsHeaders headers = new DpsHeaders();
        CloseableHttpResponse mockResponse = getResponse(400, BAD_REQUEST);
        when(cacheHttpClient.execute(any())).thenReturn(mockResponse);

        PartitionService sut = new PartitionService(config, headers, cacheHttpClient);
        try {
            sut.create(PARTITION_ID, null);
            fail("should not be here");
        } catch (PartitionException e) {
            assertNotNull(e);
            assertEquals(BAD_REQUEST, e.getMessage());
        }
    }

    @Test
    public void should_return_partition_204_after_deleting() throws IOException, PartitionException {
        PartitionAPIConfig config = PartitionAPIConfig.builder().rootUrl("http://localhost").build();
        DpsHeaders headers = new DpsHeaders();
        CloseableHttpResponse mockResponse = getResponse(204, null);
        when(cacheHttpClient.execute(any())).thenReturn(mockResponse);

        PartitionService sut = new PartitionService(config, headers, cacheHttpClient);
        sut.delete(PARTITION_ID);
    }

    @Test
    public void should_return_exception_when_deleting_not_existing_partition() throws IOException {
        PartitionAPIConfig config = PartitionAPIConfig.builder().rootUrl("http://localhost").build();
        DpsHeaders headers = new DpsHeaders();
        CloseableHttpResponse mockResponse = getResponse(404, PARTITION_NOT_FOUND);
        when(cacheHttpClient.execute(any())).thenReturn(mockResponse);

        PartitionService sut = new PartitionService(config, headers, cacheHttpClient);
        try {
            sut.delete(PARTITION_ID);
            fail("should not be here");
        } catch (PartitionException e) {
            assertNotNull(e);
            assertEquals(PARTITION_NOT_FOUND, e.getMessage());
        }
    }


    private CloseableHttpResponse getResponse(int status, String body) throws IOException {
        CloseableHttpResponse response = mock(CloseableHttpResponse.class);
        HttpEntity entity = mock(HttpEntity.class);
        when(response.getStatusLine()).thenReturn(new BasicStatusLine(HttpVersion.HTTP_1_1, status, "OK"));
        if (body != null) {
            when(entity.getContent()).thenReturn(new ByteArrayInputStream(body.getBytes()));
        }
        when(response.getEntity()).thenReturn(entity);
        return response;
    }
}
