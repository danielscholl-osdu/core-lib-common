
// Copyright 2017-2022, Schlumberger
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

package org.opengroup.osdu.core.common.model.search.validation;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

import javax.validation.ConstraintValidatorContext;
import java.util.ArrayList;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(MockitoJUnitRunner.class)
public class MultiKindValidatorTest {

    private ConstraintValidatorContext context;

    @InjectMocks
    private MultiKindValidator sut;

    @Before
    public  void setup(){
        initMocks(this);

        this.context = mock(ConstraintValidatorContext.class);
        ConstraintValidatorContext.ConstraintViolationBuilder builder = mock(ConstraintValidatorContext.ConstraintViolationBuilder.class);
        when(this.context.buildConstraintViolationWithTemplate(anyString())).thenReturn(builder);
    }

    @Test
    public void validSingleKind() {
        assertTrue(this.sut.isValid("*:*:*:*", this.context));
        assertTrue(this.sut.isValid("authority:source:entity:1.0.0", this.context));
    }

    @Test
    public void validMultiKinds() {
        String kind1="A1:S1:E1:1.0.0";
        String kind2="A2:S2:E2:2.0.0";
        String kind3="A3:S3:E3:2.0.0";
        assertTrue(this.sut.isValid(kind1, this.context));
        assertTrue(this.sut.isValid(kind2, this.context));
        assertTrue(this.sut.isValid(kind3, this.context));
        ArrayList kinds = new ArrayList();
        kinds.add(kind1);
        kinds.add(kind2);
        kinds.add(kind3);
        assertTrue(this.sut.isValid(kinds, this.context));
    }

    @Test
    public void validMultiKindsSeparatedByComma() {
        assertTrue(this.sut.isValid("A1:S1:E1:1.0.0,A2:S2:E2:2.0.0,A3:S3:E3:2.0.0", this.context));
    }

    @Test
    public void validMultiKindsSeparatedByCommaAndWhitespace() {
        assertTrue(this.sut.isValid("   A1:S1:E1:1.0.0 , A2:S2:E2:2.0.0,  A3:S3:E3:2.0.0  ", this.context));
    }

    @Test
    public void invalidMultiKindsSeparatedByComma() {
        assertFalse(this.sut.isValid("A1:S1:E1:1.0.0, ,A2:S2:E2:2.0.0,A3:S3:E3:2.0.0", this.context));
    }

    @Test
    public void simpleKindWithInvalidFormat() {
        assertFalse(this.sut.isValid("*:*:*:", this.context));
        assertFalse(this.sut.isValid("authority:source:entity:V1.0.0", this.context));
    }

    @Test
    public void simpleKindWithInvalidType() {
        assertFalse(this.sut.isValid(123, this.context));
        assertFalse(this.sut.isValid(12.5, this.context));
    }

    @Test
    public void simpleKindWithEmptyString() {
        assertFalse(this.sut.isValid("", this.context));
    }

    @Test
    public void multiKindsWithInvalidFormat() {
        String kind1="A1:S1:E1:1.0.0";
        String kind2="A2:S2:E2:2.0.0";
        String kind3="A3:S3:E3:V2.0.0";
        assertTrue(this.sut.isValid(kind1, this.context));
        assertTrue(this.sut.isValid(kind2, this.context));
        assertFalse(this.sut.isValid(kind3, this.context));
        ArrayList kinds = new ArrayList();
        kinds.add(kind1);
        kinds.add(kind2);
        kinds.add(kind3);
        assertFalse(this.sut.isValid(kinds, this.context));
    }

    @Test
    public void multiKindsWithInvalidTypes() {
        String kind1="A1:S1:E1:1.0.0";
        String kind2="A2:S2:E2:2.0.0";
        int kind3=1000;
        assertTrue(this.sut.isValid(kind1, this.context));
        assertTrue(this.sut.isValid(kind2, this.context));
        assertFalse(this.sut.isValid(kind3, this.context));
        ArrayList kinds = new ArrayList();
        kinds.add(kind1);
        kinds.add(kind2);
        kinds.add(kind3);
        assertFalse(this.sut.isValid(kinds, this.context));
    }

    @Test
    public void multiKindsWithEmptyArray() {
        ArrayList kinds = new ArrayList();
        assertFalse(this.sut.isValid(kinds, this.context));
    }

    @Test
    public void multiKindsWithEmptyStringInArray() {
        ArrayList kinds = new ArrayList();
        kinds.add("");
        assertFalse(this.sut.isValid(kinds, this.context));
    }

    @Test
    public void multiKindsWithTooManyItems() {
        int maxLength = 3840;
        // The following kind does not have alias index name
        String kind1="osdu:wks:master-data-wellbore:1.0.*";
        int count = maxLength/(kind1.length() + 1);

        ArrayList kinds = new ArrayList();
        int n = 0;
        while(n++ < count) {
            kinds.add(kind1);
        }
        assertTrue(this.sut.isValid(kinds, this.context));

        kinds.add(kind1);
        assertFalse(this.sut.isValid(kinds, this.context));
    }

    @Test
    public void multiKindsWithAliaseNames() {
        // Compare it with the above test case: multiKindsWithTooManyItems()
        int maxLength = 3840;
        // The following kind has alias index name
        String kind1="osdu:wks:master-data-wellbore:1.0.0";
        String kind2="osdu:wks:master-data-wellbore:1.*.*";
        int count = maxLength/(kind1.length() + 1);

        ArrayList kind1s = new ArrayList();
        ArrayList kind2s = new ArrayList();
        int n = 0;
        while(n++ < count) {
            kind1s.add(kind1);
            kind2s.add(kind2);
        }
        assertTrue(this.sut.isValid(kind1s, this.context));
        assertTrue(this.sut.isValid(kind2s, this.context));

        kind1s.add(kind1);
        kind2s.add(kind2);

        // It is still valid though the total length of the original kinds exceed the limit
        // but with the aliases, the total length will be much shorter
        assertTrue(this.sut.isValid(kind1s, this.context));
        assertTrue(this.sut.isValid(kind2s, this.context));
    }

    @Test
    public void should_notInterpolate_when_KindHasExpressionLanguage() {
        String kind2="${2+2}";
        ArrayList kinds = new ArrayList();
        kinds.add(kind2);
        assertFalse(this.sut.isValid(kinds, this.context));
        verify(this.context).buildConstraintViolationWithTemplate("Not a valid record kind format. Found: [\\$\\{2+2\\}]");
    }
}
