package org.gertje.abacus.lexer;

import org.gertje.abacus.token.Token;
import org.gertje.abacus.lexer.io.LexerReader;

import java.io.IOException;
import java.io.StringReader;

public abstract class AbstractLexer implements Lexer {

	protected LexerReader reader;

	public AbstractLexer(LexerReader reader) {
		this.reader = reader;
	}
	
	public AbstractLexer(String expression) {
		this(new LexerReader(new StringReader(expression)));
	}

	/**
	 * Geeft het volgende karakter uit de input terug en hoogt de pointer met 1 op.
	 * @return Het volgende karakter uit de input.
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
	 * Geeft het volgende karakter uit de input terug, maar hoogt de pointer niet op.
	 * @return Het volgende karakter uit de input.
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
	 * Bepaalt of het einde van de invoer bereikt is.
	 * @return <code>true</code> wanneer het einde van de invoer bereikt is, anders <code>false</code>.
	 * @throws LexerException 
	 */
	protected boolean isEndOfInput() throws LexerException {
		return isEndOfInput(0);
	}

	/**
	 * Bepaalt of offset tekens vooruit het einde van de invoer bereikt is.
	 * @param offset Het aantal tekens dat vooruit gekeken moet worden.
	 * @return <code>true</code> wanneer offset tekens vooruit het einde van de invoer bereikt is, anders <code>false</code>.
	 * @throws LexerException
	 */
	protected boolean isEndOfInput(int offset) throws LexerException {
		try {
			return reader.peek(offset) == -1;
		} catch (IOException e) {
			throw new LexerException(e.getMessage(), reader.getLineNumber(), reader.getColumnNumber());
		}
	}

	/**
	 * Geeft de volgende token terug zonder de index te veranderen.
	 * @return Het volgende token.
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
}

