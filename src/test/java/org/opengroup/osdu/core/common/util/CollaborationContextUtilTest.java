package org.opengroup.osdu.core.common.util;

import org.junit.Assert;
import org.junit.Test;
import org.opengroup.osdu.core.common.model.http.CollaborationContext;

import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.junit.Assert.assertEquals;

public class CollaborationContextUtilTest {

    private final static String RECORD_ID = "opendes:id:15706318658560";

    @Test
    public void testGetNamespace_withCollaboration() {
        // Arrange
        final UUID collaborationId = UUID.randomUUID();
        final CollaborationContext collaborationContext = new CollaborationContext(collaborationId, "app", Collections.emptyMap());
        // Act
        final String result = CollaborationContextUtil.getNamespace(Optional.of(collaborationContext));
        // Assert
        Assert.assertEquals(collaborationId.toString(), result);
    }

    @Test
    public void testGetNamespace_withoutCollaboration() {
        // Act
        final String result = CollaborationContextUtil.getNamespace(Optional.empty());
        // Assert
        assertEquals("", result);
    }

    @Test
    public void testComposeIdWithNamespace_withCollaboration() {
        // Arrange
        final UUID collaborationId = UUID.randomUUID();
        final CollaborationContext collaborationContext = new CollaborationContext(collaborationId, "app", Collections.emptyMap());

        // Act
        final String result = CollaborationContextUtil.composeIdWithNamespace(RECORD_ID, Optional.of(collaborationContext));
        // Assert
        assertEquals(collaborationId.toString() + RECORD_ID, result);
    }

    @Test
    public void testComposeIdWithNamespace_withoutCollaboration() {
        // Act
        final String result = CollaborationContextUtil.composeIdWithNamespace(RECORD_ID, Optional.empty());
        // Assert
        assertEquals(RECORD_ID, result);
    }

    @Test
    public void testGetIdWithoutNamespace_withCollaboration() {
        // Arrange
        final UUID collaborationId = UUID.randomUUID();
        final CollaborationContext collaborationContext = new CollaborationContext(collaborationId, "app", Collections.emptyMap());
        final String recordId = collaborationId.toString() + RECORD_ID;
        // Act
        final String result = CollaborationContextUtil.getIdWithoutNamespace(recordId, Optional.of(collaborationContext));
        // Assert
        assertEquals(RECORD_ID, result);

    }

    @Test
    public void testGetIdWithoutNamespace_withNoCollaboration() {
        // Arrange
        final String recordId = UUID.randomUUID() + RECORD_ID;
        // Act
        final String result = CollaborationContextUtil.getIdWithoutNamespace(recordId, Optional.empty());
        // Assert
        assertEquals(recordId, result);
    }

}