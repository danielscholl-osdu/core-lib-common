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

import com.google.common.base.Strings;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class NameValidator implements ConstraintValidator<ValidName, String> {

	private static final String NAME_PATTERN = "^[-A-Za-z0-9]{3,100}+$";

	@Override
	public void initialize(ValidName constraintAnnotation) {
		//needed by interface - we don't use
	}

	@Override
	public boolean isValid(String name, ConstraintValidatorContext context) {
		return !Strings.isNullOrEmpty(name) && name.matches(NAME_PATTERN);
	}
}
