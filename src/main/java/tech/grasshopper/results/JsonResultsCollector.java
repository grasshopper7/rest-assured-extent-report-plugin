package tech.grasshopper.results;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import tech.grasshopper.exception.RestAssuredExtentReportPluginException;
import tech.grasshopper.logging.ReportLogger;
import tech.grasshopper.pojo.Result;

@Singleton
public class JsonResultsCollector {

	private ReportLogger logger;

	@Inject
	public JsonResultsCollector(ReportLogger logger) {
		this.logger = logger;
	}

	public List<Result> retrieveResults(String resultsDirectory) {
		List<Path> resultFilePaths = retrievePaths(resultsDirectory);
		Gson gson = new GsonBuilder().create();

		List<Result> results = new ArrayList<>();
		Result result = null;

		for (Path resultFilePath : resultFilePaths) {
			try {
				result = gson.fromJson(Files.newBufferedReader(resultFilePath), Result.class);
			} catch (JsonSyntaxException | JsonIOException | IOException e) {
				logger.warn(String.format("Skipping result report at '%s', as unable to parse result to Result pojo.",
						resultFilePath));
				continue;
			}

			if (result == null) {
				logger.warn(
						String.format("Skipping result report at '%s', parsing result report returned no Result pojo.",
								resultFilePath));
				continue;
			}
			results.addAll(Arrays.asList(result));
		}

		if (results.size() == 0)
			throw new RestAssuredExtentReportPluginException("No Result found in report. Stopping report creation. "
					+ "Check the 'extentreport.allureResultsDirectory' plugin configuration.");

		return results;
	}

	private List<Path> retrievePaths(String resultsDirectory) {
		List<Path> resultFilePaths = null;
		try {
			resultFilePaths = Files.walk(Paths.get(resultsDirectory)).filter(Files::isRegularFile)
					.filter(p -> p.toString().toLowerCase().endsWith("-result.json")).collect(Collectors.toList());
		} catch (IOException e) {
			throw new RestAssuredExtentReportPluginException(
					"Unable to navigate Json result folder. Stopping report creation. "
							+ "Check the 'extentreport.allureResultsDirectory' plugin configuration.");
		}

		if (resultFilePaths == null || resultFilePaths.size() == 0)
			throw new RestAssuredExtentReportPluginException("No Allure Json Result found. Stopping report creation. "
					+ "Check the 'extentreport.allureResultsDirectory' plugin configuration.");
		return resultFilePaths;
	}
}
