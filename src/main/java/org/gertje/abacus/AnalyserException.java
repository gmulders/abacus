package org.gertje.abacus;

public class AnalyserException extends CompilerException {

	public AnalyserException(String message, Token token) {
		super(message, token.getLineNumber(), token.getColumnNumber());
	}

}
