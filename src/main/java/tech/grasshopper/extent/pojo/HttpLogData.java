package tech.grasshopper.extent.pojo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class HttpLogData {

	private HttpRequestData httpRequestData;

	private HttpResponseData httpResponseData;
}
