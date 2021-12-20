package tech.grasshopper.extent.reports;

import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Singleton;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.model.Test;

import tech.grasshopper.extent.pojo.ResultExtent;

@Singleton
public class ExtentClassLevelCreator {

	public ExtentTest generate(ExtentReports extent, String testName, List<ResultExtent> results) {
		ExtentTest clzExtentTest = extent.createTest(testName);
		Test clzTest = clzExtentTest.getModel();

		Date clzStart = results.stream().map(ResultExtent::getStartTime).min(Comparator.naturalOrder()).get();
		Date clzEnd = results.stream().map(ResultExtent::getEndTime).max(Comparator.naturalOrder()).get();

		clzTest.setStartTime(clzStart);
		clzTest.setEndTime(clzEnd);

		List<Status> testStatuses = results.stream().map(ResultExtent::getStatus).collect(Collectors.toList());
		clzTest.setStatus(Status.max(testStatuses));

		return clzExtentTest;
	}
}
