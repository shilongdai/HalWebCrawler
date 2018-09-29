package net.viperfish.crawlerApp.exceptions;

public class ConflictingDependenciesException extends DependencyException {

	public ConflictingDependenciesException() {
	}

	public ConflictingDependenciesException(String message) {
		super(message);
	}

	public ConflictingDependenciesException(String message, Throwable cause) {
		super(message, cause);
	}

	public ConflictingDependenciesException(Throwable cause) {
		super(cause);
	}
}
