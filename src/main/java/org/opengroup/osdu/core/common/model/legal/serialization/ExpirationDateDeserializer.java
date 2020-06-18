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