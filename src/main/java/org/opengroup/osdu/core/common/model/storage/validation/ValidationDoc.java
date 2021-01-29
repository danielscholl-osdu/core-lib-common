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

public class ValidationDoc {

	private ValidationDoc() {
		// private constructor
	}

	// https://www.owasp.org/index.php/OWASP_Validation_Regex_Repository
	public static final String EMAIL_REGEX = "^data\\.[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
	public static final String RECORD_ID_REGEX = "^[\\w\\-\\.]+:[\\w-\\.]+:[\\w\\-\\.\\:\\%]+$";
	public static final String RECORD_ID_WITH_VERSION_REGEX = "^[\\w\\-\\.]+:[\\w-\\.]+:[\\w\\-\\.\\:\\%]+:[0-9]+$";
	public static final String KIND_REGEX = "^[\\w\\-\\.]+:[\\w\\-\\.]+:[\\w\\-\\.]+:[0-9]+.[0-9]+.[0-9]+$";

	public static final String DUPLICATE_RECORD_ID = "Same record cannot be updated twice: '%s'.";
	public static final String INVALID_NULL_IN_ARRAY = "Invalid 'null' value found in array";
	public static final String INVALID_PAYLOAD = "Invalid payload.";
	public static final String INVALID_GROUP_NAME = "Invalid group name '%s'";
	public static final String INVALID_RECORD_ID = "Not a valid record id. Found: ${validatedValue}";
	public static final String INVALID_RECORD_ID_FORMAT = "Invalid record format: '%s'. The following format is expected: {tenant-name}:{object-type}:{unique-identifier} or {tenant-name}:{object-type}:{unique-identifier}:{version}";
	public static final String INVALID_KIND = "Not a valid record kind. Found: ${validatedValue}";
	public static final String INVALID_PARENT_RECORD_ID_FORMAT = "Invalid parent record format: '%s'. The following format is expected: {record-id}:{record-version}";
	public static final String INVALID_PARENT_RECORD_VERSION_FORMAT = "Invalid parent record version: '%s'. Record version must be a numeric value";
	public static final String INVALID_PATCH_OPERATION = "Invalid Patch Operation: can only be 'replace'";
	public static final String INVALID_PATCH_PATH = "Invalid Patch Path: can only be '/acl/viewers', '/acl/owners' or '/legal/legaltags'";
	public static final String RECORD_ID_LIST_NOT_EMPTY = "The list of record IDs cannot be empty";
	public static final String RECORD_ACL_NOT_EMPTY = "Record ACL cannot be empty";
	public static final String RECORD_ACL_VIEWERS_NOT_EMPTY = "Record acl.viewers cannot be empty";
	public static final String RECORD_ACL_OWNERS_NOT_EMPTY = "Record acl.owners cannot be empty";
	public static final String RECORD_ORDC_NOT_EMPTY = "Record otherRelevantDataCountries cannot be empty";
	public static final String RECORD_LEGAL_TAGS_NOT_EMPTY = "Record legaltags cannot be empty";
	public static final String RECORD_PAYLOAD_NOT_EMPTY = "Record data cannot be empty";
	public static final String RECORD_QUERY_CONDITION_NOT_EMPTY = "Record query condition cannot be empty";
	public static final String RECORD_METADATA_OPERATIONS_NOT_EMPTY = "Record metadata operations cannot be empty";
	public static final String SCHEMA_ITEMS_NOT_EMPTY = "Schema information cannot be empty";
	public static final String SCHEMA_PATH_NOT_EMPTY = "Schema path cannot be empty";
	public static final String SCHEMA_KIND_NOT_EMPTY = "Schema kind cannot be empty";
	public static final String RECORDS_MAX = "Up to 500 records can be ingested at a time";
	public static final String RECORDS_RETRIEVAL_MAX = "No more than 100 records can be retrieved in a single request";
	public static final String RECORDS_RETRIEVAL_MAX_V2 = "No more than 20 records can be retrieved in a single request";
}