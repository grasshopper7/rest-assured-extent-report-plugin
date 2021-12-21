package tech.grasshopper.processor.attachment;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Singleton;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import tech.grasshopper.exception.RestAssuredExtentReportPluginException;
import tech.grasshopper.properties.ReportProperties;

@Singleton
public class HtmlParser {

	private Document document;

	public void initialize(Path filePath) throws IOException {
		document = Jsoup.parse(filePath.toFile(), null);
	}

	public String retrieveMethodUrlOrStatusText() {
		Element elem = document.selectFirst("body > div");
		if (elem == null)
			throw new RestAssuredExtentReportPluginException("Http method, endpoint or status code not available.");
		return elem.text();
	}

	public String retrieveBodyContent() {
		Elements body = document.getElementsContainingOwnText(ReportProperties.BODY);
		return (body.isEmpty()) ? "" : body.get(0).nextElementSibling().child(0).text();
	}

	public Map<String, String> retrieveHeadersContent() {
		return retrieveHeadersAndCookies(ReportProperties.HEADERS);
	}

	public Map<String, String> retrieveCookiesContent() {
		return retrieveHeadersAndCookies(ReportProperties.COOKIES);
	}

	private Map<String, String> retrieveHeadersAndCookies(String type) {
		Elements elements = document.getElementsContainingOwnText(type);
		Map<String, String> data = new HashMap<>();

		if (!elements.isEmpty()) {
			Elements details = elements.get(0).nextElementSibling().children();
			details.forEach(e -> {
				if (e.text().contains(":")) {
					String[] detail = e.text().split(":");
					data.put(detail[0], detail[1]);
				}
			});
		}
		return data;
	}
}
