package org.opengroup.osdu.core.common.model.http;

import org.junit.Test;
import org.opengroup.osdu.core.common.exception.CoreException;

import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

public class CollaborationContextTest {

    private static final String COLLABORATION_APPLICATION = "application";
    private UUID COLLABORATION_ID = UUID.randomUUID();

    CollaborationContext collaborationContext = CollaborationContext.builder().id(COLLABORATION_ID).application(COLLABORATION_APPLICATION).build();

    @Test
    public void should_returnTrue_when_collaborationIdProvided(){
        assertEquals(true, collaborationContext.hasId());
    }

    @Test
    public void should_returnFalse_when_collaborationIdNotProvided(){
        collaborationContext.setId(null);
        assertThrows(CoreException.class, () -> {
            throw new CoreException("Collaboration id cannot be null");
        });
    }

}
