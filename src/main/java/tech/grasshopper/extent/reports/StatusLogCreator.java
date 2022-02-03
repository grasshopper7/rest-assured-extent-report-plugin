package tech.grasshopper.extent.reports;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.MarkupHelper;

import tech.grasshopper.exception.ExceptionParser;
import tech.grasshopper.extent.pojo.ResultExtent;

@Singleton
public class StatusLogCreator {

	private ExceptionParser exceptionParser;

	@Inject
	public StatusLogCreator(ExceptionParser exceptionParser) {
		this.exceptionParser = exceptionParser;
	}

	public void generate(ExtentTest methodExtentTest, ResultExtent result) {

		if (result.getStatus() == Status.FAIL) {
			Throwable throwable = exceptionParser.parseStackTrace(result);

			// Hack to remove stack due to exception creation
			throwable.setStackTrace(new StackTraceElement[0]);

			methodExtentTest.fail(throwable);
		} else if (result.getStatus() == Status.SKIP) {

			String skipMsg = result.getStatusMessage().equals("") ? "No skip message." : result.getStatusMessage();
			methodExtentTest.skip(MarkupHelper.createCodeBlock(skipMsg));
		} else {
			return;
		}
	}
}
