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

package org.opengroup.osdu.core.common.model.search;


import org.opengroup.osdu.core.common.search.Config;

public class QueryUtils {

	public static int getResultSizeForQuery(int limit) {
		if (limit == 0) return Config.getQueryDefaultLimit();
		if (limit > 0 && limit <= Config.getQueryLimitMaximum()) return limit;
		return Config.getQueryLimitMaximum();
	}
}
