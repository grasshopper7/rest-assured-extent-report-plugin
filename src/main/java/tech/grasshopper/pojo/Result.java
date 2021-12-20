package tech.grasshopper.pojo;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Result {

	private String name;

	private String status;

	private StatusDetails statusDetails = new StatusDetails();

	private List<Attachment> attachments = new ArrayList<>();

	private long start;

	private long stop;

	private String fullName;

	private List<Label> labels = new ArrayList<>();

}
