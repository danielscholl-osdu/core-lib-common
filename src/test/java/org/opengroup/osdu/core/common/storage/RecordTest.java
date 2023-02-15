// CopyEcoright © 2021 Amazon Web Services
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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.opengroup.osdu.core.common.model.storage.Record;

public class RecordTest {

    @Test
    public void createNewRecordIdTest() {

        String tenant = "test-tenant";
        String kind = "test-tenant:wks:work-product-component--wellLog:1.0.0";        

        Record r = new Record();

        r.createNewRecordId(tenant, kind);

        assertTrue(Record.isRecordIdValid(r.getId(), tenant, kind));
        
    }

    @Test
    public void isRecordIdValidFormatAndTenantTest() {

        String tenant = "test-tenant";
        String kindSubType = "work-product-component--wellLog";
        String uid = "aaaaabbbbccccdddd";

         //Should be valid if formatted properly
         String recordId = String.format("%s:%s:%s", tenant, kindSubType, uid);
         assertTrue(Record.isRecordIdValidFormatAndTenant(recordId, tenant));

         //Should not be valid if missing uid
         String badRecordId1 = String.format("%s:%s", tenant, kindSubType);
         assertFalse(Record.isRecordIdValidFormatAndTenant(badRecordId1, tenant));
 
         //Should not be valid if doesnt match id format
         String badRecordId2 = "garbage";
         assertFalse(Record.isRecordIdValidFormatAndTenant(badRecordId2, tenant));

    }

    @Test
    public void isRecordIdValidTest() {

        String tenant = "test-tenant";
        String kindSubType = "work-product-component--wellLog";
        String kind = String.format("test-tenant:wks:%s:1.0.0", kindSubType);
        String uid = "aaaaabbbbccccdddd";

        //Should be valid if formatted properly
        String recordId = String.format("%s:%s:%s", tenant, kindSubType, uid);
        assertTrue(Record.isRecordIdValid(recordId, tenant, kind));

        //Should not be valid if missing uid
        String badRecordId1 = String.format("%s:%s", tenant, kindSubType);
        assertFalse(Record.isRecordIdValid(badRecordId1, tenant, kind));

        //Should not be valid if doesnt match id format
        String badRecordId2 = "garbage";
        assertFalse(Record.isRecordIdValid(badRecordId2, tenant, kind));

    }

 
    
}
