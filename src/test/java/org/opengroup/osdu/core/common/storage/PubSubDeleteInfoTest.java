package org.opengroup.osdu.core.common.storage;

import org.junit.Assert;
import org.junit.Test;
import org.opengroup.osdu.core.common.model.indexer.DeletionType;
import org.opengroup.osdu.core.common.model.indexer.OperationType;
import org.opengroup.osdu.core.common.model.storage.PubSubDeleteInfo;

public class PubSubDeleteInfoTest {

    private static String recordId = "test-recordId";
    private static String kind = "test-tenant:wks:work-product-component--wellLog:1.0.0";
    private PubSubDeleteInfo pubSubDeleteInfo;

    @Test
    public void testPubSubDeleteInfoWithDeletionTypeSoft() throws Exception {
        pubSubDeleteInfo = new PubSubDeleteInfo(recordId, kind, DeletionType.soft);
        Assert.assertEquals(DeletionType.soft.getValue(), pubSubDeleteInfo.getDeletionType().getValue());
        Assert.assertEquals(kind, pubSubDeleteInfo.getKind());
        Assert.assertEquals(recordId, pubSubDeleteInfo.getId());
        Assert.assertEquals(OperationType.delete.getValue(), pubSubDeleteInfo.getOp().getValue());
    }

    @Test
    public void testPubSubDeleteInfoWithDeletionTypeHard() throws Exception {
        pubSubDeleteInfo = new PubSubDeleteInfo(recordId, kind, DeletionType.hard);
        Assert.assertEquals(DeletionType.hard.getValue(), pubSubDeleteInfo.getDeletionType().getValue());
        Assert.assertEquals(kind, pubSubDeleteInfo.getKind());
        Assert.assertEquals(recordId, pubSubDeleteInfo.getId());
        Assert.assertEquals(OperationType.delete.getValue(), pubSubDeleteInfo.getOp().getValue());
    }

}
