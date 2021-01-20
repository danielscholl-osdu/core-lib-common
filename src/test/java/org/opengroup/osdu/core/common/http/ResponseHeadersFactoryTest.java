package org.opengroup.osdu.core.common.http;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.util.Map;

import static junit.framework.TestCase.assertEquals;
@RunWith(MockitoJUnitRunner.class)
@PrepareForTest({HttpURLConnection.class, OutputStream.class})
public class ResponseHeadersFactoryTest {

    @InjectMocks
    ResponseHeadersFactory responseHeadersFactory;

    @Test
    public void should_retrieveFullListOfHeaders() {
        // Arrange
        ReflectionTestUtils.setField(responseHeadersFactory, "ACCESS_CONTROL_ALLOW_ORIGIN_DOMAINS", "test-domain");

        // Act
        Map<String, String> responseHeaders = responseHeadersFactory.getResponseHeaders();

        // Assert
        assertEquals(12, responseHeaders.size());
        assertEquals("test-domain", responseHeaders.get("Access-Control-Allow-Origin"));
    }
}
