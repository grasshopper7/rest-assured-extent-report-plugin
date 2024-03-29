package tech.grasshopper.processor;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Singleton;

import tech.grasshopper.extent.pojo.ResultExtent;
import tech.grasshopper.pojo.Result;
import tech.grasshopper.properties.ReportProperties;

@Singleton
public class ResultsProcessor {

	private StatusProcessor statusProcessor;
	private DateProcessor dateProcessor;
	private LabelProcessor labelProcessor;
	private ReportProperties reportProperties;
	private AttachmentProcessor attachmentProcessor;

	@Inject
	public ResultsProcessor(StatusProcessor statusProcessor, DateProcessor dateProcessor, LabelProcessor labelProcessor,
			ReportProperties reportProperties) {
		this.statusProcessor = statusProcessor;
		this.dateProcessor = dateProcessor;
		this.labelProcessor = labelProcessor;
		this.reportProperties = reportProperties;
	}

	public Map<String, List<ResultExtent>> process(List<Result> results) {
		deleteExistingAttachmentFiles();

		attachmentProcessor = AttachmentProcessor.builder()
				.allureResultsDirectory(reportProperties.getAllureResultsDirectory())
				.reportDirectory(reportProperties.getReportDirectory())
				.requestHeadersBlacklist(reportProperties.getRequestHeadersBlacklist())
				.responseHeadersBlacklist(reportProperties.getResponseHeadersBlacklist()).build();

		return results.stream()
				.collect(Collectors.mapping(r -> transformResult(r), Collectors.groupingBy(e -> e.getClassName())));
	}

	private void deleteExistingAttachmentFiles() {
		Path path = Paths.get(reportProperties.getReportDirectory(), ReportProperties.EXTENT_REPORT_DATA_DIRECTORY);

		if (Files.exists(path))
			Arrays.stream(new File(path.toString()).listFiles()).forEach(File::delete);
	}

	private ResultExtent transformResult(Result result) {
		return ResultExtent.builder().name(result.getName()).status(statusProcessor.process(result.getStatus()))
				.statusMessage(result.getStatusDetails().getMessage()).stackTrace(result.getStatusDetails().getTrace())
				.startTime(dateProcessor.process(result.getStart())).endTime(dateProcessor.process(result.getStop()))
				.className(labelProcessor.processClassName(result.getLabels()))
				.categories(labelProcessor.processCategories(result.getLabels()))
				.authors(labelProcessor.processAuthors(result.getLabels()))
				.devices(labelProcessor.processDevices(result.getLabels()))
				.dataLogs(attachmentProcessor.process(result.getAttachments())).build();
	}
}
