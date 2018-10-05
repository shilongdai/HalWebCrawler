package net.viperfish.crawlerApp.exceptions;

public class DependencyException extends ComponentResolutionException {

	public DependencyException() {
	}

	public DependencyException(String message) {
		super(message);
	}

	public DependencyException(String message, Throwable cause) {
		super(message, cause);
	}

	public DependencyException(Throwable cause) {
		super(cause);
	}
}
