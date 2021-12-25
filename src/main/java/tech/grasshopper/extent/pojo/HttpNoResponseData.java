package tech.grasshopper.extent.pojo;

import java.util.Map;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class HttpNoResponseData extends HttpResponseData {

	@Override
	public int rowCount() {
		return 0;
	}

	@Override
	public boolean containsHttpContentFiles() {
		return false;
	}

	@Override
	public void addPropertiesDisplay(Map<String, String> details) {

	}

	@Override
	public void addHttpContentFilesDisplay(Map<String, String> details) {

	}
}
