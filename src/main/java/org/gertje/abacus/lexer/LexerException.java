package org.gertje.abacus.lexer;

import org.gertje.abacus.exception.CompilerException;

public class LexerException extends CompilerException {

	public LexerException(String message, int lineNumber, int columnNumber) {
		super(message, lineNumber, columnNumber);
	}

}
