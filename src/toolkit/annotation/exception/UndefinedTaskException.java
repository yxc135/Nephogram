package toolkit.annotation.exception;

public class UndefinedTaskException extends Exception {

	private static final long serialVersionUID = 1L;

	public UndefinedTaskException() {
	}

	public UndefinedTaskException(String message) {
		super(message);
	}

	public UndefinedTaskException(String message, Throwable cause) {
		super(message, cause);
	}

	public UndefinedTaskException(Throwable cause) {
		super(cause);
	}

}