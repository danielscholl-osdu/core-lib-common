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

package org.opengroup.osdu.core.common.model.entitlements.validation;

import javax.validation.ConstraintValidatorContext;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import org.opengroup.osdu.core.common.model.entitlements.Acl;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;


@RunWith(MockitoJUnitRunner.class)
public class AclValidatorTest {
    private static final String[] VALUES = new String[] { "data.email1@dom.dev.cloud.dom-ds.com",
            "data.test@dom.dev.cloud.dom-ds.com" };

    @Mock
    private ConstraintValidatorContext context;

    private Acl acl;

    private AclValidator sut;

    @Before
    public void setup() {
        this.acl = new Acl();
        this.sut = new AclValidator();

        ConstraintValidatorContext.ConstraintViolationBuilder builder = mock(ConstraintValidatorContext.ConstraintViolationBuilder.class);

        when(this.context.buildConstraintViolationWithTemplate("Invalid group name '\\$\\{2+2\\}'"))
                .thenReturn(builder);
    }

    @Test
    public void should_doNothingInInitialize() {
        // for coverage purposes. Do nothing method!
        this.sut.initialize(null);
    }

    @Test
    public void should_notInterpolate_when_AclOwnerHasExpressionLanguage() {

        String[] EXPRESSION_GROUP = new String[] { "${2+2}" };

        this.acl.setViewers(VALUES);
        this.acl.setOwners(EXPRESSION_GROUP);

        assertFalse(this.sut.isValid(this.acl, this.context));
        verify(this.context).buildConstraintViolationWithTemplate("Invalid group name '\\$\\{2+2\\}'");
    }

    @Test
    public void should_notInterpolate_when_AclViewerHasExpressionLanguage() {

        String[] EXPRESSION_GROUP = new String[] { "${2+2}" };

        this.acl.setViewers(EXPRESSION_GROUP);
        this.acl.setOwners(VALUES);

        assertFalse(this.sut.isValid(this.acl, this.context));
        verify(this.context).buildConstraintViolationWithTemplate("Invalid group name '\\$\\{2+2\\}'");
    }
}