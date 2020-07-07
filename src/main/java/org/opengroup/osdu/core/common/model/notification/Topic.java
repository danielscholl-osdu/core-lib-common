package org.opengroup.osdu.core.common.model.notification;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Topic {
    private String name;
    private String description;
    private String state;
    private Object example;
}
