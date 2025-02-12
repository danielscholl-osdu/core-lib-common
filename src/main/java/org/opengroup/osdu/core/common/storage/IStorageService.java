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

package org.opengroup.osdu.core.common.storage;

import java.util.Collection;
import org.opengroup.osdu.core.common.model.storage.MultiRecordInfo;
import org.opengroup.osdu.core.common.model.storage.Record;
import org.opengroup.osdu.core.common.model.storage.Schema;
import org.opengroup.osdu.core.common.model.storage.StorageException;
import org.opengroup.osdu.core.common.model.storage.UpsertRecords;

public interface IStorageService {

    /**
     * Update or insert a record
     * @param record The record contents
     * @return An <code>UpsertRecords</code> object which summarizes the results
     * @throws StorageException If the HTTP status code indicates failure or the response cannot be parsed
     */
	UpsertRecords upsertRecord(Record record) throws StorageException;

    /**
     * Update or insert multiple records
     * @param record The record contents
     * @return An <code>UpsertRecords</code> object which summarizes the results
     * @throws StorageException If the HTTP status code indicates failure or the response cannot be parsed
     */
	UpsertRecords upsertRecord(Record[] record) throws StorageException;

    /**
     * Delete a record. Note that this corresponds to the <code>purgeRecord</code> API, which is a "hard" delete.
     * @param id The ID of the record to purge
     * @throws StorageException If the HTTP status code indicates failure or the response cannot be parsed
     */
	void deleteRecord(String id) throws StorageException;

    /**
     * Soft delete a record. This sets the record's status to `deleted` and does not purge it from the database.
     * @param id The ID of the record to soft delete
     * @throws StorageException If the HTTP status code indicates failure or the response cannot be parsed
     */
    void softDeleteRecord(String id) throws StorageException;

    /**
     * Soft delete multiple records. This sets the record's status to `deleted` and does not purge it from the database.
     * @param ids The IDs of the records to soft delete
     * @throws StorageException If the HTTP status code indicates failure or the response cannot be parsed
     */
    void softDeleteRecords(Collection<String> ids) throws StorageException;

    /**
     * Purge record versions by version IDs
     * @param id The ID of the record from which versions should be purged
     * @param versionIds The IDs of the versions to be purged
     * @throws StorageException If the HTTP status code indicates failure or the response cannot be parsed
     */
    void purgeRecordVersions(String id, Collection<String> versionIds) throws StorageException;

    /**
     * Purge record versions.
     * At least one of <code>limit</code> or <code>fromVersion</code> should not be <code>null</code>.
     * If both parameters are provided, the last <code>limit</code> versions starting from <code>fromVersion</code> are purged.
     * If <code>limit</code> is <code>null</code>, then every version older than <code>fromVersion</code> is purged.
     * If <code>fromVersion</code> is <code>null</code>, then the oldest <code>limit</code> versions are purged.
     * In all cases, the latest record version is excluded from the purge.
     * @param id The ID of the record from which versions should be purged
     * @param limit The maximum number of versions to purge
     * @param fromVersion The record version from which the purge range should begin
     * @throws StorageException If the HTTP status code indicates failure or the response cannot be parsed
     */
    void purgeRecordVersions(String id, Integer limit, Long fromVersion) throws StorageException;

    /**
     * Retrieve a record
     * @param id The ID of the record to retrieve
     * @return The requested record, or <code>null</code> if the record was not found
     * @throws StorageException If the HTTP status code indicates failure (except for 404 Not Found) or the response cannot be parsed
     */
	Record getRecord(String id) throws StorageException;

    /**
     * Retrieve multiple records
     * @param ids The IDs of the records to retrieve
     * @return The requested records, or <code>null</code> if the record was not found
     * @throws StorageException If the HTTP status code indicates failure (except for 404 Not Found) or the response cannot be parsed
     */
	MultiRecordInfo getRecords(Collection<String> ids) throws StorageException;

    /**
     * Create a schema
     * @param schema The schema to create
     * @return The created schema
     * @throws StorageException If the HTTP status code indicates failure or the response cannot be parsed
     */
	Schema createSchema(Schema schema) throws StorageException;

    /**
     * Retrieve a schema
     * @param kind The schema to retrieve
     * @return The requested schema, or <code>null</code> if not found
     * @throws StorageException If the HTTP status code indicates failure (except for 404 Not Found) or the response cannot be parsed
     */
	Schema getSchema(String kind) throws StorageException;

    /**
     * Delete a schema
     * @param kind The schema to delete
     * @throws StorageException If the HTTP status code indicates failure or the response cannot be parsed
     */
	void deleteSchema(String kind) throws StorageException;
}