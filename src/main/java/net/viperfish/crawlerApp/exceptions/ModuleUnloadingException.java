package net.viperfish.crawlerApp.exceptions;

public class ModuleUnloadingException extends Exception {

	public ModuleUnloadingException() {
	}

	public ModuleUnloadingException(String message) {
		super(message);
	}

	public ModuleUnloadingException(String message, Throwable cause) {
		super(message, cause);
	}

	public ModuleUnloadingException(Throwable cause) {
		super(cause);
	}
}
