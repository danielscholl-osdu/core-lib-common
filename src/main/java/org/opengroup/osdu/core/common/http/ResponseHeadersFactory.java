package org.opengroup.osdu.core.common.http;

import org.opengroup.osdu.core.common.model.http.DpsHeaders;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ResponseHeadersFactory {
    public Map<String, String> getResponseHeaders(String commaDelimitedDomains){
        Map<String, String> responseHeaders = new HashMap<>();
        responseHeaders.put("Access-Control-Allow-Origin", commaDelimitedDomains);
        responseHeaders.put("Access-Control-Allow-Credentials", "true");
        responseHeaders.put("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD, PATCH");
        responseHeaders.put("X-Frame-Options", "DENY");
        responseHeaders.put("X-XSS-Protection", "1; mode=block");
        responseHeaders.put("X-Content-Type-Options", "nosniff");
        responseHeaders.put("Cache-Control", "no-cache, no-store, must-revalidate");
        responseHeaders.put("Content-Security-Policy", "default-src 'self'");
        responseHeaders.put("Strict-Transport-Security", "max-age=31536000; includeSubDomains");
        responseHeaders.put("Expires", "0");
        responseHeaders.put("Access-Control-Max-Age", "3600");
        responseHeaders.put("Access-Control-Allow-Headers", "access-control-allow-origin, origin, content-type, accept, authorization, data-partition-id, correlation-id, appkey");
        return responseHeaders;
    }
}
