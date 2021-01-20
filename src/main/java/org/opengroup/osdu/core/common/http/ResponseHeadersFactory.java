package org.opengroup.osdu.core.common.http;

import org.opengroup.osdu.core.common.model.http.DpsHeaders;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class ResponseHeadersFactory {
    // If env var is not set, defaults to *
    @Value("${ACCESS_CONTROL_ALLOW_ORIGIN_DOMAINS:*}")
    String ACCESS_CONTROL_ALLOW_ORIGIN_DOMAINS;

    public Map<String, String> getResponseHeaders(){
        Map<String, String> responseHeaders = new HashMap<>();
        responseHeaders.put("Access-Control-Allow-Origin", ACCESS_CONTROL_ALLOW_ORIGIN_DOMAINS);
        responseHeaders.put("Access-Control-Allow-Credentials", "true");
        String httpMethods = String.format("%s, %s, %s, %s, %s",
                HttpRequest.PATCH, HttpRequest.POST, HttpRequest.PUT, HttpRequest.GET, HttpRequest.DELETE, HttpRequest.HEAD);
        responseHeaders.put("Access-Control-Allow-Methods", httpMethods);
        responseHeaders.put("X-Frame-Options", "DENY");
        responseHeaders.put("X-XSS-Protection", "1; mode=block");
        responseHeaders.put("X-Content-Type-Options", "nosniff");
        responseHeaders.put("Cache-Control", "no-cache, no-store, must-revalidate");
        responseHeaders.put("Content-Security-Policy", "default-src 'self'");
        responseHeaders.put("Strict-Transport-Security", "max-age=31536000; includeSubDomains");
        responseHeaders.put("Expires", "0");
        responseHeaders.put("Access-Control-Max-Age", "3600");
        String allowedHeaders = String.format("%s, %s, %s, %s, %s, %s, %s, %s, %s",
                DpsHeaders.ACCOUNT_ID, DpsHeaders.ON_BEHALF_OF, DpsHeaders.CORRELATION_ID,
                DpsHeaders.DATA_PARTITION_ID, DpsHeaders.USER_EMAIL, DpsHeaders.AUTHORIZATION, DpsHeaders.CONTENT_TYPE,
                DpsHeaders.PRIMARY_PARTITION_ID, DpsHeaders.FRAME_OF_REFERENCE);
        responseHeaders.put("Access-Control-Allow-Headers", allowedHeaders);
        return responseHeaders;
    }
}
