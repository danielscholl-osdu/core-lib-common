package org.opengroup.osdu.core.common.model.info;

import lombok.Builder;
import lombok.Data;

/**
 * The node contains service-specific values for all outer services connected to OSDU service.
 * The value is optional - basic implementation contains an empty list.
 * To define outer services info for OSDU service
 * need to override <code>loadConnectedOuterServices</code> method.
 */
@Data
@Builder
public class ConnectedOuterService {
  private String name;
  private String version;
}
