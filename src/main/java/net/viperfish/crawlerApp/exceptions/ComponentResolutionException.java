package net.viperfish.crawlerApp.exceptions;

public class ComponentResolutionException extends Exception {

	public ComponentResolutionException() {
	}

	public ComponentResolutionException(String message) {
		super(message);
	}

	public ComponentResolutionException(String message, Throwable cause) {
		super(message, cause);
	}

	public ComponentResolutionException(Throwable cause) {
		super(cause);
	}
}
