package org.gertje.abacus;

public class ParserException extends CompilerException {
	/**
	 * Het token dat hoort bij het element waar de exceptie bij gegooid werd.
	 */
	private Token token;

	public ParserException(String message, Token token) {
		super(message, token.getLineNumber(), token.getColumnNumber());

		this.token = token;
	}

	public Token getToken() {
		return token;
	}
}