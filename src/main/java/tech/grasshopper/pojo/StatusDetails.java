package tech.grasshopper.pojo;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class StatusDetails {

	private String message = "";

	private String trace = "";
}
