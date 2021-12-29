package tech.grasshopper.extent.reports;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
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

		addSystemInfoProperties(extent);

		ExtentSparkReporter spark = initializeSparkReport(extent);
		customizeViewOrder(spark);
		hideLogEvents(spark);
		customizeDataLogTable(spark);

		return extent;
	}

	private void addSystemInfoProperties(ExtentReports extent) {
		String systemInfoFilePath = reportProperties.getSystemInfoFilePath();

		if (systemInfoFilePath == null || systemInfoFilePath.indexOf('.') == -1)
			return;

		Properties properties = new Properties();
		try {
			InputStream is = new FileInputStream(systemInfoFilePath);
			properties.load(is);
		} catch (IOException e) {
			logger.info("Unable to load system info properties. No system info data available.");
			return;
		}
		properties.forEach((k, v) -> extent.setSystemInfo(String.valueOf(k), String.valueOf(v)));
	}

	private ExtentSparkReporter initializeSparkReport(ExtentReports extent) {
		ExtentSparkReporter spark = new ExtentSparkReporter(
				Paths.get(reportProperties.getReportDirectory(), "ExtentSparkReport.html").toString());

		extent.attachReporter(spark);
		try {
			loadConfigFile(spark);
		} catch (Exception e) {
			logger.info("Unable to locate spark configuration. Creating report with default settings.");
		}
		return spark;
	}

	private void loadConfigFile(ExtentSparkReporter spark) throws IOException {
		String configFilePath = reportProperties.getConfigFilePath();

		if (configFilePath == null || configFilePath.indexOf('.') == -1)
			return;

		String configExt = configFilePath.substring(configFilePath.lastIndexOf('.') + 1);

		if (configExt.equalsIgnoreCase("xml"))
			spark.loadXMLConfig(configFilePath);
		else if (configExt.equalsIgnoreCase("json"))
			spark.loadJSONConfig(configFilePath);
	}

	private void customizeViewOrder(ExtentSparkReporter spark) {
		if (reportProperties.getSparkViewOrder() == null)
			return;

		try {
			List<ViewName> viewOrder = Arrays.stream(reportProperties.getSparkViewOrder().split(","))
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
