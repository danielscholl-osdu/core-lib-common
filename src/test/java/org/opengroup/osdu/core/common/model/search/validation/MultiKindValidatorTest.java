package org.opengroup.osdu.core.common.model.search.validation;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import javax.validation.ConstraintValidatorContext;

import java.util.ArrayList;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class MultiKindValidatorTest {

    @Mock
    private ConstraintValidatorContext context;

    static MultiKindValidator sut;

    @Before
    public  void setup(){
        sut = new MultiKindValidator();
        sut.initialize(null);
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
    public void multiKindsWithTooManyItems() {
        int maxLength = 3840;
        String kind1="A1:S1:E1:1.0.0";

        String kind = kind1;
        ArrayList kinds = new ArrayList();
        int totalLen = 0;
        while(totalLen <= maxLength - kind1.length()) {
            kinds.add(kind);
            totalLen += kind1.length() + 1;
        }
        assertTrue(totalLen <= maxLength);
        assertTrue(this.sut.isValid(kinds, this.context));

        kinds.add(kind);
        totalLen += kind1.length() + 1;
        assertTrue(totalLen > maxLength);
        assertFalse(this.sut.isValid(kinds, this.context));
    }
}
