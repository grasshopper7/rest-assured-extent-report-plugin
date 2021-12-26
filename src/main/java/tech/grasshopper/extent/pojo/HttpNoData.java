package tech.grasshopper.extent.pojo;

import java.util.Map;

public interface HttpNoData {

	public default int rowCount() {
		return 0;
	}

	public default boolean containsHttpContentFiles() {
		return false;
	}

	public default void addPropertiesDisplay(Map<String, String> details) {

	}

	public default void addHttpContentFilesDisplay(Map<String, String> details) {

	}
}
