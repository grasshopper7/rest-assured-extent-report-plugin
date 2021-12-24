package tech.grasshopper.properties;

import javax.inject.Singleton;

import lombok.Data;

@Singleton
@Data
public class ReportProperties {

	private String allureResultsDirectory;
	private String extentReportDirectory;
	private String extentConfigFilePath;
	private String extentSparkViewOrder;
	private boolean hidelogEvents;

	public static final String EXTENT_REPORT_DATA_DIRECTORY = "data";
	public static final String BODY = "Body";
	public static final String HEADERS = "Headers";
	public static final String COOKIES = "Cookies";
}
