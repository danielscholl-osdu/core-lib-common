/*
 * Copyright 2021 Microsoft Corporation
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

package org.opengroup.osdu.core.common.dms;

import org.opengroup.osdu.core.common.dms.model.*;
import org.opengroup.osdu.core.common.model.storage.Record;

import java.util.List;

/**
 * Interface for different DMS functionalities.
 * Each DMS or CSP could have different implementations but should adhere to this interface.
 */
public interface IDmsService {
    /**
     * Method is used to generate storage instructions for datasets.
     * The storage instructions response include the Signed URL / Temporary credentials along with other CSP specific metadata required to identify a Dataset.
     * @return StorageInstructionsResponse
     */
    StorageInstructionsResponse getStorageInstructions();


    /**
     * Method is used to generate retrieval instructions for datasets.
     * The storage instructions response include the Signed URL / Temporary credentials along with other CSP specific metadata required to identify a Dataset.
     * @param retrievalInstructionsRequest Request containing dataset ids which should be downloaded
     * @return RetrievalInstructionsResponse
     */
    RetrievalInstructionsResponse getRetrievalInstructions(RetrievalInstructionsRequest retrievalInstructionsRequest);

    /**
     * Method is used to Copy Datasets from staging locations to persistent locations
     * @param datasetSources Request containing list of Dataset Metadata Records from which DMS implementations
     *                      will parse Dataset path in Cloud Blob Stores and copies content to persistent locations.
     * @return Copy operation responses that contain if operation is successful and the location to which the dataset is copied to.
     */
    List<CopyDmsResponse> copyDatasetsToPersistentLocation(List<Record> datasetSources);

    /***
     * Method is used to generate storage instructions for datasets.
     * The storage instructions response include the Signed URL / Temporary credentials along with other CSP specific metadata required to identify a Dataset.
     * @param expiryTime: When the method is called with an expiry time for the storage instructions
     * @return StorageInstructionsResponse
     */
    default StorageInstructionsResponse getStorageInstructions(String expiryTime){
        return getStorageInstructions();
    }

    /**
     * Method is used to generate retrieval instructions for datasets.
     * The storage instructions response include the Signed URL / Temporary credentials along with other CSP specific metadata required to identify a Dataset.
     * @param retrievalInstructionsRequest Request containing dataset ids which should be downloaded
     * @param expiryTime: When the method is called with an expiry time for the storage instructions
     * @return RetrievalInstructionsResponse
     */
    default RetrievalInstructionsResponse getRetrievalInstructions(
            RetrievalInstructionsRequest retrievalInstructionsRequest, String expiryTime){
        return getRetrievalInstructions(retrievalInstructionsRequest);
    }

}
