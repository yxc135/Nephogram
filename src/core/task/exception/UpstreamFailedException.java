package core.task.exception;

public class UpstreamFailedException extends Exception {

	private static final long serialVersionUID = 1L;

	public UpstreamFailedException() {
	}

	public UpstreamFailedException(String message) {
		super(message);
	}

	public UpstreamFailedException(String message, Throwable cause) {
		super(message, cause);
	}

	public UpstreamFailedException(Throwable cause) {
		super(cause);
	}
}
