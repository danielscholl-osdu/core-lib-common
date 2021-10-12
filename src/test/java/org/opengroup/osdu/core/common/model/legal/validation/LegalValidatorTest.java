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

package org.opengroup.osdu.core.common.model.legal.validation;

import javax.validation.ConstraintValidatorContext;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.common.collect.Sets;

import org.opengroup.osdu.core.common.model.storage.Record;
import org.opengroup.osdu.core.common.model.storage.RecordAncestry;
import org.opengroup.osdu.core.common.model.entitlements.Acl;
import org.opengroup.osdu.core.common.model.legal.Legal;
import org.opengroup.osdu.core.common.model.legal.validation.LegalValidator;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.anyString;


@RunWith(MockitoJUnitRunner.class)
public class LegalValidatorTest {
    private static final String[] VALUES = new String[] { "data.email1@dom.dev.cloud.dom-ds.com",
            "data.test@dom.dev.cloud.dom-ds.com" };

    @Mock
    private ConstraintValidatorContext context;

    private Record record;

    private LegalValidator sut;

    @Before
    public void setup() {
        this.record = new Record();
        this.sut = new LegalValidator();

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
    public void should_notInterpolate_when_parentWithoutVersionHasExpressionLanguage() {

        Legal legal = new Legal();
        legal.setLegaltags(Sets.newHashSet("legal1"));
        legal.setOtherRelevantDataCountries(Sets.newHashSet("FRA"));
        this.record.setLegal(legal);

        RecordAncestry ancestry = new RecordAncestry();
        ancestry.setParents(Sets.newHashSet("${2+2}"));

        this.record.setAncestry(ancestry);

        assertFalse(this.sut.isValid(this.record, this.context));
        verify(this.context).buildConstraintViolationWithTemplate("Invalid parent record format: '\\$\\{2+2\\}'. The following format is expected: {record-id}:{record-version}");
    }

    @Test
    public void should_notInterpolate_when_parentWithInvalidVersionHasExpressionLanguage() {

        Legal legal = new Legal();
        legal.setLegaltags(Sets.newHashSet("legal1"));
        legal.setOtherRelevantDataCountries(Sets.newHashSet("FRA"));
        this.record.setLegal(legal);

        RecordAncestry ancestry = new RecordAncestry();
        ancestry.setParents(Sets.newHashSet("${2+2}:abc:def:xyz"));

        this.record.setAncestry(ancestry);

        assertFalse(this.sut.isValid(this.record, this.context));
        verify(this.context).buildConstraintViolationWithTemplate("Invalid parent record version: '\\$\\{2+2\\}:abc:def:xyz'. Record version must be a numeric value");
    }
}