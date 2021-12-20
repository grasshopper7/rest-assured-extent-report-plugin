package tech.grasshopper.extent.reports;

import java.io.IOException;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.aventstack.extentreports.AnalysisStrategy;
import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;

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
		hideLogEvents(spark);
		customizeDataLogTable(spark);

		return extent;
	}

	private ExtentSparkReporter initializeSparkReport(ExtentReports extent) {
		ExtentSparkReporter spark = new ExtentSparkReporter("reports/SparkReport.html");

		extent.attachReporter(spark);
		try {
			spark.loadXMLConfig(reportProperties.getExtentConfigFilePath());
		} catch (IOException e) {
			logger.info("Unable to locate spark configuration file. Creating report with default settings.");
		}

		return spark;
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
