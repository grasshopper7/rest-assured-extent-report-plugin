package tech.grasshopper.properties;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.inject.Inject;
import javax.inject.Singleton;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import tech.grasshopper.logging.ReportLogger;

@Singleton
@Data
public class ReportProperties {

	private String allureResultsDirectory;

	@Setter(value = AccessLevel.NONE)
	private String reportDirectory;

	private String configFilePath;
	private String systemInfoFilePath;
	private String sparkViewOrder;
	private boolean hidelogEvents;

	public static final String EXTENT_REPORT_DATA_DIRECTORY = "data";
	public static final String BODY = "Body";
	public static final String HEADERS = "Headers";
	public static final String COOKIES = "Cookies";

	private ReportLogger logger;

	@Inject
	public ReportProperties(ReportLogger logger) {
		this.logger = logger;
	}

	public void setReportDirectory(String extentReportDirectory, String extentReportDirectoryTimeStamp) {
		if (extentReportDirectoryTimeStamp == null)
			this.reportDirectory = extentReportDirectory;

		else {
			try {
				DateTimeFormatter timeStampFormat = DateTimeFormatter.ofPattern(extentReportDirectoryTimeStamp);
				String timeStampStr = timeStampFormat.format(LocalDateTime.now());

				this.reportDirectory = extentReportDirectory + " " + timeStampStr;
			} catch (Exception e) {
				logger.info(
						"Unable to process supplied date time format pattern. Creating report with default directory settings.");
				this.reportDirectory = extentReportDirectory;
			}
		}
	}
}
