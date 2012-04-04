package org.gertje.abacus;

import java.io.IOException;
import java.io.StringReader;

import org.gertje.abacus.io.LexerReader;

public abstract class AbstractLexer {

	protected LexerReader reader;

	public AbstractLexer(LexerReader reader) {
		this.reader = reader;
	}
	
	public AbstractLexer(String expression) {
		this(new LexerReader(new StringReader(expression)));
	}

	/**
	 * Geeft het volgende karakter van de expressie terug en hoogt de pointer met 1 op.
	 * @throws LexerException 
	 */
	protected char nextChar() throws LexerException {
		try {
			return (char) reader.read();
		} catch (IOException e) {
			throw new LexerException(e.getMessage(), reader.getLineNumber(), reader.getColumnNumber());
		}
	}

	/**
	 * Geeft het volgende karakter van de expressie terug, maar hoogt de pointer niet op.
	 * @throws LexerException 
	 */
	protected char peekChar() throws LexerException {
		try {
			return (char) reader.peek();
		} catch (IOException e) {
			throw new LexerException(e.getMessage(), reader.getLineNumber(), reader.getColumnNumber());
		}
	}

	/**
	 * Bepaalt of we het einde van de invoer bereikt hebben.
	 * @throws LexerException 
	 */
	protected boolean isEndOfInput() throws LexerException {
		try {
			return reader.peek() == -1;
		} catch (IOException e) {
			throw new LexerException(e.getMessage(), reader.getLineNumber(), reader.getColumnNumber());
		}
	}

	/**
	 * Geeft de volgende token terug zonder de index te veranderen.
	 * @throws LexerException
	 */
	public Token peekToken() throws LexerException {
		try {
			reader.mark(0);
			// Bepaal het volgende token.
			Token token = getNextToken();
			reader.reset();
			// Geef het token terug.
			return token;
		} catch (IOException e) {
			throw new LexerException(e.getMessage(), reader.getLineNumber(), reader.getColumnNumber());
		}
	}
	
	/**
	 * Geeft het volgende token terug en haalt deze ook van de stack (de index wordt opgehoogd).
	 * @throws LexerException
	 */
	abstract public Token getNextToken() throws LexerException;
}

