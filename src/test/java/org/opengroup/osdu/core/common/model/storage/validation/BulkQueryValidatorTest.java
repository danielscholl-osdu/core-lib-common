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

import java.util.ArrayList;
import java.util.List;

import javax.validation.ConstraintValidatorContext;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import org.opengroup.osdu.core.common.model.storage.RecordQuery;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.anyString;

@RunWith(MockitoJUnitRunner.class)
public class BulkQueryValidatorTest {
    private final String recordId = "tenant:test:record:123";
    private final String invalidFormatRecord = "tenant:testrecord";
    private final String validRecord = "tenant:test:record:456";

    @Mock
    private ConstraintValidatorContext context;

    private BulkQueryValidator sut;

    @Before
    public void setup() {
        this.sut = new BulkQueryValidator();

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
    public void should_returnFalse_ifDuplicateRecords() {
        RecordQuery recordQuery = new RecordQuery();
        List<String> ids = new ArrayList<>();
        ids.add(recordId);
        ids.add(recordId);
        recordQuery.setIds(ids);

        assertFalse(this.sut.isValid(recordQuery, this.context));
    }

    @Test
    public void should_returnFalse_ifWrongFormatRecords() {
        RecordQuery recordQuery = new RecordQuery();
        List<String> ids = new ArrayList<>();
        ids.add(invalidFormatRecord);
        recordQuery.setIds(ids);

        assertFalse(this.sut.isValid(recordQuery, this.context));
    }

    @Test
    public void should_returnTrue_ifValidRecord() {
        RecordQuery recordQuery = new RecordQuery();
        List<String> ids = new ArrayList<>();
        ids.add(validRecord);
        recordQuery.setIds(ids);

        assertTrue(this.sut.isValid(recordQuery, this.context));
    }

    @Test
    public void should_notInterpolate_when_recordIdHasExpressionLanguage() {
        RecordQuery recordQuery = new RecordQuery();
        List<String> ids = new ArrayList<>();
        ids.add("${2+2}");
        recordQuery.setIds(ids);
    
        assertFalse(this.sut.isValid(recordQuery, this.context));
        verify(this.context).buildConstraintViolationWithTemplate("Invalid record format: '\\$\\{2+2\\}'. The following format is expected: {tenant-name}:{object-type}:{unique-identifier} or {tenant-name}:{object-type}:{unique-identifier}:{version}");
    }
}