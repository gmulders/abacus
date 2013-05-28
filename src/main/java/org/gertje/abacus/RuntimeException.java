package org.gertje.abacus;

public class RuntimeException extends AbacusException {

	public RuntimeException(String message, Token token) {
		super(message, token.getLineNumber(), token.getColumnNumber());
	}
}
