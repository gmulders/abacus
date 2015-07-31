package org.gertje.abacus.exception;

public class CompilerException extends AbacusException {

	public CompilerException(String message, int lineNumber, int columnNumber, Throwable cause) {
		super(message, lineNumber, columnNumber, cause);
	}

	public CompilerException(String message, int lineNumber, int columnNumber) {
		this(message, lineNumber, columnNumber, null);
	}
}
