package org.gertje.abacus.translator.java.runtime;

import org.gertje.abacus.exception.AbacusException;

public class AbacusRuntimeException extends AbacusException {

	public AbacusRuntimeException(String message, int lineNumber, int columnNumber) {
		super(message, lineNumber, columnNumber);
	}

	public AbacusRuntimeException(String message, int lineNumber, int columnNumber, Exception cause) {
		super(message, lineNumber, columnNumber, cause);
	}
}
