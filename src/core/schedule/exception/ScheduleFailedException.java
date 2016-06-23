package core.schedule.exception;

public class ScheduleFailedException extends Exception {

	private static final long serialVersionUID = 1L;

	public ScheduleFailedException() {
	}

	public ScheduleFailedException(String message) {
		super(message);
	}

	public ScheduleFailedException(String message, Throwable cause) {
		super(message, cause);
	}

	public ScheduleFailedException(Throwable cause) {
		super(cause);
	}
}
