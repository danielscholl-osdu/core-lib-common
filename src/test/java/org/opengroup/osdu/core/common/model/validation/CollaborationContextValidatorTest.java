// Copyright 2022 Schlumberger
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

package org.opengroup.osdu.core.common.model.validation;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.opengroup.osdu.core.common.Constants;
import org.opengroup.osdu.core.common.util.CollaborationContextUtil;

import javax.validation.ConstraintValidatorContext;

import java.util.Map;
import java.util.UUID;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CollaborationContextValidatorTest {

    @Mock
    private ConstraintValidatorContext context;

    private CollaborationContextValidator sut;

    private static final String validGUID = UUID.randomUUID().toString();
    private static final String VALID_APP_NAME = "valid app name";

    private static final String INVALID_APP_NAME = "invalid * app & name";

    private static final String TEST_THIRD_DIRECTIVE = "test third directive for the future";

    private static final String DIRECTIVES_STRING_KEY_PROVIDED_MORE_THAN_ONE_TIME = "id= 6B29FC40-CA47-1067-B31D-00DD010662DA,id= 6B29FC40-CA47-1067-B31D-00DD010662DA, application= app";
    private static final String DIRECTIVES_STRING_KEY_CONTAINS_NON_NUMERIC_CHARACTORS =  "id= 6B29FC40-CA47-1067-B31D-00DD010662DA, application= app, directive$$= test ";

    private static final String DIRECTIVES_STRING_KEY_CONTAINS_UPPER_AND_LOWER_CASE =  "ID= 6B29FC40-CA47-1067-B31D-00DD010662DA, APPlication= app, Directive= test ";
    ConstraintValidatorContext.ConstraintViolationBuilder builder;

    @Before
    public void setup() {
        sut = new CollaborationContextValidator();
        builder = mock(ConstraintValidatorContext.ConstraintViolationBuilder.class);
    }

    @Test
    public void should_notInterpolate_when_IdHasExpressionLanguage() {

        String EXPRESSION_GROUP = "${2+2}";
        String input = getDirectivesRawFromIdAndApplication(EXPRESSION_GROUP, "test app");

        when(context.buildConstraintViolationWithTemplate("invalid directive 'id': '\\$\\{2+2\\}'. 'id' must be a valid GUID/UUID"))
                .thenReturn(builder);

        assertFalse(sut.isValid(input, context));
        verify(context).buildConstraintViolationWithTemplate("invalid directive 'id': '\\$\\{2+2\\}'. 'id' must be a valid GUID/UUID");
    }

    @Test
    public void should_notInterpolate_when_ApplicationHasExpressionLanguage() {

        String EXPRESSION_GROUP = "${2+2}";
        String input = getDirectivesRawFromIdAndApplication(validGUID, EXPRESSION_GROUP);

        when(context.buildConstraintViolationWithTemplate("invalid directive 'application': '\\$\\{2+2\\}'. 'application' must contain only alphanumerics and spaces and should not exceed 128 characters"))
                .thenReturn(builder);

        assertFalse(sut.isValid(input, context));
        verify(context).buildConstraintViolationWithTemplate("invalid directive 'application': '\\$\\{2+2\\}'. 'application' must contain only alphanumerics and spaces and should not exceed 128 characters");
    }

    @Test
    public void shouldReturnTrueWhenValidXCollaborationHeaderIsProvided() {
        String input = getDirectivesRawFromIdAndApplication(validGUID, VALID_APP_NAME);
        assertTrue(sut.isValid(input, context));
    }
    @Test
    public void shouldReturnTrueWhenValidXCollaborationHeaderIsProvidedContainsUpperAndLowerCase() {
        assertTrue(sut.isValid(DIRECTIVES_STRING_KEY_CONTAINS_UPPER_AND_LOWER_CASE, context));
    }

    @Test
    public void shouldReturnTrueWhenValidXCollaborationHeaderIsProvidedWithMoreThanTwoDIrectives() {
        String input = String.format("id= %1$s,application=%2$s,OtherFutureDirective= %3$s", validGUID,VALID_APP_NAME,TEST_THIRD_DIRECTIVE);
        Map<String, String> result = CollaborationContextUtil.getCollaborationDirectiveProperties(input);
        Assert.assertEquals(validGUID, result.get(Constants.ID));
        Assert.assertEquals(VALID_APP_NAME, result.get(Constants.APPLICATION));
        Assert.assertEquals(TEST_THIRD_DIRECTIVE, result.get("otherfuturedirective"));
    }

    @Test
    public void shouldReturnTrueWhenValidXCollaborationHeaderIsProvidedWithThreeDirectives() {
        String input = getDirectivesRawFromIdAndApplicationAndOtherDirectives(validGUID, VALID_APP_NAME,TEST_THIRD_DIRECTIVE);
        assertTrue(sut.isValid(input, context));
    }

    @Test
    public void shouldReturnTrueWhenXCollaborationHeaderHasSpacesInDirectives() {
        String input = getDirectivesRawFromIdAndApplicationWithSpaces(validGUID, VALID_APP_NAME);
        assertTrue(sut.isValid(input, context));
    }

    @Test
    public void shouldReturnTrueWhenCollaborationContextIsNull() {
        assertTrue(sut.isValid(null, context));
    }

    @Test
    public void shouldReturnFalseWhenCollaborationContextIsProvidedButEmpty() {
        when(context.buildConstraintViolationWithTemplate("collaboration context cannot be empty"))
                .thenReturn(builder);

        assertFalse(sut.isValid("", context));
        verify(context).buildConstraintViolationWithTemplate("collaboration context cannot be empty");
    }

    @Test
    public void shouldReturnFalseWhenIdIsNull() {
        String input = getDirectivesRawFromIdAndApplication(null, VALID_APP_NAME);
        assertExistenceViolation(input);
    }

    @Test
    public void shouldReturnFalseWhenIdIsEmpty() {
        String input = getDirectivesRawFromIdAndApplication(" ", VALID_APP_NAME);
        assertPatternViolation(input);
    }

    @Test
    public void shouldReturnFalseWhenApplicationIsNull() {
        String input = getDirectivesRawFromIdAndApplication(validGUID, null);
        assertExistenceViolation(input);
    }

    @Test
    public void shouldReturnFalseWhenApplicationIsEmpty() {
        String input = getDirectivesRawFromIdAndApplication(validGUID, " ");
        assertPatternViolation(input);
    }

    @Test
    public void shouldReturnFalseWhenDirectiveProvidedMoreThanOneTime() {
        when(context.buildConstraintViolationWithTemplate("Directive id provided more than one time"))
                .thenReturn(builder);

        assertFalse(sut.isValid(DIRECTIVES_STRING_KEY_PROVIDED_MORE_THAN_ONE_TIME, context));
        verify(context).buildConstraintViolationWithTemplate("Directive id provided more than one time");
    }

    @Test
    public void shouldReturnFalseWhenDirectiveKeyHasNonNumericCharactors() {
        when(context.buildConstraintViolationWithTemplate("directives names can only contain alphanumerics, '-' and '_'"))
                .thenReturn(builder);

        assertFalse(sut.isValid(DIRECTIVES_STRING_KEY_CONTAINS_NON_NUMERIC_CHARACTORS, context));
        verify(context).buildConstraintViolationWithTemplate("directives names can only contain alphanumerics, '-' and '_'");
    }

    @Test
    public void shouldThrowConstraintVioloationWhenIdIsNotGUID() {
        String input = getDirectivesRawFromIdAndApplication("id1", VALID_APP_NAME);
        when(context.buildConstraintViolationWithTemplate("invalid directive 'id': 'id1'. 'id' must be a valid GUID/UUID"))
                .thenReturn(builder);

        assertFalse(sut.isValid(input, context));
        verify(context).buildConstraintViolationWithTemplate("invalid directive 'id': 'id1'. 'id' must be a valid GUID/UUID");
    }

    @Test
    public void shouldThrowConstraintVioloationWhenApplicationHasInvalidCharacters() {
        String input = getDirectivesRawFromIdAndApplication(validGUID, INVALID_APP_NAME);
        when(context.buildConstraintViolationWithTemplate("invalid directive 'application': 'invalid * app & name'. 'application' must contain only alphanumerics and spaces and should not exceed 128 characters"))
                .thenReturn(builder);

        assertFalse(sut.isValid(input, context));
        verify(context).buildConstraintViolationWithTemplate("invalid directive 'application': 'invalid * app & name'. 'application' must contain only alphanumerics and spaces and should not exceed 128 characters");
    }

    @Test
    public void shouldThrowConstraintVioloationWhenApplicationExceedsCharacterLimit() {
        String longAppName = new String(new char[129]).replace('\0', 'a');
        String input = getDirectivesRawFromIdAndApplication(validGUID, longAppName);
        when(context.buildConstraintViolationWithTemplate("invalid directive 'application': 'aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa'. 'application' must contain only alphanumerics and spaces and should not exceed 128 characters"))
                .thenReturn(builder);

        assertFalse(sut.isValid(input, context));
        verify(context).buildConstraintViolationWithTemplate("invalid directive 'application': 'aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa'. 'application' must contain only alphanumerics and spaces and should not exceed 128 characters");
    }

    private void assertPatternViolation(String input) {
        when(context.buildConstraintViolationWithTemplate("all directives must have non-empty <key>=<value> format"))
                .thenReturn(builder);

        assertFalse(sut.isValid(input, context));
        verify(context).buildConstraintViolationWithTemplate("all directives must have non-empty <key>=<value> format");
    }

    private void assertExistenceViolation(String input) {
        when(context.buildConstraintViolationWithTemplate("collaboration context must contain 'id' and 'application' directives"))
                .thenReturn(builder);

        assertFalse(sut.isValid(input, context));
        verify(context).buildConstraintViolationWithTemplate("collaboration context must contain 'id' and 'application' directives");
    }

    private String getDirectivesRawFromIdAndApplication(String id, String application) {
        StringBuilder directive = new StringBuilder();
        if(id != null) {
            directive.append("id=");
            directive.append(id);
            directive.append(",");
        }
        if(application != null) {
            directive.append("application=");
            directive.append(application);
        }
        return directive.toString();
    }

    private String getDirectivesRawFromIdAndApplicationAndOtherDirectives(String id, String application, String otherDirective) {
        StringBuilder directive = new StringBuilder();
        if(id != null) {
            directive.append("id=");
            directive.append(id);
            directive.append(",");
        }
        if(application != null) {
            directive.append("application=");
            directive.append(application);
            directive.append(",");
        }

        if(otherDirective != null) {
            directive.append("otherDirectiveKey=");
            directive.append(otherDirective);
        }
        return directive.toString();
    }

    private String getDirectivesRawFromIdAndApplicationWithSpaces(String id, String application) {
        StringBuilder directive = new StringBuilder();
        if(id != null) {
            directive.append("id = ");
            directive.append(id);
            directive.append(" , ");
        }
        if(application != null) {
            directive.append(" application =   ");
            directive.append(application);
        }
        return directive.toString();
    }
}
