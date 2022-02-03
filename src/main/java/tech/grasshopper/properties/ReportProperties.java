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

	private String sparkConfigFilePath;
	private String systemInfoFilePath;
	private String sparkViewOrder;
	private boolean sparkHidelogEvents;

	private String pdfConfigFilePath;

	public static final String EXTENT_REPORT_DATA_DIRECTORY = "data";
	public static final String BODY = "Body";
	public static final String HEADERS = "Headers";
	public static final String COOKIES = "Cookies";

	public static final String ALLURE_RESULTS_DIRECTORY = "target/allure-results";
	public static final String REPORT_DIRECTORY = "report";
	public static final String REPORT_DIRECTORY_TIMESTAMP = "dd MM yyyy HH mm ss";
	public static final String REPORT_SYSTEM_INFO_FILE = "src/test/resources/systeminfo.properties";

	public static final String SPARK_REPORT_CONFIG_FILE = "src/test/resources/spark-config.xml";
	public static final String SPARK_REPORT_HIDE_LOG_EVENTS = "true";

	public static final String PDF_REPORT_CONFIG_FILE = "src/test/resources/pdf-config.xml";

	private ReportLogger logger;

	@Inject
	public ReportProperties(ReportLogger logger) {
		this.logger = logger;
	}

	public void setReportDirectory(String extentReportDirectory, String extentReportDirectoryTimeStamp) {
		if (extentReportDirectoryTimeStamp == null)
			this.reportDirectory = extentReportDirectory;

		else {
			DateTimeFormatter timeStampFormat = null;
			String timeStampStr = "";

			try {
				timeStampFormat = DateTimeFormatter.ofPattern(extentReportDirectoryTimeStamp);
				timeStampStr = timeStampFormat.format(LocalDateTime.now());
			} catch (Exception e) {
				logger.info(
						"Unable to process supplied date time format pattern. Creating report with default directory timestamp settings.");

				timeStampFormat = DateTimeFormatter.ofPattern(REPORT_DIRECTORY_TIMESTAMP);
				timeStampStr = timeStampFormat.format(LocalDateTime.now());
			}

			this.reportDirectory = extentReportDirectory + " " + timeStampStr;
		}
	}
}
