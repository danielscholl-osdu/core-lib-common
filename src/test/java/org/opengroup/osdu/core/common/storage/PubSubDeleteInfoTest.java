// Copyright 2022, Schlumberger
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
