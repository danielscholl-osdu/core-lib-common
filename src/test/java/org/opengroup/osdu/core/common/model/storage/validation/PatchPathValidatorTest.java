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

import javax.validation.ConstraintValidatorContext;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PatchPathValidatorTest {
    @Mock
    private ConstraintValidatorContext context;

    private PatchPathValidator sut;

    @Before
    public void setup() {
        this.sut = new PatchPathValidator();
        ConstraintValidatorContext.ConstraintViolationBuilder builder = mock(ConstraintValidatorContext.ConstraintViolationBuilder.class);

        when(this.context.buildConstraintViolationWithTemplate(ValidationDoc.INVALID_PATCH_PATH)).thenReturn(builder);
    }

    @Test
    public void should_doNothingInInitialize() {
        // for coverage purposes. Do nothing method!
        this.sut.initialize(null);
    }

    @Test
    public void should_returnFalse_ifInvalidPaths() {
        String path1 = "/acl/viewer";
        assertFalse(this.sut.isValid(path1, this.context));

        String path2 = "/invalid/owners";
        assertFalse(this.sut.isValid(path2, this.context));

    }

    @Test
    public void should_returnTrue_ifValidPaths() {
        String path1 = "/acl/viewers";
        assertTrue(this.sut.isValid(path1, this.context));

        String path2 = "/acl/owners";
        assertTrue(this.sut.isValid(path2, this.context));

        String path3 = "/legal/legaltags";
        assertTrue(this.sut.isValid(path3, this.context));

        String path4 = "/tags";
        assertTrue(this.sut.isValid(path4, this.context));
    }

}