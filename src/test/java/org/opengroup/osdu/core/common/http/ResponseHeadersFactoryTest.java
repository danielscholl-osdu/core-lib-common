package org.opengroup.osdu.core.common.http;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.powermock.core.classloader.annotations.PrepareForTest;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static junit.framework.TestCase.assertEquals;
@RunWith(MockitoJUnitRunner.class)
@PrepareForTest({HttpURLConnection.class, OutputStream.class})
public class ResponseHeadersFactoryTest {
    @Test
    public void should_retrieveFullListOfHeaders() {
        // Arrange
        String domains = "test-domain,test-domain2";
        ResponseHeadersFactory responseHeadersFactory = new ResponseHeadersFactory();

        // Act
        Map<String, String> responseHeaders = responseHeadersFactory.getResponseHeaders(domains);

        // Assert
        assertEquals(12, responseHeaders.size());
        assertEquals("test-domain,test-domain2", responseHeaders.get("Access-Control-Allow-Origin"));
    }
}
