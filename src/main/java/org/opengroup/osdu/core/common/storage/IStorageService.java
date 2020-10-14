package org.opengroup.osdu.core.common.storage;

import org.opengroup.osdu.core.common.model.storage.*;

import java.util.Collection;

public interface IStorageService {

	UpsertRecords upsertRecord(Record record) throws StorageException;

	UpsertRecords upsertRecord(Record[] record) throws StorageException;

	void deleteRecord(String id) throws StorageException;

	Record getRecord(String id) throws StorageException;

	MultiRecordInfo getRecords(Collection<String> ids) throws StorageException;

	Schema createSchema(Schema schema) throws StorageException;

	Schema getSchema(String kind) throws StorageException;

	void deleteSchema(String kind) throws StorageException;
}