package org.gertje.abacus;

public class ParserException extends CompilerException {
	public ParserException(String message, Token token) {
		super(message, token.getLineNumber(), token.getColumnNumber());
	}
}
