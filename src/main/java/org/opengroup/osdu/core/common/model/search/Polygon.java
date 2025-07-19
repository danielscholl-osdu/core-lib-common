package org.opengroup.osdu.core.common.model.search;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.opengroup.osdu.core.common.SwaggerDoc;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Polygon {
	@ApiModelProperty(value = SwaggerDoc.POLYGON)
	private List<Point> points;
}
