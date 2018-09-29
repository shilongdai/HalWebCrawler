package net.viperfish.crawlerApp.exceptions;

public class UnsupportedComponentException extends Exception {

	public UnsupportedComponentException() {
	}

	public UnsupportedComponentException(String message) {
		super(message);
	}

	public UnsupportedComponentException(String message, Throwable cause) {
		super(message, cause);
	}

	public UnsupportedComponentException(Throwable cause) {
		super(cause);
	}
}
