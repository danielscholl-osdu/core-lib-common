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

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.http.HttpStatus;
import org.opengroup.osdu.core.common.model.entitlements.Acl;
import org.opengroup.osdu.core.common.model.http.AppException;
import org.opengroup.osdu.core.common.model.legal.Legal;

@Data
@Builder(toBuilder=true)
@NoArgsConstructor
@AllArgsConstructor
public class RecordMetadata{

	private String id;

	private String kind;

	@JsonIgnore
	private String previousVersionKind;

	private Acl acl;

	private Legal legal;

	private RecordAncestry ancestry;

	private Map<String, String> tags = new HashMap<>();

	private List<String> gcsVersionPaths = new ArrayList<>();

	private RecordState status;

	private String user;

	// epoch time
	private long createTime;

	private String modifyUser;

	// epoch time
	private long modifyTime;

	/**
	* storing the hash of the data and meta block
	* @see org.opengroup.osdu.core.common.model.storage.RecordData
	*/
	private Map<String, String> hash;

  public RecordMetadata(Record record) {
    this.id = record.getId();
    this.kind = record.getKind();
    this.acl = record.getAcl();
    this.legal = record.getLegal();
    this.tags = record.getTags();
    this.ancestry = record.getAncestry();
  }

  public Long getLatestVersion() {
    if (!hasVersion()) {
      return null;
    }

    String latestVersionPath = this.gcsVersionPaths.get(this.gcsVersionPaths.size() - 1);
    String[] versionTokens = latestVersionPath.split("/");
    return Long.parseLong(versionTokens[versionTokens.length - 1]);
  }

	public boolean hasVersion() {
    return !this.getGcsVersionPaths().isEmpty();
	}

	public void addGcsPath(long version) {
		this.gcsVersionPaths.add(String.format("%s/%s/%s", this.kind, this.id, version));
	}

	public String getVersionPath(Long version) {
		for (String path : this.gcsVersionPaths) {
			if (path.contains(Long.toString(version))) {
				return path;
			}
		}

		throw new AppException(HttpStatus.SC_NOT_FOUND, "Record version not found",
				"The requested record version was not found",
				"Record id:%s;Record version:%s".formatted(this.id, version));
	}

	public void resetGcsPath(List<String> gcsVersionPathList) {
	    this.gcsVersionPaths.clear();
	    for (String path: gcsVersionPathList) {
	        this.gcsVersionPaths.add(path);
        }
    }
}