package tech.grasshopper.exception;

import java.lang.reflect.Constructor;

import javax.inject.Inject;
import javax.inject.Singleton;

import tech.grasshopper.extent.pojo.ResultExtent;
import tech.grasshopper.logging.ReportLogger;

@Singleton
public class ExceptionParser {

	private ReportLogger logger;

	@Inject
	public ExceptionParser(ReportLogger logger) {
		this.logger = logger;
	}

	public Throwable parseStackTrace(ResultExtent result) {
		String exceptionClzName = "java.lang.Exception";
		String exceptionMessage = "Error in parsing stacktrace.";

		if (!result.getStackTrace().isEmpty()) {
			String[] details = retrieveExceptionNameAndMessage(result);

			if (!details[0].isEmpty()) {
				exceptionClzName = details[0];
				exceptionMessage = details[1];
			}
		}
		return createThrowableInstance(exceptionClzName, exceptionMessage);
	}

	// Only displays exception name and message
	private String[] retrieveExceptionNameAndMessage(ResultExtent result) {
		String[] details = { "", "" };

		String[] lines = result.getStackTrace().split("\\R");

		// Exception stacktrace will always contain and end with newline character.
		if (lines.length > 0) {
			int colonIndex = lines[0].indexOf(":");

			if (colonIndex > -1) {
				// Name: Msg\Rat stacktrace\R
				details[0] = lines[0].substring(0, colonIndex);
			} else {
				// Name\Rat stacktrace\R
				details[0] = lines[0];
			}
			details[1] = result.getStatusMessage();
		}
		return details;
	}

	// Too many unwanted details in stacktrace.
	/*
	 * private String[] retrieveExceptionNameAndStack(String stackTrace) { String[]
	 * details = { "", "" };
	 * 
	 * Matcher m = Pattern.compile("\\R").matcher(stackTrace); // Exception
	 * stacktrace will always contain and end with newline character. if (m.find())
	 * { String excepNameMsg = stackTrace.substring(0, m.start());
	 * 
	 * int colonIndex = excepNameMsg.indexOf(":"); if (colonIndex > -1) { // Name:
	 * Msg\Rat stacktrace\R details[0] = excepNameMsg.substring(0, colonIndex);
	 * details[1] = stackTrace.substring(colonIndex + 2); } else { // Name\Rat
	 * stacktrace\R details[0] = excepNameMsg; details[1] =
	 * stackTrace.substring(m.start()); } } return details; }
	 */

	private Throwable createThrowableInstance(String className, String message) {
		Class<?> throwableClass = null;

		try {
			throwableClass = Class.forName(className);
			if (!Throwable.class.isAssignableFrom(throwableClass))
				throw new ClassNotFoundException();
		} catch (ClassNotFoundException e) {
			logger.warn(className + " class cannot be found or not an instance of Throwable.");
			return new Exception("Generic Exception for " + className + " : " + message);
		}
		return createThrowableInstance(className, message, throwableClass);
	}

	private Throwable createThrowableInstance(String className, String message, Class<?> throwableClass) {
		Constructor<?> throwableConstructor = null;
		Throwable throwableInstance = null;

		try {
			if (message.isEmpty()) {
				throwableConstructor = throwableClass.getConstructor();
				throwableInstance = (Throwable) throwableConstructor.newInstance();
			} else {
				try {
					throwableConstructor = throwableClass.getConstructor(String.class);
				} catch (NoSuchMethodException e) {
					throwableConstructor = throwableClass.getConstructor(Object.class);
				}
				throwableInstance = (Throwable) throwableConstructor.newInstance(message);
			}
		} catch (ReflectiveOperationException | SecurityException e) {
			logger.warn(className + " constructor cannot be found or cannot be instanciated.");
			throwableInstance = new Exception("Generic Exception for " + className + " : " + message);
		}
		return throwableInstance;
	}
}
