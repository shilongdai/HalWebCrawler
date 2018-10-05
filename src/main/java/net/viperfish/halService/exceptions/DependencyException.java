package net.viperfish.halService.exceptions;

public class DependencyException extends Exception {

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
