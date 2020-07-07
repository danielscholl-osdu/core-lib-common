package org.opengroup.osdu.core.common.model.notification;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "secretType")
@JsonSubTypes({
        @JsonSubTypes.Type(value = HmacSecret.class, name = Constants.HMACString),
        @JsonSubTypes.Type(value = GsaSecret.class, name = Constants.GSAString)}
)
public abstract class Secret {
    private String secretType;

    protected Secret(String secretType) {
        this.secretType = secretType;
    }
}