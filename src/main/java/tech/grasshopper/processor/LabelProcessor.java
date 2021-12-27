package tech.grasshopper.processor;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.aventstack.extentreports.model.Author;
import com.aventstack.extentreports.model.Category;
import com.aventstack.extentreports.model.Device;

import tech.grasshopper.allure.LabelAnnotationNames;
import tech.grasshopper.pojo.Label;

public class LabelProcessor {

	public Set<Category> processCategories(List<Label> labels) {
		return labels.stream().filter(l -> l.getName().equals(LabelAnnotationNames.CATEGORY_LABEL_NAME))
				.map(l -> new Category(l.getValue())).collect(Collectors.toSet());
	}

	public Set<Author> processAuthors(List<Label> labels) {
		return labels.stream().filter(l -> l.getName().equals(LabelAnnotationNames.AUTHOR_LABEL_NAME))
				.map(l -> new Author(l.getValue())).collect(Collectors.toSet());
	}

	public Set<Device> processDevices(List<Label> labels) {
		return labels.stream().filter(l -> l.getName().equals(LabelAnnotationNames.DEVICE_LABEL_NAME))
				.map(l -> new Device(l.getValue())).collect(Collectors.toSet());
	}

	public String processClassName(List<Label> labels) {
		return labels.stream().filter(l -> l.getName().equals("testClass")).findAny().get().getValue();
	}
}
