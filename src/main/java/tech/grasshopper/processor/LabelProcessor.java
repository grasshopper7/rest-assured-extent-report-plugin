package tech.grasshopper.processor;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.aventstack.extentreports.model.Author;
import com.aventstack.extentreports.model.Category;

import tech.grasshopper.pojo.Label;

public class LabelProcessor {

	public Set<Category> processCategories(List<Label> labels) {
		return labels.stream().filter(l -> l.getName().equals("tag")).map(l -> new Category(l.getValue()))
				.collect(Collectors.toSet());
	}

	public Set<Author> processAuthors(List<Label> labels) {
		return labels.stream().filter(l -> l.getName().equals("owner") || l.getName().equals("lead"))
				.map(l -> new Author(l.getValue())).collect(Collectors.toSet());
	}

	public String processClassName(List<Label> labels) {
		return labels.stream().filter(l -> l.getName().equals("testClass")).findAny().get().getValue();
	}
}
