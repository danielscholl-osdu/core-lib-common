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
import org.mockito.runners.MockitoJUnitRunner;
import org.opengroup.osdu.core.common.model.storage.PatchOperation;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PatchOpValidatorTest {

    private static final String NON_TAG_PATH = "/acl";
    private static final String TAG_PATH = "/tags";

    private static final String PATCH_ADD = "add";
    private static final String PATCH_REMOVE = "remove";
    private static final String PATCH_REPLACE = "replace";
    private static final String PATCH_MOVE = "move";
    private static final String PATCH_COPY = "copy";
    private static final String PATCH_TEST = "test";

    @Mock
    private ConstraintValidatorContext context;
    private PatchOperation operation;

    private PatchOpValidator sut;

    @Before
    public void setup() {
        this.sut = new PatchOpValidator();

        ConstraintValidatorContext.ConstraintViolationBuilder builder = mock(ConstraintValidatorContext.ConstraintViolationBuilder.class);

        when(this.context.buildConstraintViolationWithTemplate(ValidationDoc.INVALID_PATCH_OPERATION)).thenReturn(builder);
    }

    // --- Non Tag Path tests ---

    @Test
    public void should_doNothingInInitialize() {
        // for coverage purposes. Do nothing method!
        this.sut.initialize(null);
    }

    @Test
    public void should_returnFalse_ForNonTag_AddOp() {
        verifyIsInvalid(NON_TAG_PATH, PATCH_ADD);
    }

    @Test
    public void should_returnFalse_ForNonTag_RemoveOp() {
        verifyIsInvalid(NON_TAG_PATH, PATCH_REMOVE);
    }

    @Test
    public void should_returnFalse_ForNonTag_MoveOp() {
        verifyIsInvalid(NON_TAG_PATH, PATCH_MOVE);
    }

    @Test
    public void should_returnFalse_ForNonTag_CopyOp() {
        verifyIsInvalid(NON_TAG_PATH, PATCH_COPY);
    }

    @Test
    public void should_returnFalse_ForNonTag_TestOp() {
        verifyIsInvalid(NON_TAG_PATH, PATCH_TEST);
    }

    @Test
    public void should_returnTrue_ForNonTag_ReplaceOp() {
        verifyIsValid(NON_TAG_PATH, PATCH_REPLACE);
    }

    // --- Tag Path tests ---

    @Test
    public void should_returnFalse_ForTag_MoveOp() {
        verifyIsInvalid(TAG_PATH, PATCH_MOVE);
    }

    @Test
    public void should_returnFalse_ForTag_CopyOp() {
        verifyIsInvalid(TAG_PATH, PATCH_COPY);
    }

    @Test
    public void should_returnFalse_ForTag_TestOp() {
        verifyIsInvalid(TAG_PATH, PATCH_TEST);
    }

    @Test
    public void should_returnTrue_ForTag_ReplaceOp() {
        verifyIsValid(TAG_PATH, PATCH_REPLACE);
    }

    @Test
    public void should_returnTrue_ForTag_AddOp() {
        verifyIsValid(TAG_PATH, PATCH_ADD);
    }

    @Test
    public void should_returnTrue_ForTag_RemoveOp() {
        verifyIsValid(TAG_PATH, PATCH_REMOVE);
    }

    private void verifyIsValid(String path, String op) {
        operation = buildOperation(path, op);
        assertTrue(this.sut.isValid(operation, this.context));
    }

    private void verifyIsInvalid(String path, String op) {
        operation = buildOperation(path, op);
        assertFalse(this.sut.isValid(operation, this.context));
    }

    private PatchOperation buildOperation(String path, String op) {
        return PatchOperation.builder().op(op).path(path).build();
    }
}
