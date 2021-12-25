package tech.grasshopper.extent.pojo;

import lombok.Builder;
import lombok.Data;
import lombok.Builder.Default;

@Data
@Builder
public class HttpLogData {

	private HttpRequestData httpRequestData;

	@Default
	private HttpResponseData httpResponseData = HttpNoResponseData.builder().build();
}
