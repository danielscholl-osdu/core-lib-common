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

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import org.apache.http.HttpStatus;
import org.opengroup.osdu.core.common.model.entitlements.Acl;
import org.opengroup.osdu.core.common.model.legal.Legal;
import org.opengroup.osdu.core.common.model.entitlements.validation.ValidAcl;
import org.opengroup.osdu.core.common.model.http.AppException;
import org.opengroup.osdu.core.common.model.storage.validation.ValidKind;
import org.opengroup.osdu.core.common.model.legal.validation.ValidLegal;
import org.opengroup.osdu.core.common.model.storage.validation.ValidRecordAncestry;
import org.opengroup.osdu.core.common.model.storage.validation.ValidationDoc;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ValidLegal
@JsonIgnoreProperties(ignoreUnknown = true)
public class Record {

	private static final String DATALAKE_RECORD_PREFIX = "doc";

	@Pattern(regexp = ValidationDoc.RECORD_ID_REGEX, message = ValidationDoc.INVALID_RECORD_ID)
	@ApiModelProperty(value = SwaggerDoc.RECORD_ID_DESCRIPTION,
			required = true,
			example = SwaggerDoc.RECORD_ID_EXAMPLE)
	private String id;

	private Long version;

	@ValidKind
	@ApiModelProperty(value = SwaggerDoc.SCHEMA_REQUEST_KIND,
			required = true,
			example = SwaggerDoc.RECORD_KIND_EXAMPLE)
	private String kind;

	@NotNull(message = ValidationDoc.RECORD_ACL_NOT_EMPTY)
	@ValidAcl
	private Acl acl;

	@Valid
	private Legal legal;

	@NotEmpty(message = ValidationDoc.RECORD_PAYLOAD_NOT_EMPTY)
	@JsonInclude(Include.ALWAYS)
	private Map<String, Object> data;

	@ValidRecordAncestry
	private RecordAncestry ancestry;

	private Map<String, Object>[] meta;

	private Map<String, String> tags = new HashMap<>();

	private String createUser;

	private long createTime;

	private String modifyUser;

	private long modifyTime;


	private static final java.util.regex.Pattern recordKindPattern = java.util.regex.Pattern.compile(ValidationDoc.KIND_REGEX);
	private static final java.util.regex.Pattern recordIdPattern = java.util.regex.Pattern.compile(ValidationDoc.RECORD_ID_REGEX);

	public void createNewRecordId(String tenant, String kind) {

		Matcher kindMatcher = recordKindPattern.matcher(kind);
		boolean matchFound = kindMatcher.find();

		if (!matchFound)
			throw new AppException(HttpStatus.SC_BAD_REQUEST, "Record kind not valid",
				"The Record kind is not valid");

		String[] kindSplitByColon = kind.split(":");
		String kindSubType = kindSplitByColon[2]; //grab GroupType/IndividualType

		String uuid = UUID.randomUUID().toString().replace("-", "");
		String dlId = String.format("%s:%s:%s", tenant, kindSubType, uuid);
		this.setId(dlId);
	}

	/**
	 * Checks if a RecordId has a valid format.  Use when the Record kind is not available
	 * @param recordId
	 * @param tenant
	 * @return
	 */
	public static boolean isRecordIdValidFormatAndTenant(String recordId, String tenant) {

		Matcher recordIdMatcher = recordIdPattern.matcher(recordId);
		boolean matchFound = recordIdMatcher.find();

		//full ID should match validation regex
		if (!matchFound) {
			return false;
		}

		//id should be split by colons. ex: tenant:groupType--individualType:uniqueId
		String[] recordIdSplitByColon = recordId.split(":");

		//first section of id should be the tenant
		if (!recordIdSplitByColon[0].equalsIgnoreCase(tenant))
			return false;

		return true;
	}

	/**
	 * Checks if a RecordId has a valid format and matches the specified tenant and kind
	 * @param recordId
	 * @param tenant
	 * @param kind
	 * @return
	 */
	public static boolean isRecordIdValid(String recordId, String tenant, String kind) {

		//Check format and tenant
		if (!Record.isRecordIdValidFormatAndTenant(recordId, tenant))
			return false;

		//id should be split by colons. ex: tenant:groupType--individualType:uniqueId
		String[] recordIdSplitByColon = recordId.split(":");

		//make sure groupType/individualType is correct
		String[] kindSplitByColon = kind.split(":");
		String kindSubType = kindSplitByColon[2]; //grab GroupType/IndividualType

		return true;
	}
}