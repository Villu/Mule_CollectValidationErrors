package exceptions;


public class FilterUnacceptedException extends RuntimeException {
	private static final long serialVersionUID = 7042508132998750980L;

	public FilterUnacceptedException() {
	}

	public FilterUnacceptedException(String message) {
		super(message);
	}

	public FilterUnacceptedException(Throwable cause) {
		super(cause);
	}

	public FilterUnacceptedException(String message, Throwable cause) {
		super(message, cause);
	}

}
