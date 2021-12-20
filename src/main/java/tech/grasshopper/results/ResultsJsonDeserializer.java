package tech.grasshopper.results;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
public class ResultsJsonDeserializer {

	private ReportLogger logger;

	@Inject
	public ResultsJsonDeserializer(ReportLogger logger) {
		this.logger = logger;
	}

	public List<Result> retrieveResults(List<Path> resultFilePaths) {
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
}
