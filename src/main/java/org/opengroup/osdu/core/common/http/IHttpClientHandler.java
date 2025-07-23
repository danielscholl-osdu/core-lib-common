package org.opengroup.osdu.core.common.http;

import org.apache.http.client.methods.HttpRequestBase;
import org.opengroup.osdu.core.common.model.http.DpsHeaders;
import org.opengroup.osdu.core.common.model.http.HttpResponse;

public interface IHttpClientHandler {
    /**
     * Sends an HTTP request. When the isIdempotent flag is not passed, we assume false by default.
     * 
     * @param request the HTTP request to send
     * @param requestHeaders the headers to include with the request
     * @return the HTTP response
     */
    HttpResponse sendRequest(HttpRequestBase request, DpsHeaders requestHeaders);
    
    /**
     * Sends an HTTP request with idempotency flag.
     * 
     * @param request the HTTP request to send
     * @param requestHeaders the headers to include with the request
     * @param isIdempotent whether the request is idempotent (affects retry behavior)
     * @return the HTTP response
     */
    default HttpResponse sendRequest(HttpRequestBase request, DpsHeaders requestHeaders, boolean isIdempotent) {
        // Default implementation delegates to the original method, ignoring isIdempotent
        return sendRequest(request, requestHeaders);
    }
}
