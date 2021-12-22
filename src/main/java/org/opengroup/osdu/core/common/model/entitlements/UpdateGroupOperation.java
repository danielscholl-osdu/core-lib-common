package org.opengroup.osdu.core.common.model.entitlements;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Generated;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@Generated
@NoArgsConstructor
@AllArgsConstructor
public class UpdateGroupOperation {

    private String op;

    private String path;

    private List<String> value;
}
