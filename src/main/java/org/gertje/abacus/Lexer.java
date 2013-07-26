package org.gertje.abacus;

/**
 * Deze interface definieert een aantal methoden die een lexer standaard moet implementeren.
 */
public interface Lexer {

	/**
	 * Geeft de volgende token terug zonder de index te veranderen.
	 * @return Het volgende token.
	 * @throws LexerException
	 */
	public Token peekToken() throws LexerException;

	/**
	 * Geeft het volgende token terug en haalt deze ook van de stack (de index wordt opgehoogd).
	 * @return Het volgende token.
	 * @throws LexerException
	 */
	public Token getNextToken() throws LexerException;
}
