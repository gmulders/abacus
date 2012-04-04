package org.gertje.abacus;

public class LexerException extends CompilerException {

	public LexerException(String message, int lineNumber, int columnNumber) {
		super(message, lineNumber, columnNumber);
	}

}
