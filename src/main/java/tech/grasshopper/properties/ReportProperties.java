package tech.grasshopper.properties;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.inject.Singleton;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

@Singleton
@Data
public class ReportProperties {

	private String allureResultsDirectory;

	@Setter(value = AccessLevel.NONE)
	private String extentReportDirectory;

	private String extentConfigFilePath;
	private String extentSparkViewOrder;
	private boolean hidelogEvents;

	public static final String EXTENT_REPORT_DATA_DIRECTORY = "data";
	public static final String BODY = "Body";
	public static final String HEADERS = "Headers";
	public static final String COOKIES = "Cookies";

	public void setExtentReportDirectory(String extentReportDirectory, String extentReportDirectoryTimeStamp) {
		if (extentReportDirectoryTimeStamp == null) {
			this.extentReportDirectory = extentReportDirectory;
		} else {
			DateTimeFormatter timeStampFormat = DateTimeFormatter.ofPattern(extentReportDirectoryTimeStamp);
			String timeStampStr = timeStampFormat.format(LocalDateTime.now());

			this.extentReportDirectory = extentReportDirectory + " " + timeStampStr;
		}
	}
}
