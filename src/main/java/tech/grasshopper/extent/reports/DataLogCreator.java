package tech.grasshopper.extent.reports;

import java.util.LinkedHashMap;
import java.util.Map;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.markuputils.MarkupHelper;

import tech.grasshopper.extent.pojo.HttpLogData;
import tech.grasshopper.extent.pojo.ResultExtent;

public class DataLogCreator {

	public void generate(ExtentTest methodExtentTest, ResultExtent result) {
		Map<String, String> details = null;
		String[][] data = null;

		for (HttpLogData log : result.getDataLogs()) {
			details = new LinkedHashMap<>();

			log.getHttpRequestData().addPropertiesDisplay(details);
			log.getHttpResponseData().addPropertiesDisplay(details);
			log.getHttpRequestData().addHttpContentFilesDisplay(details);
			log.getHttpResponseData().addHttpContentFilesDisplay(details);

			data = details.entrySet().stream().map(e -> new String[] { e.getKey(), e.getValue() })
					.toArray(String[][]::new);

			methodExtentTest.info(MarkupHelper.createTable(data));
		}
	}
}