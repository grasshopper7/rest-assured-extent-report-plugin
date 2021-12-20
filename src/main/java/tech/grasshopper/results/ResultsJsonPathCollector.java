package tech.grasshopper.results;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Singleton;

import tech.grasshopper.exception.RestAssuredExtentReportPluginException;

@Singleton
public class ResultsJsonPathCollector {

	public List<Path> retrievePaths(String resultsDirectory) {
		List<Path> resultFilePaths = null;
		try {
			resultFilePaths = Files.walk(Paths.get(resultsDirectory)).filter(Files::isRegularFile)
					.filter(p -> p.toString().toLowerCase().endsWith("-result.json")).collect(Collectors.toList());
		} catch (IOException e) {
			throw new RestAssuredExtentReportPluginException(
					"Unable to navigate Json result folder. Stopping report creation. "
							+ "Check the 'extentreport.restAssuredResultsDirectory' plugin configuration.");
		}

		if (resultFilePaths == null || resultFilePaths.size() == 0)
			throw new RestAssuredExtentReportPluginException("No Allure Json Result found. Stopping report creation. "
					+ "Check the 'extentreport.restAssuredResultsDirectory' plugin configuration.");
		return resultFilePaths;
	}
}
