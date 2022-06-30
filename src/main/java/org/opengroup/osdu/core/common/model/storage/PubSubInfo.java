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

package org.opengroup.osdu.core.common.model.storage;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import org.opengroup.osdu.core.common.model.indexer.OperationType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import static com.fasterxml.jackson.annotation.JsonInclude.Include;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PubSubInfo {
	private String id;
	private String kind;
	private OperationType op;

	/**
	 * This specifies the changes that have been made to the record
	 * e.g. "data" "data metadata" "data metadata+" "metadata-" ...
	 */
	private String recordBlocks;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@Builder.Default
	private String previousVersionKind = null;

	public PubSubInfo(String id, String kind, OperationType operationType) {
		this.id = id;
		this.kind = kind;
		this.op = operationType;
	}
}
