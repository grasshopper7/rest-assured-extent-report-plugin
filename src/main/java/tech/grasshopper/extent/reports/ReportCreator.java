package tech.grasshopper.extent.reports;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;

import tech.grasshopper.extent.pojo.ResultExtent;

@Singleton
public class ReportCreator {

	private ReportInitializer reportInitializer;

	private ClassLevelTestCreator extentClassLevelCreator;

	private MethodLevelTestCreator extentTestLevelCreator;

	private DataLogCreator dataLogCreator;

	private StatusLogCreator statusLogCreator;

	@Inject
	public ReportCreator(ReportInitializer reportInitializer, ClassLevelTestCreator extentClassLevelCreator,
			MethodLevelTestCreator extentTestLevelCreator, StatusLogCreator statusLogCreator,
			DataLogCreator dataLogCreator) {
		this.reportInitializer = reportInitializer;
		this.extentClassLevelCreator = extentClassLevelCreator;
		this.extentTestLevelCreator = extentTestLevelCreator;
		this.statusLogCreator = statusLogCreator;
		this.dataLogCreator = dataLogCreator;
	}

	public void generate(Map<String, List<ResultExtent>> extentReportData) {
		ExtentReports extent = reportInitializer.initialize();

		extentReportData.forEach((k, v) -> {
			ExtentTest clzExtentTest = extentClassLevelCreator.generate(extent, k, v);

			v.forEach(result -> {
				ExtentTest methodExtentTest = extentTestLevelCreator.generate(clzExtentTest, result);
				statusLogCreator.generate(methodExtentTest, result);
				dataLogCreator.generate(methodExtentTest, result);
			});
		});

		extent.flush();
	}
}
