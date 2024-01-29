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

package org.opengroup.osdu.core.common.model.storage.validation;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.opengroup.osdu.core.common.model.storage.RecordAncestry;
import org.opengroup.osdu.core.common.model.storage.RecordQuery;

import javax.validation.ConstraintValidatorContext;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class RecordAncestryValidatorTest {
    private final String parentRecordId = "tenant:test:parentRecord:123";
    private final String invalidFormatParentRecord = "tenanttestrecord";
    private final String invalidFormatVersionParentRecord = "tenanttestrecord:version";
    private final String validParentRecord = "tenant:test:record:456";

    @Mock
    private ConstraintValidatorContext context;

    private RecordAncestryValidator sut;

    @Before
    public void setup() {
        this.sut = new RecordAncestryValidator();

        ConstraintValidatorContext.ConstraintViolationBuilder builder = mock(ConstraintValidatorContext.ConstraintViolationBuilder.class);

        when(this.context.buildConstraintViolationWithTemplate(anyString()))
                .thenReturn(builder);
    }

    @Test
    public void should_doNothingInInitialize() {
        // for coverage purposes. Do nothing method!
        this.sut.initialize(null);
    }

    @Test
    public void should_returnFalse_ifWrongFormatParentRecords() {
        RecordAncestry recordAncestry = new RecordAncestry();
        Set<String> parents = new HashSet<>();
        parents.add(invalidFormatParentRecord);
        recordAncestry.setParents(parents);

        assertFalse(this.sut.isValid(recordAncestry, this.context));
    }

    @Test
    public void should_returnFalse_ifnullParentRecords() {
        RecordAncestry recordAncestry = new RecordAncestry();

        assertFalse(this.sut.isValid(recordAncestry, this.context));
    }

    @Test
    public void should_returnTure_ifnullRecordAncestry() {
        RecordAncestry recordAncestry = null;

        assertTrue(this.sut.isValid(recordAncestry, this.context));
    }

    @Test
    public void should_returnFalse_ifWrongFormatVersionParentRecords() {
        RecordAncestry recordAncestry = new RecordAncestry();
        Set<String> parents = new HashSet<>();
        parents.add(invalidFormatVersionParentRecord);
        recordAncestry.setParents(parents);

        assertFalse(this.sut.isValid(recordAncestry, this.context));
    }

    @Test
    public void should_returnTrue_ifValidRecord() {
        RecordAncestry recordAncestry = new RecordAncestry();
        Set<String> parents = new HashSet<>();
        parents.add(validParentRecord);
        recordAncestry.setParents(parents);

        assertTrue(this.sut.isValid(recordAncestry, this.context));
    }
}
