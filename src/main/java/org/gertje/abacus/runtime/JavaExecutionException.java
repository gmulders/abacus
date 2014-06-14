package org.gertje.abacus.runtime;

import org.gertje.abacus.AbacusException;

public class JavaExecutionException extends AbacusException {

	public JavaExecutionException(String message, int lineNumber, int columnNumber) {
		super(message, lineNumber, columnNumber);
	}

	public JavaExecutionException(String message, int lineNumber, int columnNumber, Exception cause) {
		super(message, lineNumber, columnNumber, cause);
	}
}
