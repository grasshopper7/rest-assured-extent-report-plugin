package tech.grasshopper.extent.reports;

import javax.inject.Singleton;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.model.Test;

import tech.grasshopper.extent.pojo.ResultExtent;

@Singleton
public class ExtentMethodLevelCreator {

	public ExtentTest generate(ExtentTest clzExtentTest, ResultExtent result) {
		ExtentTest methodExtentTest = clzExtentTest.createNode(result.getName());
		Test methodTest = methodExtentTest.getModel();

		methodTest.setStartTime(result.getStartTime());
		methodTest.setEndTime(result.getEndTime());

		methodTest.setStatus(result.getStatus());

		result.getCategories().forEach(t -> methodExtentTest.assignCategory(t.getName()));
		result.getAuthors().forEach(t -> methodExtentTest.assignAuthor(t.getName()));
		
		return methodExtentTest;
	}
}
