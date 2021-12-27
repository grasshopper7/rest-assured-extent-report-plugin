package tech.grasshopper.extent.reports;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.aventstack.extentreports.AnalysisStrategy;
import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.ViewName;

import tech.grasshopper.logging.ReportLogger;
import tech.grasshopper.properties.ReportProperties;

@Singleton
public class ReportInitializer {

	private ReportProperties reportProperties;

	private ReportLogger logger;

	@Inject
	public ReportInitializer(ReportProperties reportProperties, ReportLogger logger) {
		this.reportProperties = reportProperties;
		this.logger = logger;
	}

	public ExtentReports initialize() {
		ExtentReports extent = new ExtentReports();
		extent.setAnalysisStrategy(AnalysisStrategy.CLASS);
		extent.setReportUsesManualConfiguration(true);

		ExtentSparkReporter spark = initializeSparkReport(extent);
		customizeViewOrder(spark);
		hideLogEvents(spark);
		customizeDataLogTable(spark);

		return extent;
	}

	private ExtentSparkReporter initializeSparkReport(ExtentReports extent) {
		ExtentSparkReporter spark = new ExtentSparkReporter(
				Paths.get(reportProperties.getExtentReportDirectory(), "ExtentSparkReport.html").toString());

		extent.attachReporter(spark);
		try {
			loadConfigFile(spark);
		} catch (Exception e) {
			logger.info("Unable to locate spark configuration file. Creating report with default settings.");
		}
		return spark;
	}

	private void loadConfigFile(ExtentSparkReporter spark) throws IOException {
		String configFilePath = reportProperties.getExtentConfigFilePath();

		if (configFilePath == null)
			return;

		if (configFilePath.indexOf('.') == -1)
			return;

		String configExt = configFilePath.substring(configFilePath.lastIndexOf('.') + 1);

		if (configExt.equalsIgnoreCase("xml"))
			spark.loadXMLConfig(configFilePath);
		else if (configExt.equalsIgnoreCase("json"))
			spark.loadJSONConfig(configFilePath);
	}

	private void customizeViewOrder(ExtentSparkReporter spark) {
		if (reportProperties.getExtentSparkViewOrder() == null)
			return;

		try {
			List<ViewName> viewOrder = Arrays.stream(reportProperties.getExtentSparkViewOrder().split(","))
					.map(v -> ViewName.valueOf(v.trim().toUpperCase())).collect(Collectors.toList());
			spark.viewConfigurer().viewOrder().as(viewOrder).apply();
		} catch (Exception e) {
			logger.info("Unable to customize Spark report view order. Creating report with default view order.");
		}
	}

	private void hideLogEvents(ExtentSparkReporter spark) {
		// Hide log events chart
		if (reportProperties.isHidelogEvents())
			spark.config().setJs("document.getElementsByClassName('col-md-4')[2].style.setProperty('display','none');");
	}

	private void customizeDataLogTable(ExtentSparkReporter spark) {
		// Fix width of first column of details table
		spark.config().setCss(
				"div[class='card-body'] table[class='table table-sm'] tr[class='event-row'] table[class='markup-table table '] tr:first-child > td:first-child { width: 100px; }");
	}
}
