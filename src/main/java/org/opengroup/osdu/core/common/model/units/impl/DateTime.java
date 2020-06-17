package org.opengroup.osdu.core.common.model.units.impl;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.NotEmpty;


@Data
@AllArgsConstructor
@EqualsAndHashCode(callSuper=true)
public class DateTime extends PersistableReference {
    @NotEmpty
    @JsonProperty("format")
    private String format;

    @NotEmpty
    @JsonProperty("timeZone")
    private String timeZone;
    
    @Override
    public String toJsonString()
    {
        return super.toJsonString();
    }

    public DateTime() {
        super("DTM");
    }

}
