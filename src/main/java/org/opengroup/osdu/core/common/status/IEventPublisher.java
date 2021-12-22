package org.opengroup.osdu.core.common.status;

import java.util.Map;

import org.opengroup.osdu.core.common.exception.CoreException;
import org.opengroup.osdu.core.common.model.status.Message;

/**
 * Interface to publish message in event.
 *
 */
public interface IEventPublisher {
    /**
     * This method will publish message to the configured event using data partition
     * id and correlation id in attributesMap. Messages is array which should
     * contain either StatusDetails or DatasetDetails. Attributes map should consist
     * both data-partition-id and correlation-id.
     * 
     * <pre>
     * {@code
     * Map<String, String> attributesMap() = new HashMap<>();
     * attributesMap.put("data-partition-id", "partitionId");
     * attributesMap.put("correlation-id", "abc123");
     *  
     * iEventpublisher.publish(messages, attributesMap);
     * }
     * </pre>
     * 
     * @param messages
     * @param attributesMap
     * @throws CoreException
     */
    void publish(Message[] messages, Map<String, String> attributesMap) throws CoreException;
}
