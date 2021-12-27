package tech.grasshopper.extent.pojo;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.model.Author;
import com.aventstack.extentreports.model.Category;
import com.aventstack.extentreports.model.Device;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResultExtent {

	private String name;

	private Status status;

	@Default
	private String statusMessage = "";

	@Default
	private String stackTrace = "";

	private Date startTime;

	private Date endTime;

	private String className;

	@Default
	private Set<Author> authors = new HashSet<>();

	@Default
	private Set<Category> categories = new HashSet<>();

	@Default
	private Set<Device> devices = new HashSet<>();

	@Default
	private List<HttpLogData> dataLogs = new ArrayList<>();
}
