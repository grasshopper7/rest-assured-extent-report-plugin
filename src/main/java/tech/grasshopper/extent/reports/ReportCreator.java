package tech.grasshopper.extent.reports;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.markuputils.MarkupHelper;

import tech.grasshopper.extent.pojo.HttpData;
import tech.grasshopper.extent.pojo.HttpLogData;
import tech.grasshopper.extent.pojo.ResultExtent;

@Singleton
public class ReportCreator {

	private ReportInitializer reportInitializer;

	private ExtentClassLevelCreator extentClassLevelCreator;

	private ExtentMethodLevelCreator extentTestLevelCreator;

	@Inject
	public ReportCreator(ReportInitializer reportInitializer, ExtentClassLevelCreator extentClassLevelCreator,
			ExtentMethodLevelCreator extentTestLevelCreator) {
		this.reportInitializer = reportInitializer;
		this.extentClassLevelCreator = extentClassLevelCreator;
		this.extentTestLevelCreator = extentTestLevelCreator;
	}

	public void generate(Map<String, List<ResultExtent>> extentReportData) {
		ExtentReports extent = reportInitializer.initialize();

		extentReportData.forEach((k, v) -> {
			ExtentTest clzExtentTest = extentClassLevelCreator.generate(extent, k, v);

			v.forEach(c -> {
				ExtentTest methodExtentTest = extentTestLevelCreator.generate(clzExtentTest, c);

				for (HttpLogData log : c.getDataLogs()) {

					String[][] data = new String[log.getHttpRequestData().rowCount()
							+ log.getHttpResponseData().rowCount()][2];

					data[0][0] = "Method";
					data[0][1] = log.getHttpRequestData().getHttpMethod();
					data[1][0] = "Endpoint";
					data[1][1] = log.getHttpRequestData().getEndpoint();
					data[2][0] = "Status Code";
					data[2][1] = log.getHttpResponseData().getStatusCode();

					int i = 3;
					if (log.getHttpRequestData().containsHttpContentFiles()) {
						data[i][0] = "Request";
						data[i][1] = createFileLinks(log.getHttpRequestData());
						i = 4;
					}

					if (log.getHttpResponseData().containsHttpContentFiles()) {
						data[i][0] = "Response";
						data[i][1] = createFileLinks(log.getHttpResponseData());
					}
					methodExtentTest.info(MarkupHelper.createTable(data));
				}
			});
		});

		extent.flush();
	}

	private String createFileLinks(HttpData httpData) {
		StringBuffer sbr = new StringBuffer();

		if (httpData.containsHttpContentFiles()) {
			if (!httpData.getBodyContentFile().isEmpty())
				sbr.append(createFileLink(httpData.getBodyContentFile(), "Body"));
			if (!httpData.getHeadersContentFile().isEmpty())
				sbr.append(createFileLink(httpData.getHeadersContentFile(), "Headers"));
			if (!httpData.getCookiesContentFile().isEmpty())
				sbr.append(createFileLink(httpData.getCookiesContentFile(), "Cookies"));
		}
		return sbr.toString();
	}

	private String createFileLink(String link, String linkText) {
		StringBuffer sbr = new StringBuffer();
		return sbr.append("<a href='#' onClick=\"window.open('").append(link)
				.append("','','width=700,height=500'); return false;\">").append(linkText).append("</a> &nbsp;&nbsp;")
				.toString();
	}
}
