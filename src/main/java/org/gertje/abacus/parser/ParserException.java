package org.gertje.abacus.parser;

import org.gertje.abacus.exception.CompilerException;
import org.gertje.abacus.token.Token;
import org.gertje.abacus.lexer.LexerException;

public class ParserException extends CompilerException {
	/**
	 * Het token dat hoort bij het element waar de exceptie bij gegooid werd.
	 */
	private Token token;

	public ParserException(String message, Token token) {
		super(message, token.getLineNumber(), token.getColumnNumber());

		this.token = token;
	}

	public ParserException(String message, LexerException lexerException) {
		super(message, lexerException.getLineNumber(), lexerException.getColumnNumber(), lexerException);
	}

	public Token getToken() {
		return token;
	}
}