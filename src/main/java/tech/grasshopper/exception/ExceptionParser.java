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

	public Throwable parseStackTrace(ResultExtent result, String stackTrace) {

		String exceptionClzName = exceptionClassName(stackTrace);
		String exceptionMessage = createExceptionMessage(result.getStatusMessage(), stackTrace);

		return createThrowableInstance(exceptionClzName, exceptionMessage);
	}

	private String createExceptionMessage(String exceptionMessage, String stackTrace) {
		int firstNewLineChr = stackTrace.indexOf("\r\n");

		// Add remaining stack,if available, to message for display purposes. HACK
		if (firstNewLineChr > -1)
			exceptionMessage = exceptionMessage + " " + stackTrace.substring(firstNewLineChr + 1);
		return exceptionMessage;
	}

	private String exceptionClassName(String stackTrace) {
		int firstNewLineChr = stackTrace.indexOf("\r\n");

		// Get first line
		String exceptionClzNameMessage = (firstNewLineChr > -1) ? stackTrace.substring(0, firstNewLineChr) : stackTrace;

		// Get exception name from first line
		int colonChrIndex = exceptionClzNameMessage.indexOf(':');
		String exceptionClzName = (colonChrIndex > -1) ? exceptionClzNameMessage.substring(0, colonChrIndex)
				: exceptionClzNameMessage;
		return exceptionClzName;
	}

	private Throwable createThrowableInstance(String className, String message) {
		Class<?> throwableClass = null;
		try {
			throwableClass = Class.forName(className);

			if (!Throwable.class.isAssignableFrom(throwableClass))
				throw new ClassNotFoundException();
		} catch (ClassNotFoundException e) {
			logger.info(className + " class cannot be found or not an instance of Throwable.");
			return new Exception("Generic Exception " + message);
		}

		if (message.isEmpty())
			return createThrowableInstanceWithoutMessage(className, throwableClass);
		else
			return createThrowableInstanceWithMessage(className, message, throwableClass);
	}

	private Throwable createThrowableInstanceWithoutMessage(String className, Class<?> throwableClass) {
		Constructor<?> throwableConstructor = null;
		Throwable throwableInstance = null;
		try {
			throwableConstructor = throwableClass.getConstructor();
			throwableInstance = (Throwable) throwableConstructor.newInstance();
		} catch (ReflectiveOperationException | SecurityException | IllegalArgumentException e) {
			logger.info(className + " constructor cannot be found or cannot be instanciated.");
			throwableInstance = new Exception("Generic Exception");
		}
		return throwableInstance;
	}

	private Throwable createThrowableInstanceWithMessage(String className, String message, Class<?> throwableClass) {
		Constructor<?> throwableConstructor = null;
		Throwable throwableInstance = null;
		try {
			throwableConstructor = throwableClass.getConstructor(Object.class);
			throwableInstance = (Throwable) throwableConstructor.newInstance(message);
		} catch (ReflectiveOperationException | SecurityException | IllegalArgumentException e) {
			logger.info(className + " constructor cannot be found or cannot be instanciated.");
			throwableInstance = new Exception("Generic Exception " + message);
		}
		return throwableInstance;
	}
}
