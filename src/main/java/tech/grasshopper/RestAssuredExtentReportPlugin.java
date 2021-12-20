package tech.grasshopper;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import tech.grasshopper.extent.pojo.ResultExtent;
import tech.grasshopper.extent.reports.ReportCreator;
import tech.grasshopper.logging.ReportLogger;
import tech.grasshopper.pojo.Result;
import tech.grasshopper.processor.ResultsProcessor;
import tech.grasshopper.properties.ReportProperties;
import tech.grasshopper.results.ResultsJsonDeserializer;
import tech.grasshopper.results.ResultsJsonPathCollector;

@Mojo(name = "extentreport")
public class RestAssuredExtentReportPlugin extends AbstractMojo {

	@Parameter(property = "extentreport.allureResultsDirectory", required = true)
	private String allureResultsDirectory;

	@Parameter(property = "extentreport.extentReportDirectory", required = true)
	private String extentReportDirectory;

	@Parameter(property = "extentreport.extentConfigFilePath", defaultValue = "src/test/resources/extent-config.xml")
	private String extentConfigFilePath;

	@Parameter(property = "extentreport.clearExtentReportData", defaultValue = "true")
	private boolean clearExtentReportData;

	@Parameter(property = "extentreport.hidelogEvents", defaultValue = "true")
	private boolean hidelogEvents;

	private ResultsJsonPathCollector resultsJsonPathCollector;
	private ResultsJsonDeserializer resultsJsonDeserializer;
	private ResultsProcessor resultsProcessor;
	private ReportCreator reportCreator;
	private ReportProperties reportProperties;

	private ReportLogger logger;

	@Inject
	public RestAssuredExtentReportPlugin(ResultsJsonPathCollector resultsJsonPathCollector,
			ResultsJsonDeserializer resultsJsonDeserializer, ResultsProcessor resultsProcessor,
			ReportCreator reportCreator, ReportProperties reportProperties, ReportLogger logger) {
		this.resultsJsonPathCollector = resultsJsonPathCollector;
		this.resultsJsonDeserializer = resultsJsonDeserializer;
		this.resultsProcessor = resultsProcessor;
		this.reportCreator = reportCreator;
		this.reportProperties = reportProperties;
		this.logger = logger;
	}

	public void execute() {
		try {
			logger.initializeLogger(getLog());
			logger.info("STARTED EXTENT REPORT GENERATION PLUGIN");

			setReportProperties();

			List<Path> resultPaths = resultsJsonPathCollector
					.retrievePaths(reportProperties.getAllureResultsDirectory());
			List<Result> results = resultsJsonDeserializer.retrieveResults(resultPaths);

			// Need data validation

			Map<String, List<ResultExtent>> extentReportData = resultsProcessor.process(results);

			reportCreator.generate(extentReportData);

			logger.info("FINISHED EXTENT REPORT GENERATION PLUGIN");
		} catch (Throwable t) {
			// Report will not result in build failure.
			t.printStackTrace();
			logger.error(String.format("STOPPING EXTENT REPORT GENERATION - %s", t.getMessage()));
		}
	}

	private void setReportProperties() {
		reportProperties.setAllureResultsDirectory(allureResultsDirectory);
		reportProperties.setExtentReportDirectory(extentReportDirectory);
		reportProperties.setExtentConfigFilePath(extentConfigFilePath);
		reportProperties.setClearExtentReportData(clearExtentReportData);
		reportProperties.setHidelogEvents(hidelogEvents);
	}
}
