package tech.grasshopper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import tech.grasshopper.exception.RestAssuredExtentReportPluginException;
import tech.grasshopper.extent.pojo.ResultExtent;
import tech.grasshopper.extent.reports.ReportCreator;
import tech.grasshopper.logging.ReportLogger;
import tech.grasshopper.pojo.Result;
import tech.grasshopper.processor.ResultsProcessor;
import tech.grasshopper.properties.ReportProperties;
import tech.grasshopper.results.JsonResultsCollector;

@Mojo(name = "extentreport")
public class RestAssuredExtentReportPlugin extends AbstractMojo {

	@Parameter(property = "extentreport.allureResultsDirectory", defaultValue = ReportProperties.ALLURE_RESULTS_DIRECTORY)
	private String allureResultsDirectory;

	@Parameter(property = "extentreport.reportDirectory", defaultValue = ReportProperties.REPORT_DIRECTORY)
	private String reportDirectory;

	@Parameter(property = "extentreport.reportDirectoryTimeStamp", defaultValue = ReportProperties.REPORT_DIRECTORY_TIMESTAMP)
	private String reportDirectoryTimeStamp;

	@Parameter(property = "extentreport.sparkConfigFilePath", defaultValue = ReportProperties.SPARK_REPORT_CONFIG_FILE)
	private String sparkConfigFilePath;

	@Parameter(property = "extentreport.pdfConfigFilePath", defaultValue = ReportProperties.PDF_REPORT_CONFIG_FILE)
	private String pdfConfigFilePath;

	@Parameter(property = "extentreport.systemInfoFilePath", defaultValue = ReportProperties.REPORT_SYSTEM_INFO_FILE)
	private String systemInfoFilePath;

	@Parameter(property = "extentreport.sparkViewOrder")
	private String sparkViewOrder;

	@Parameter(property = "extentreport.sparkHideLogEvents", defaultValue = ReportProperties.SPARK_REPORT_HIDE_LOG_EVENTS)
	private boolean sparkHideLogEvents;

	private JsonResultsCollector jsonResultsCollector;
	private ResultsProcessor resultsProcessor;
	private ReportCreator reportCreator;
	private ReportProperties reportProperties;

	private ReportLogger logger;

	@Inject
	public RestAssuredExtentReportPlugin(JsonResultsCollector jsonResultsCollector, ResultsProcessor resultsProcessor,
			ReportCreator reportCreator, ReportProperties reportProperties, ReportLogger logger) {

		this.jsonResultsCollector = jsonResultsCollector;
		this.resultsProcessor = resultsProcessor;
		this.reportCreator = reportCreator;
		this.reportProperties = reportProperties;
		this.logger = logger;
	}

	public void execute() {
		try {
			logger.initializeLogger(getLog());
			logger.info("STARTING EXTENT REPORT GENERATION");

			setReportProperties();
			createAttachmentFolder();

			List<Result> results = jsonResultsCollector.retrieveResults(reportProperties.getAllureResultsDirectory());

			Map<String, List<ResultExtent>> extentReportData = resultsProcessor.process(results);

			reportCreator.generate(extentReportData);

			logger.info("EXTENT REPORT SUCCESSFULLY GENERATED");
		} catch (Throwable t) {
			// Report will not result in build failure.
			t.printStackTrace();
			logger.error(String.format("STOPPING EXTENT REPORT GENERATION - %s", t.getMessage()));
		}
	}

	private void setReportProperties() {
		reportProperties.setAllureResultsDirectory(allureResultsDirectory);
		reportProperties.setReportDirectory(reportDirectory, reportDirectoryTimeStamp);
		reportProperties.setSparkConfigFilePath(sparkConfigFilePath);
		reportProperties.setSystemInfoFilePath(systemInfoFilePath);
		reportProperties.setSparkViewOrder(sparkViewOrder);
		reportProperties.setSparkHidelogEvents(sparkHideLogEvents);
		reportProperties.setPdfConfigFilePath(pdfConfigFilePath);
	}

	private void createAttachmentFolder() {
		try {
			Files.createDirectories(
					Paths.get(reportProperties.getReportDirectory(), ReportProperties.EXTENT_REPORT_DATA_DIRECTORY));
		} catch (IOException e) {
			throw new RestAssuredExtentReportPluginException("Unable to create report attachments directory.", e);
		}
	}
}
