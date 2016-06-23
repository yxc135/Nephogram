package core.schedule.exception;

public class CircularGraphException extends ScheduleFailedException {

	private static final long serialVersionUID = 1L;

	public CircularGraphException() {
	}

	public CircularGraphException(String message) {
		super(message);
	}

	public CircularGraphException(String message, Throwable cause) {
		super(message, cause);
	}

	public CircularGraphException(Throwable cause) {
		super(cause);
	}

}
