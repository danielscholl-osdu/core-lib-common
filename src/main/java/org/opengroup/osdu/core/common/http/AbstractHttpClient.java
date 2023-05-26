// Copyright 2017-2019, Schlumberger
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

package org.opengroup.osdu.core.common.http;

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

abstract class AbstractHttpClient implements IHttpClient {

    @Override
    public HttpResponse send(HttpRequest request) {

        HttpResponse output = new HttpResponse();
        output.setRequest(request);
        HttpURLConnection conn = null;
        try {
            supportPatchMethod();
            request.setUrl(encodeUrl(request.getUrl()));

            long start = System.currentTimeMillis();
            conn = this.createConnection(request);
            this.sendRequest(conn, request.body);

            output.setResponseCode(conn.getResponseCode());
            output.setContentType(conn.getContentType());
            output.setHeaders(conn.getHeaderFields());

            if (output.isSuccessCode()) {
                output.setBody(getBody(conn.getInputStream()));

            } else {
                output.setBody(getBody(conn.getErrorStream()));
            }

            output.setLatency(System.currentTimeMillis() - start);
        } catch (IOException e) {
            System.err.println(String.format("Unexpected error sending to URL %s METHOD %s. error %s", request.url,
                    request.httpMethod, e));
            output.setException(e);
        } catch (URISyntaxException e) {
            output.setException(e);
        } finally {
            if (conn != null)
                conn.disconnect();
        }

        return output;
    }

    private String getBody(InputStream stream) throws IOException {
        if(stream == null) {
            return "";
        }
        try (BufferedReader in = new BufferedReader(new InputStreamReader(stream))) {
            String inputLine;
            StringBuilder resp = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                resp.append(inputLine);
            }
            return resp.toString();
        }
    }

    HttpURLConnection createConnection(HttpRequest request)
            throws IOException {

        HttpURLConnection conn = null;

        URL url = new URL(request.url);
        conn = (HttpURLConnection) url.openConnection();
        conn.setInstanceFollowRedirects(request.followRedirects);
        conn.setConnectTimeout(request.connectionTimeout);

        for (Map.Entry<String, String> header : request.headers.entrySet()) {
            if (header.getKey() == "Content-Length") {
                conn.setFixedLengthStreamingMode(Long.parseLong(header.getValue()));
            } else {
                conn.setRequestProperty(header.getKey(), header.getValue());
            }
        }

        if (request.httpMethod.equals(HttpRequest.POST) ||
                request.httpMethod.equals(HttpRequest.PUT) ||
                request.httpMethod.equals(HttpRequest.PATCH)) {
            conn.setDoOutput(true); //only set if we have a body on request
        }
        conn.setRequestMethod(request.httpMethod);

        return conn;
    }

    private void sendRequest(HttpURLConnection connection, String body) throws IOException {
        if (!StringUtils.isBlank(body)) {
            try (OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream())) {
                writer.write(body);
            }
        }
    }

    private String encodeUrl(String url) throws MalformedURLException, URISyntaxException {
        UriComponents uriComponents = UriComponentsBuilder.fromUriString(url).build();
        return uriComponents.toUriString();
    }

    private void supportPatchMethod() {
        try {
            Field methodsField = HttpURLConnection.class.getDeclaredField("methods");
            Field modifiersField = Field.class.getDeclaredField("modifiers");
            modifiersField.setAccessible(true);
            modifiersField.setInt(methodsField, methodsField.getModifiers() & ~Modifier.FINAL);
            methodsField.setAccessible(true);
            String[] oldMethods = (String[]) methodsField.get(null);
            Set<String> methodsSet = new LinkedHashSet<>(Arrays.asList(oldMethods));
            methodsSet.addAll(Arrays.asList(HttpRequest.PATCH));
            String[] newMethods = methodsSet.toArray(new String[0]);
            methodsField.set(null, newMethods);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new IllegalStateException(e);
        }
    }
}
