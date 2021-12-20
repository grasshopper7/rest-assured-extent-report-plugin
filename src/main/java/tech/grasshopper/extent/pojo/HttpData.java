package tech.grasshopper.extent.pojo;

import lombok.Builder.Default;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import tech.grasshopper.properties.ReportProperties;

@Getter
@SuperBuilder
public abstract class HttpData {

	@Default
	private String bodyContentFile = "";

	@Default
	private String headersContentFile = "";

	@Default
	private String cookiesContentFile = "";

	public void setBodyContentFile(String fileNamePrefix) {
		this.bodyContentFile = contentFileName(fileNamePrefix, ReportProperties.BODY);
	}

	public void setHeadersContentFile(String fileNamePrefix) {
		this.headersContentFile = contentFileName(fileNamePrefix, ReportProperties.HEADERS);
	}

	public void setCookiesContentFile(String fileNamePrefix) {
		this.cookiesContentFile = contentFileName(fileNamePrefix, ReportProperties.COOKIES);
	}

	private String contentFileName(String fileNamePrefix, String type) {
		StringBuffer sbr = new StringBuffer(ReportProperties.EXTENT_REPORT_DATA_DIRECTORY);
		return sbr.append("/").append(fileNamePrefix).append("-").append(type).append(".html").toString();
	}

	public static HttpData createHttpData(String title) {
		String[] details = title.split(" ");

		if (title.startsWith("Status code"))
			return HttpResponseData.builder().statusCode(details[2]).build();

		return HttpRequestData.builder().httpMethod(details[0]).endpoint(details[2]).build();
	}

	protected int rowCount() {
		if (containsHttpContentFiles())
			return 1;
		return 0;
	}

	public boolean containsHttpContentFiles() {
		if (bodyContentFile.isEmpty() && headersContentFile.isEmpty() && cookiesContentFile.isEmpty())
			return false;
		return true;
	}
}
