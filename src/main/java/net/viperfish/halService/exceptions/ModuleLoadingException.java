package net.viperfish.halService.exceptions;

public class ModuleLoadingException extends Exception {

	public ModuleLoadingException() {
	}

	public ModuleLoadingException(String message) {
		super(message);
	}

	public ModuleLoadingException(String message, Throwable cause) {
		super(message, cause);
	}

	public ModuleLoadingException(Throwable cause) {
		super(cause);
	}
}
