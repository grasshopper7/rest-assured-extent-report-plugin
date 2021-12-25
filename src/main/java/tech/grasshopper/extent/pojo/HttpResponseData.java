package tech.grasshopper.extent.pojo;

import java.util.Map;

import lombok.Builder.Default;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class HttpResponseData extends HttpData {

	@Default
	private String statusCode = "";

	@Override
	public int rowCount() {
		return 1 + super.rowCount();
	}

	@Override
	public void addPropertiesDisplay(Map<String, String> details) {
		details.put("Status Code", statusCode);
	}

	@Override
	public void addHttpContentFilesDisplay(Map<String, String> details) {
		details.put("Response", createFileLinks());
	}
}
