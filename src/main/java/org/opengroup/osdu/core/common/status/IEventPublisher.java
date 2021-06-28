package org.opengroup.osdu.core.common.status;

import java.util.Map;

import org.opengroup.osdu.core.common.exception.CoreException;

/**
 * Interface to publish message in event.
 *
 */
public interface IEventPublisher {
    /**
     * This method will publish message to the configured event using data partition
     * id and correlation id in attributesMap. Message should be in json format.
     * Attributes map should consist both data-partition-id and correlation-id.
     * 
     * <pre>
     * {@code
     * Map<String, String> attributesMap() = new HashMap<>();
     * attributesMap.put("data-partition-id", "partitionId");
     * attributesMap.put("correlation-id", "abc123");
     *  
     * String message = { "data": "value" };
     *  
     * iEventpublisher.publish(message, attributesMap);
     * }
     * </pre>
     * 
     * @param message
     * @param attributesMap
     * @throws CoreException
     */
    void publish(String message, Map<String, String> attributesMap) throws CoreException;
}
