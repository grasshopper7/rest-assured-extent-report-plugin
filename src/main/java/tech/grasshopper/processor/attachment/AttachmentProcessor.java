package tech.grasshopper.processor.attachment;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import tech.grasshopper.exception.RestAssuredExtentReportPluginException;
import tech.grasshopper.extent.pojo.HttpData;
import tech.grasshopper.extent.pojo.HttpLogData;
import tech.grasshopper.extent.pojo.HttpRequestData;
import tech.grasshopper.extent.pojo.HttpResponseData;
import tech.grasshopper.logging.ReportLogger;
import tech.grasshopper.pojo.Attachment;
import tech.grasshopper.properties.ReportProperties;

@Singleton
public class AttachmentProcessor {

	private HttpData httpData;
	private HttpLogData log;
	List<HttpLogData> httpLogData;
	private String fileNamePrefix = "";

	private AttachmentContentProcessor attachmentContentProcessor;

	private ReportProperties reportProperties;

	private HtmlParser htmlParser;

	private ReportLogger logger;

	@Inject
	public AttachmentProcessor(AttachmentContentProcessor attachmentContentProcessor, ReportProperties reportProperties,
			HtmlParser htmlParser, ReportLogger logger) {
		this.attachmentContentProcessor = attachmentContentProcessor;
		this.reportProperties = reportProperties;
		this.htmlParser = htmlParser;
		this.logger = logger;
	}

	public List<HttpLogData> process(List<Attachment> attachments) {
		httpLogData = new ArrayList<>();

		for (Attachment attachment : attachments) {
			Path path = Paths.get(reportProperties.getAllureResultsDirectory(), attachment.getSource());

			try {
				htmlParser.initialize(path);
			} catch (IOException e) {
				logger.info("Skipping attachment as unable to access file - " + path.toString());
				continue;
			}

			String methodUrlOrStatusTxt = "";
			try {
				methodUrlOrStatusTxt = htmlParser.retrieveMethodUrlOrStatusText();
			} catch (RestAssuredExtentReportPluginException e) {
				logger.info(e.getMessage() + " Skipping attachment - " + path.toString());
				continue;
			}

			httpData = HttpData.createHttpData(methodUrlOrStatusTxt);

			if (attachment.getSource().indexOf('-') == -1) {
				logger.info("Skipping attachment as file name not correct - " + path.toString());
				continue;
			}
			fileNamePrefix = retrieveFileNamePrefix(attachment.getSource());

			createOrUpdateHttpLogData();
			processContent(path);
		}
		return httpLogData;
	}

	private void processContent(Path path) {
		try {
			processBodyContent();
		} catch (RestAssuredExtentReportPluginException e) {
			logger.info(e.getMessage() + " Skipping body for - " + path.toString());
		}
		try {
			processHeaders();
		} catch (RestAssuredExtentReportPluginException e) {
			logger.info(e.getMessage() + " Skipping headers for - " + path.toString());
		}
		try {
			processCookies();
		} catch (RestAssuredExtentReportPluginException e) {
			logger.info(e.getMessage() + " Skipping cookies for - " + path.toString());
		}
	}

	private String retrieveFileNamePrefix(String source) {
		return source.substring(0, source.lastIndexOf('-'));
	}

	private void createOrUpdateHttpLogData() {
		if (httpData instanceof HttpRequestData) {
			log = HttpLogData.builder().build();
			log.setHttpRequestData((HttpRequestData) httpData);
			httpLogData.add(log);
		} else
			log.setHttpResponseData((HttpResponseData) httpData);
	}

	private void processBodyContent() {
		String content = htmlParser.retrieveBodyContent();
		if (content.length() > 0) {
			attachmentContentProcessor.processBodyContent(content, fileNamePrefix);
			httpData.setBodyContentFile(fileNamePrefix);
		}
	}

	private void processHeaders() {
		Map<String, String> headers = htmlParser.retrieveHeadersContent();
		if (!headers.isEmpty()) {
			attachmentContentProcessor.processHeadersContent(headers, fileNamePrefix);
			httpData.setHeadersContentFile(fileNamePrefix);
		}
	}

	private void processCookies() {
		Map<String, String> cookies = htmlParser.retrieveCookiesContent();
		if (!cookies.isEmpty()) {
			attachmentContentProcessor.processCookiesContent(cookies, fileNamePrefix);
			httpData.setCookiesContentFile(fileNamePrefix);
		}
	}
}
