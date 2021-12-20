package tech.grasshopper.extent.pojo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Builder.Default;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class HttpRequestData extends HttpData {

	@Default
	private String httpMethod = "";

	@Default
	private String endpoint = "";

	@Override
	public int rowCount() {
		return 2 + super.rowCount();
	}
}
