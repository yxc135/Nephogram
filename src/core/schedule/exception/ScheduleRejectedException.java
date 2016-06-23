package core.schedule.exception;

public class ScheduleRejectedException extends ScheduleFailedException {

	private static final long serialVersionUID = 1L;

	public ScheduleRejectedException() {
	}

	public ScheduleRejectedException(String message) {
		super(message);
	}

	public ScheduleRejectedException(String message, Throwable cause) {
		super(message, cause);
	}

	public ScheduleRejectedException(Throwable cause) {
		super(cause);
	}

}
