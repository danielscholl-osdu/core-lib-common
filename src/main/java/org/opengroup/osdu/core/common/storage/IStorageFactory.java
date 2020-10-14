package org.opengroup.osdu.core.common.storage;

import org.opengroup.osdu.core.common.model.http.DpsHeaders;

public interface IStorageFactory {
	IStorageService create(DpsHeaders headers);
}