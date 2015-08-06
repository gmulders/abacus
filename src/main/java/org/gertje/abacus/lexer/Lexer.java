package org.gertje.abacus.lexer;

import org.gertje.abacus.token.Token;

/**
 * Deze interface definieert een aantal methoden die een lexer standaard moet implementeren.
 */
public interface Lexer {

	/**
	 * Geeft de volgende token terug zonder de index te veranderen.
	 * @return Het volgende token.
	 * @throws LexerException
	 */
	Token peekToken() throws LexerException;

	/**
	 * Geeft het volgende token terug en haalt deze ook van de stack (de index wordt opgehoogd).
	 * @return Het volgende token.
	 * @throws LexerException
	 */
	Token getNextToken() throws LexerException;
}
