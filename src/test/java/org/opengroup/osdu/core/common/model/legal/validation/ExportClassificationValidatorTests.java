// Copyright 2021 Schlumberger
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

import org.junit.BeforeClass;
import org.junit.Test;
import org.opengroup.osdu.core.common.model.legal.AllowedLegaltagPropertyValues;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ExportClassificationValidatorTests {
    static ExportClassificationValidator sut;

    @BeforeClass
    public static void setupClass(){
        sut = new ExportClassificationValidator();
        sut.initialize(null);
    }

    @Test
    public void should_ReturnTrue_When_GivenValidEccnPredefinedValue(){
        assertTrue(sut.isValid(AllowedLegaltagPropertyValues.ECCN_EAR99, null));
        assertTrue(sut.isValid(AllowedLegaltagPropertyValues.ECCN_0A998, null));

    }
    @Test
    public void should_ReturnTrue_When_GivenValidEccn(){
        assertTrue(sut.isValid("EAR99", null));
        assertTrue(sut.isValid("0A998", null));
    }
    @Test
    public void should_ReturnFalse_When_GivenEmptyEccn(){
        assertFalse(sut.isValid("", null));
    }
    @Test
    public void should_ReturnFalse_When_GivenNulEccn(){
        assertFalse(sut.isValid(null, null));
    }
    @Test
    public void should_ReturnFalse_When_GivenInvalidEccnChars(){
        assertFalse(sut.isValid("ref-eor", null));
    }
    @Test
    public void should_ReturnFalse_When_ECCNIsNotExactlyMatch(){
        assertFalse(sut.isValid("EAR999999", null));
        assertFalse(sut.isValid("0A9988888", null));
    }
    @Test
    public void should_ReturnTrue_When_GivenValidEccnInIrregularCase(){
        assertTrue(sut.isValid("eaR99", null));
        assertTrue(sut.isValid("0a998", null));
    }
    @Test
    public void should_ReturnTrue_When_GivenNotTechnicalDataValue(){
        assertTrue(sut.isValid("Not - Technical Data", null));
    }
    @Test
    public void should_ReturnTrue_When_GivenNoLicenseRequiredValue(){
        assertTrue(sut.isValid("No License Required", null));
    }
}
