package tech.grasshopper.extent.reports;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Singleton;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.markuputils.MarkupHelper;

import tech.grasshopper.extent.pojo.ResultExtent;
import tech.grasshopper.pojo.HttpLogData;

@Singleton
public class DataLogCreator {

	public void generate(ExtentTest methodExtentTest, ResultExtent result) {
		Map<String, String> details = null;
		String[][] data = null;
		List<String> allowedData = Arrays.asList("Method", "Endpoint", "Status Code", "Request", "Response");

		for (HttpLogData log : result.getDataLogs()) {
			details = new LinkedHashMap<>();

			log.getHttpRequestData().addPropertiesDisplay(details);
			log.getHttpResponseData().addPropertiesDisplay(details);
			log.getHttpRequestData().addHttpContentFilesDisplay(details);
			log.getHttpResponseData().addHttpContentFilesDisplay(details);

			data = details.entrySet().stream().filter(e -> allowedData.contains(e.getKey()))
					.map(e -> new String[] { e.getKey(), e.getValue() }).toArray(String[][]::new);

			methodExtentTest.info(MarkupHelper.createTable(data));
		}
	}
}