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

package org.opengroup.osdu.core.common.model.legal.serialization;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.sql.Date;
import java.time.LocalDate;

import org.opengroup.osdu.core.common.model.legal.Properties;

public class ExpirationDateDeserializer extends StdDeserializer<Date> {
    public ExpirationDateDeserializer() {
        super(Date.class);
    }

    @Override
    public Date deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        final JsonNode expirationDate = jsonParser.getCodec().readTree(jsonParser);
        if (expirationDate == null || StringUtils.isBlank(expirationDate.asText())) {
            return Properties.DEFAULT_EXPIRATIONDATE;
        }
        if (expirationDate.isLong()) {
            return new Date(expirationDate.asLong());
        }
        return Date.valueOf(LocalDate.parse(expirationDate.asText()));
    }
}