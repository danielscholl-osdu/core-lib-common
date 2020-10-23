package org.opengroup.osdu.core.common.http;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opengroup.osdu.core.common.http.json.HttpResponseBodyMapper;
import org.opengroup.osdu.core.common.http.json.HttpResponseBodyParsingException;
import org.opengroup.osdu.core.common.model.legal.LegalTag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.sql.Date;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {HttpConfiguration.class, HttpResponseBodyMapper.class})
public class HttpResponseBodyMapperTest {

    @Autowired
    private HttpResponseBodyMapper mapper;

    @Test
    public void parseBody_shouldReturnParsedJson_whenValidJson() throws Exception {
        HttpResponse response = new HttpResponse();
        response.setBody("{\"name\":\"test data\"}");

        MockModel model = mapper.parseBody(response, MockModel.class);

        assertThat(model, notNullValue());
        assertThat(model.getName(), is("test data"));
    }

    @Test
    public void parseBody_shouldReturnNull_whenNullBody() throws Exception {
        HttpResponse response = new HttpResponse();

        MockModel model = mapper.parseBody(response, MockModel.class);

        assertThat(model, nullValue());
    }

    @Test(expected = HttpResponseBodyParsingException.class)
    public void parseBody_shouldThrowException_whenInvalidJson() throws Exception {
        HttpResponse response = new HttpResponse();
        response.setBody("name=test data");

        mapper.parseBody(response, MockModel.class);
    }

    @Test
    public void parseBody_shouldReturnParsedJsonNode_whenValidJson() throws Exception {
        HttpResponse response = new HttpResponse();
        response.setBody("{\"name\":\"test data\"}");

        JsonNode node = mapper.parseBody(response);

        assertThat(node.get("name").asText(), is("test data"));
    }

    @Test
    public void parseBody_shouldReturnNullJsonNode_whenEmptyBody() throws Exception {
        HttpResponse response = new HttpResponse();
        response.setBody("");

        JsonNode node = mapper.parseBody(response);

        assertThat(node, nullValue());
    }

    @Test(expected = HttpResponseBodyParsingException.class)
    public void parseBodyToJsonNode_shouldThrowException_whenInvalidJson() throws Exception {
        HttpResponse response = new HttpResponse();
        response.setBody("name=test data");

        mapper.parseBody(response);
    }

    @Test
    public void parseBody_shouldParseLegalTagWithExpirationDate_whenExpirationDate() throws Exception {
        HttpResponse response = new HttpResponse();
        response.setBody("{\n" +
                "    \"name\": \"legaltagName\",\n" +
                "    \"description\": \"\",\n" +
                "    \"properties\": {\n" +
                "        \"countryOfOrigin\": [\n" +
                "            \"US\"\n" +
                "        ],\n" +
                "        \"contractId\": \"A1234\",\n" +
                "        \"expirationDate\": \"2099-12-31\",\n" +
                "        \"originator\": \"OSDU\",\n" +
                "        \"dataType\": \"Third Party Data\",\n" +
                "        \"securityClassification\": \"Public\",\n" +
                "        \"personalData\": \"No Personal Data\",\n" +
                "        \"exportClassification\": \"EAR99\"\n" +
                "    }\n" +
                "}");

        LegalTag tag = mapper.parseBody(response, LegalTag.class);

        assertThat(tag, notNullValue());
        assertThat(tag.getName(), is("legaltagName"));
        assertThat(tag.getProperties(), notNullValue());
        assertThat(tag.getProperties().getExpirationDate(), is(Date.valueOf("2099-12-31")));
    }

    public static class MockModel {
        String name;

        public MockModel() {
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

}