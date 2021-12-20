package tech.grasshopper.exception;

public class RestAssuredExtentReportPluginException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public RestAssuredExtentReportPluginException(String message) {
		super(message);
	}

	public RestAssuredExtentReportPluginException(String message, Exception exception) {
		super(message, exception);
	}
}
