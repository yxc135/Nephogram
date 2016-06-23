package core.task.exception;

public class MaxRetryException extends Exception {

	private static final long serialVersionUID = 1L;

	public MaxRetryException() {
	}

	public MaxRetryException(String message) {
		super(message);
	}

	public MaxRetryException(String message, Throwable cause) {
		super(message, cause);
	}

	public MaxRetryException(Throwable cause) {
		super(cause);
	}

}