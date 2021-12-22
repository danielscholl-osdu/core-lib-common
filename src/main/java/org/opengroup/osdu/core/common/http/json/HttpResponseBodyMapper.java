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
