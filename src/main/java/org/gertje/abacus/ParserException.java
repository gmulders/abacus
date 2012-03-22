package org.gertje.abacus;

class ParserException extends CompilerException {
	public ParserException(String message, Token token) {
		super(message, token.getLineNumber(), token.getColumnNumber());
	}
}
