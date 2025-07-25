/*
 * Copyright 2020 Google LLC
 * Copyright 2020 EPAM Systems, Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opengroup.osdu.core.common.util;

import java.net.URI;
import org.apache.commons.lang3.StringUtils;

public class UrlNormalizationUtil {

	public static String normalizeStringUrl(String...url){
		String joinedUrl = StringUtils.join(url).replaceAll("\t|\r|\n|\f|\\s","");
		return URI.create(joinedUrl).normalize().toString();
	}

	private UrlNormalizationUtil() {
	}
}
