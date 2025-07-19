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

package org.opengroup.osdu.core.common.crs;

import org.junit.Ignore;
import org.opengroup.osdu.core.common.model.http.DpsHeaders;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Ignore
public class TestUtils {
    public static String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    private static String token = "";


    public static DpsHeaders getStandardHeaders(String tenant) {
        DpsHeaders headers = new DpsHeaders();
        //headers.put(DpsHeaders.AUTHORIZATION, getAuthToken());
        headers.put(DpsHeaders.ACCOUNT_ID, tenant);
        return headers;
    }

    public static boolean isEqual(double a, double b) {
        if (Double.isNaN(a) || Double.isNaN(b))
            return false;

        return Math.abs(a - b) <= Double.MIN_VALUE;
    }

    public static boolean isNullOrEmpty(String value) {
        return value == null || value.trim().isEmpty();
    }

    public static String readFile(String path) throws IOException {
        InputStream inputStream = TestUtils.class.getClass().getResourceAsStream(path);
        if (inputStream == null) {
            throw new IOException();
        }
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length;
        while ((length = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, length);
        }
        return outputStream.toString(StandardCharsets.UTF_8.toString());
    }
}