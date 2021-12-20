package tech.grasshopper.processor;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Singleton;

import tech.grasshopper.extent.pojo.ResultExtent;
import tech.grasshopper.pojo.Result;

@Singleton
public class ResultsProcessor {

	private StatusProcessor statusProcessor;
	private DateProcessor dateProcessor;
	private LabelProcessor labelProcessor;
	private AttachmentProcessor attachmentProcessor;

	@Inject
	public ResultsProcessor(StatusProcessor statusProcessor, DateProcessor dateProcessor, LabelProcessor labelProcessor,
			AttachmentProcessor attachmentProcessor) {
		this.statusProcessor = statusProcessor;
		this.dateProcessor = dateProcessor;
		this.labelProcessor = labelProcessor;
		this.attachmentProcessor = attachmentProcessor;
	}

	public Map<String, List<ResultExtent>> process(List<Result> results) {
		return results.stream()
				.collect(Collectors.mapping(r -> transformResult(r), Collectors.groupingBy(e -> e.getClassName())));
	}

	private ResultExtent transformResult(Result result) {
		return ResultExtent.builder().name(result.getName()).status(statusProcessor.process(result.getStatus()))
				.statusMessage(result.getStatusDetails().getMessage()).stackTrace(result.getStatusDetails().getTrace())
				.startTime(dateProcessor.process(result.getStart())).endTime(dateProcessor.process(result.getStop()))
				.className(labelProcessor.processClassName(result.getLabels()))
				.categories(labelProcessor.processCategories(result.getLabels()))
				.authors(labelProcessor.processAuthors(result.getLabels()))
				.dataLogs(attachmentProcessor.process(result.getAttachments())).build();
	}
}
