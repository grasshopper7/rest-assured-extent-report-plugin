package tech.grasshopper.extent.pojo;

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
}
