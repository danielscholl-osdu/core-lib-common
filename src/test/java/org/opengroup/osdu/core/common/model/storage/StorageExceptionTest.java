package org.opengroup.osdu.core.common.model.storage;

import org.junit.Test;
import org.mockito.Mock;
import org.opengroup.osdu.core.common.http.HttpResponse;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class StorageExceptionTest {
    @Mock
    HttpResponse httpResponse;

    @Test
    public void constructorTest() {
        StorageException exception = new StorageException("this error occurred:", httpResponse);
        assertNotNull(exception);
        assertEquals("this error occurred:", exception.getMessage());
    }
}