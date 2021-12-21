package tech.grasshopper.processor.attachment;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import tech.grasshopper.exception.RestAssuredExtentReportPluginException;
import tech.grasshopper.properties.ReportProperties;

@Singleton
public class AttachmentContentProcessor {

	private ReportProperties reportProperties;

	@Inject
	public AttachmentContentProcessor(ReportProperties reportProperties) {
		this.reportProperties = reportProperties;
	}

	public void processBodyContent(String content, String fileNamePrefix) {
		StringBuffer sbr = new StringBuffer();
		sbr.append("<div><pre>").append(content).append("</pre></div>");
		createDisplayFiles(sbr, fileNamePrefix, ReportProperties.BODY);
	}

	public void processHeadersContent(Map<String, String> data, String fileNamePrefix) {
		StringBuffer sbr = processHeadersAndCookiesContent(data);
		createDisplayFiles(sbr, fileNamePrefix, ReportProperties.HEADERS);
	}

	public void processCookiesContent(Map<String, String> data, String fileNamePrefix) {
		StringBuffer sbr = processHeadersAndCookiesContent(data);
		createDisplayFiles(sbr, fileNamePrefix, ReportProperties.COOKIES);
	}

	private StringBuffer processHeadersAndCookiesContent(Map<String, String> data) {
		StringBuffer sbr = new StringBuffer();

		sbr.append("<table style=\"border: 1px solid black;\">");
		data.forEach((k, v) -> {
			sbr.append("<tr style=\"border: 1px solid black;\">").append("<td style=\"border: 1px solid black;\">")
					.append(k).append("</td>").append("<td style=\"border: 1px solid black;\">").append(v)
					.append("</td>").append("</tr>");
		});
		sbr.append("</table>");
		return sbr;
	}

	private void createDisplayFiles(StringBuffer content, String fileNamePrefix, String fileNameSuffix) {
		StringBuffer sbr = new StringBuffer();
		sbr.append("<html><body>").append(content).append("</body></html>");

		StringBuffer sbrFile = new StringBuffer().append(fileNamePrefix).append(AttachmentProcessor.FILENAME_SEPARATOR)
				.append(fileNameSuffix).append(".html");

		Path path = Paths.get(reportProperties.getExtentReportDirectory(),
				ReportProperties.EXTENT_REPORT_DATA_DIRECTORY, sbrFile.toString());

		try (FileOutputStream outputStream = new FileOutputStream(path.toString())) {
			outputStream.write(content.toString().getBytes());
		} catch (IOException e) {
			throw new RestAssuredExtentReportPluginException(
					"Unable to process " + fileNameSuffix + " content for display.");
		}
	}
}
