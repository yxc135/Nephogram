package toolkit.annotation.exception;

public class AnnotationParseException extends Exception {

	private static final long serialVersionUID = 1L;

	public AnnotationParseException() {
	}

	public AnnotationParseException(String message) {
		super(message);
	}

	public AnnotationParseException(String message, Throwable cause) {
		super(message, cause);
	}

	public AnnotationParseException(Throwable cause) {
		super(cause);
	}

}
