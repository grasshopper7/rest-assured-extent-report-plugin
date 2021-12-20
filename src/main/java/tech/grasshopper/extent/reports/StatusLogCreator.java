package tech.grasshopper.extent.reports;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.MarkupHelper;

import tech.grasshopper.extent.pojo.ResultExtent;

public class StatusLogCreator {

	public void generate(ExtentTest methodExtentTest, ResultExtent result) {

		if (result.getStatus() == Status.FAIL) {

			String failTrace = result.getStackTrace().equals("") ? "No fail message." : result.getStackTrace();
			methodExtentTest.skip(MarkupHelper.createCodeBlock(failTrace));
		} else if (result.getStatus() == Status.SKIP) {

			String skipMsg = result.getStatusMessage().equals("") ? "No skip message." : result.getStatusMessage();
			methodExtentTest.skip(MarkupHelper.createCodeBlock(skipMsg));
		} else {
			return;
		}
	}
}
