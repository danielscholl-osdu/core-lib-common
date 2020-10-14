package org.opengroup.osdu.core.common.search;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class SearchAPIConfig {
	String rootUrl;

	String apiKey;

	public static SearchAPIConfig Default() {
		return SearchAPIConfig.builder().build();
	}
}