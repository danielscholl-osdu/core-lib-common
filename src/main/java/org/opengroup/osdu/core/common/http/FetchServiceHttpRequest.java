package org.opengroup.osdu.core.common.http;

import org.opengroup.osdu.core.common.model.http.DpsHeaders;

import java.util.Map;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FetchServiceHttpRequest {
    private String httpMethod;
    private String url;
    private String body;
    private Map<String, String> queryParams;
    private DpsHeaders headers;

    public static class FetchServiceHttpRequestBuilder {

        private DpsHeaders headers;

        public FetchServiceHttpRequestBuilder headers(Map<String, String> headers) {
            this.headers = DpsHeaders.createFromMap(headers);
            return this;
        }

        public FetchServiceHttpRequestBuilder headers(DpsHeaders headers) {
            this.headers = headers;
            return this;
        }
    }
}
