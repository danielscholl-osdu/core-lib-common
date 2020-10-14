package org.opengroup.osdu.core.common.model.search;

import org.junit.Test;
import org.mockito.Mock;
import org.opengroup.osdu.core.common.http.HttpResponse;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

public class SearchExceptionTest {
    @Mock
    HttpResponse httpResponse;

    @Test
    public void constructorTest() {
        SearchException exception = new SearchException("this error occurred:", httpResponse);
        assertNotNull(exception);
        assertEquals("this error occurred:", exception.getMessage());
    }
}