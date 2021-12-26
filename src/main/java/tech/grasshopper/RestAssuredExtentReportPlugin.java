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

	@Parameter(property = "extentreport.allureResultsDirectory", required = true)
	private String allureResultsDirectory;

	@Parameter(property = "extentreport.extentReportDirectory", defaultValue = "report")
	private String extentReportDirectory;

	@Parameter(property = "extentreport.extentReportDirectoryTimeStamp")
	private String extentReportDirectoryTimeStamp;

	@Parameter(property = "extentreport.extentConfigFilePath", defaultValue = "src/test/resources/spark-config.xml")
	private String extentConfigFilePath;

	@Parameter(property = "extentreport.extentSparkViewOrder")
	private String extentSparkViewOrder;

	@Parameter(property = "extentreport.hidelogEvents", defaultValue = "true")
	private boolean hidelogEvents;

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
		reportProperties.setExtentReportDirectory(extentReportDirectory, extentReportDirectoryTimeStamp);
		reportProperties.setExtentConfigFilePath(extentConfigFilePath);
		reportProperties.setExtentSparkViewOrder(extentSparkViewOrder);
		reportProperties.setHidelogEvents(hidelogEvents);
	}

	private void createAttachmentFolder() {
		try {
			Files.createDirectories(Paths.get(reportProperties.getExtentReportDirectory(),
					ReportProperties.EXTENT_REPORT_DATA_DIRECTORY));
		} catch (IOException e) {
			throw new RestAssuredExtentReportPluginException("Unable to create report attachments directory.", e);
		}
	}
}
