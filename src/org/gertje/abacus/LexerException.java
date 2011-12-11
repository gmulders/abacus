package org.gertje.abacus;

class LexerException extends CompilerException {

	public LexerException(String message, int lineNumber, int columnNumber) {
		super(message, lineNumber, columnNumber);
	}

}
