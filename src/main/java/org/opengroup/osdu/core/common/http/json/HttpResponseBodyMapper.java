package org.opengroup.osdu.core.common.http.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.opengroup.osdu.core.common.http.HttpResponse;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class HttpResponseBodyMapper {

    private final ObjectMapper mapper;

    public <T> T parseBody(HttpResponse response, Class<T> type) throws HttpResponseBodyParsingException {
        String body = response.getBody();
        if (StringUtils.isBlank(body)) {
            return null;
        }

        try {
            return mapper.readValue(body, type);
        } catch (JsonProcessingException e) {
            throw new HttpResponseBodyParsingException(e);
        }
    }

    public JsonNode parseBody(HttpResponse response) throws HttpResponseBodyParsingException {
        String body = response.getBody();
        if (StringUtils.isBlank(body)) {
            return null;
        }

        try {
            return mapper.readTree(body);
        } catch (JsonProcessingException e) {
            throw new HttpResponseBodyParsingException(e);
        }
    }
}
