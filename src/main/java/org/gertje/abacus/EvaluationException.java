package org.gertje.abacus;

public class EvaluationException extends AbacusException {

	public EvaluationException(String message, Token token) {
		super(message, token.getLineNumber(), token.getColumnNumber());
	}
}
