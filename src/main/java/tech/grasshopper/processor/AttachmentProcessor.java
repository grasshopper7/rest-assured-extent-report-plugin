package tech.grasshopper.processor;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import tech.grasshopper.extent.pojo.HttpData;
import tech.grasshopper.extent.pojo.HttpLogData;
import tech.grasshopper.extent.pojo.HttpRequestData;
import tech.grasshopper.extent.pojo.HttpResponseData;
import tech.grasshopper.pojo.Attachment;
import tech.grasshopper.properties.ReportProperties;

@Singleton
public class AttachmentProcessor {

	private Document document;

	private HttpData httpData;

	private String fileNamePrefix = "";

	private ReportProperties reportProperties;

	@Inject
	public AttachmentProcessor(ReportProperties reportProperties) {
		this.reportProperties = reportProperties;
	}

	public List<HttpLogData> process(List<Attachment> attachments) {
		List<HttpLogData> httpLogData = new ArrayList<>();
		HttpLogData log = null;

		for (Attachment attachment : attachments) {
			Path path = Paths.get(reportProperties.getAllureResultsDirectory(), attachment.getSource());

			try {
				document = Jsoup.parse(path.toFile(), null);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			fileNamePrefix = retrieveFileNamePrefix(attachment.getSource());

			Element methodUrlOrStatus = document.selectFirst("body > div");
			httpData = HttpData.createHttpData(methodUrlOrStatus.text());

			if (httpData instanceof HttpRequestData) {
				log = HttpLogData.builder().build();
				log.setHttpRequestData((HttpRequestData) httpData);
				httpLogData.add(log);
			} else
				log.setHttpResponseData((HttpResponseData) httpData);

			processBodyContent();
			processHeaders();
			processCookies();
		}
		return httpLogData;
	}

	private String retrieveFileNamePrefix(String source) {
		return source.substring(0, source.lastIndexOf('-'));
	}

	private void processBodyContent() {
		Elements body = document.getElementsContainingOwnText(ReportProperties.BODY);
		if (!body.isEmpty()) {
			String content = body.get(0).nextElementSibling().child(0).text();

			if (content.length() > 0) {
				StringBuffer sbr = new StringBuffer();
				sbr.append("<div><pre>").append(content).append("</pre></div>");

				createDisplayFiles(sbr, ReportProperties.BODY);
				httpData.setBodyContentFile(fileNamePrefix);
			}
		}
	}

	private void processHeaders() {
		if (processHeadersAndCookies(ReportProperties.HEADERS))
			httpData.setHeadersContentFile(fileNamePrefix);
	}

	private void processCookies() {
		if (processHeadersAndCookies(ReportProperties.COOKIES))
			httpData.setCookiesContentFile(fileNamePrefix);
	}

	private boolean processHeadersAndCookies(String type) {
		Elements elements = document.getElementsContainingOwnText(type);
		if (!elements.isEmpty()) {
			Elements details = elements.get(0).nextElementSibling().children();

			Map<String, String> data = processDataHeadersAndCookies(details);
			if (!data.isEmpty()) {
				createDisplayFiles(tableHeadersAndCookies(data), type);
				return true;
			}
		}
		return false;
	}

	private Map<String, String> processDataHeadersAndCookies(Elements elements) {
		Map<String, String> data = new HashMap<>();
		elements.forEach(e -> {
			if (e.text().contains(":")) {
				String[] details = e.text().split(":");
				data.put(details[0], details[1]);
			}
		});
		return data;
	}

	private StringBuffer tableHeadersAndCookies(Map<String, String> data) {
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

	private void createDisplayFiles(StringBuffer content, String fileNameSuffix) {
		StringBuffer sbr = new StringBuffer();
		sbr.append("<html><body>").append(content).append("</body></html>");

		StringBuffer sbrFile = new StringBuffer();
		sbrFile.append(reportProperties.getExtentReportDirectory()).append("/")
				.append(ReportProperties.EXTENT_REPORT_DATA_DIRECTORY).append("/").append(fileNamePrefix).append("-")
				.append(fileNameSuffix).append(".html");

		try (FileOutputStream outputStream = new FileOutputStream(sbrFile.toString())) {
			outputStream.write(content.toString().getBytes());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
