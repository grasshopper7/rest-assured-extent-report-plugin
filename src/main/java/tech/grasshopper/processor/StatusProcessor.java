package tech.grasshopper.processor;

import javax.inject.Singleton;

import com.aventstack.extentreports.Status;

@Singleton
public class StatusProcessor {

	public Status process(String status) {
		switch (status) {
		case "passed":
			return Status.PASS;
		case "failed":
			return Status.FAIL;
		case "skipped":
			return Status.SKIP;
		default:
			return Status.FAIL;
		}
	}
}
