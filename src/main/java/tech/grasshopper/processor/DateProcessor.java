package tech.grasshopper.processor;

import java.util.Date;

import javax.inject.Singleton;

@Singleton
public class DateProcessor {

	public Date process(Long instant) {
		return new Date(instant);
	}
}
