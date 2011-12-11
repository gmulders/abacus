package org.gertje.abacus;

import org.gertje.abacus.Token.TokenType;

class Lexer {

	private String expression;

	private int expressionLength;
	private int index;

	private int columnNumber;
	private int lineNumber;

	public Lexer(String expression) {
		this.expression = expression;
		index = 0;
		expressionLength = expression.length();

		columnNumber = 1;
		lineNumber = 1;
	}

	/**
	 * Geeft het volgende karakter van de expressie terug en hoogt de pointer met 1 op.
	 */
	private char nextChar() {
		columnNumber++;
		return expression.charAt(index++);
	}

	/**
	 * Geeft het volgende karakter van de expressie terug, maar hoogt de pointer niet op.
	 */
	private char peekChar() {
		return expression.charAt(index);
	}

	/**
	 * Bepaalt of we het einde van de invoer bereikt hebben.
	 */
	private boolean isEndOfInput() {
		return index == expressionLength;
	}

	/**
	 * Bepaalt of het karakter numeriek is.
	 */
	private boolean isNumeric(char c) {
		return (c == '.' || c >= '0' && c <= '9');
	}

	/**
	 * Bepaalt of het karakter een letter is of een underscore.
	 */
	private boolean isAlphaOrUnderscore(char c) {
		return (c == '_' || c >= 'A' && c <= 'Z' || c >= 'a' && c <= 'z');
	}

	/**
	 * Bepaalt of het karakter een letter, een undersore of een getal is.
	 */
	private boolean isAlphaNumericOrUnderscore(char c) {
		return (isNumeric(c) || isAlphaOrUnderscore(c));
	}

	/**
	 * Geeft de volgende token terug zonder de index te veranderen.
	 * @throws LexerException
	 */
	public Token peekToken() throws LexerException {
		// Sla de index op in een tijdelijke variabele.
		int tempIndex = index;
		int tempColumnNumber = columnNumber;
		int tempLineNumber = lineNumber;

		// Bepaal het volgende token.
		Token token = getNextToken();

		// Reset de index naar zijn oude waarde.
		index = tempIndex;
		columnNumber = tempColumnNumber;
		lineNumber = tempLineNumber;

		// Geef het token terug.
		return token;
	}
	
	/**
	 * Geeft het volgende token terug en haalt deze ook van de stack (de index wordt opgehoogt).
	 * @throws LexerException
	 */
	public Token getNextToken() throws LexerException {
		// We bepalen het volgende token, maar geven deze alleen terug als het niet whitespace of een nieuwe regel is.
		Token nextToken = determineNextToken();

		// Haal net zo lang een nieuw token op totdat het geen whitespace of een nieuwe regel is.
		while (nextToken.getType() == TokenType.WHITE_SPACE
				|| nextToken.getType() == TokenType.NEW_LINE) {
			// Wanneer het token een nieuwe regel was hogen we het regelnummer met 1 op en zetten we het kolomnr op 1.
            if (nextToken.getType() == TokenType.NEW_LINE) {
				lineNumber++;
				columnNumber = 1;
			}
			nextToken = determineNextToken();
		}
		return nextToken;
	}
	
	/**
	 * Bepaalt het volgende token.
	 * @throws LexerException 
	 */
	private Token determineNextToken() throws LexerException {
		// Maak een nieuw token object aan.
		Token token = new Token(lineNumber, columnNumber);

		// Wanneer de index gelijk is aan de lengte van de string met de expressie geven we een Token die het einde van
		// de expressie weergeeft terug.
		if (isEndOfInput()) {
			token.setType(TokenType.END_OF_INPUT);
			return token;
		}
		// Haal het volgende karakter op uit de expressie.
		char c = nextChar();

		// Controleer wat het karakter voorstelt en doe de bijbehorende bewerking.
		// Whitespace
		if (c == ' ' || c == '\t') {
			token.setType(TokenType.WHITE_SPACE);

		// Einde van een expressie
		} else if (c == ';') {
			token.setType(TokenType.END_OF_EXPRESSION);

		// New line
		} else if (c == '\n' || c == '\r') {
			// Wanneer het huidige teken een \n is moeten we controleren of het volgende teken een \r is en deze ook
			// van de stack halen. (index ophogen)
			if (c == '\n' && !isEndOfInput() && peekChar() == '\r') {
				nextChar();
			}
			token.setType(TokenType.NEW_LINE);

		// identifier
		} else if (isAlphaOrUnderscore(c)) {
			token.setType(TokenType.IDENTIFIER);
			token.setValue(buildIdentifier(c));

		// Linkerhaakje
		} else if (c == '(') {
			token.setType(TokenType.LEFT_PARENTHESIS);

		// Rechterhaakje
		} else if (c == ')') {
			token.setType(TokenType.RIGHT_PARENTHESIS);

		// String literal
		} else if (c == '\'') {
			token.setType(TokenType.STRING);
			token.setValue(buildString());

		// Nummer
		} else if (isNumeric(c)) {
			token.setType(TokenType.NUMBER);
			token.setValue(buildNumber(c));

		// Boolean AND
		} else if (c == '&') {
			token.setType(TokenType.BOOLEAN_AND);
			token.setValue(buildBoolean(c));

		// Boolean OR
		} else if (c == '|') {
			token.setType(TokenType.BOOLEAN_OR);
			token.setValue(buildBoolean(c));

		// Plus
		} else if (c == '+') {
			token.setType(TokenType.PLUS);
		
		// Min
		} else if (c == '-') {
			token.setType(TokenType.MINUS);
	
		// Macht
		} else if (c == '^') {
			token.setType(TokenType.POWER);
	
		// Keer
		} else if (c == '*') {
			token.setType(TokenType.MULTIPLY);

		// Delen
		} else if (c == '/') {
			token.setType(TokenType.DIVIDE);

		} else if (c == '%') {
			token.setType(TokenType.PERCENT);

		// NOT
		} else if (c == '!') {
			// Wanneer het volgende teken bestaat en het is een = teken geven we een NEQ_COMPARISON terug.
			if (!isEndOfInput() && peekChar() == '=') {
				nextChar();
				token.setType(TokenType.NEQ);
			} else {
				// We geven gewoon een NOT terug.
				token.setType(TokenType.NOT);
			}
		// Kleiner dan
		} else if (c == '<') {
			// Wanneer het volgende teken bestaat en het is een = teken geven we een LEQ terug.
			if (!isEndOfInput() && peekChar() == '=') {
				nextChar();
				token.setType(TokenType.LEQ);
			} else {
				// We geven gewoon LT terug.
				token.setType(TokenType.LT);
			}
		// Groter dan
		} else if (c == '>') {
			// Wanneer het volgende teken bestaat en het is een = teken geven we een GEQ terug.
			if (!isEndOfInput() && peekChar() == '=') {
				nextChar();
				token.setType(TokenType.GEQ);
			} else {
				// We geven gewoon GT terug.
				token.setType(TokenType.GT);
			}
		// gelijk aan
		} else if (c == '=') {
			// Wanneer het volgende teken bestaat en het is een = teken gooien we een EQ terug.
			if (isEndOfInput() || peekChar() == '=') {
				nextChar();
				token.setType(TokenType.EQ);
			} else {
				// Geef een ASSIGNMENT terug.
				nextChar();
				token.setType(TokenType.ASSIGNMENT);
			}
		// If (?)
		} else if (c == '?') {
			token.setType(TokenType.IF);

		// ELSE (:)
		} else if (c == ':') {
			token.setType(TokenType.ELSE);

		// COMMA
		} else if (c == ',') {
			token.setType(TokenType.COMMA);
		}

		// Controleer of er een bekend karakter gevonden wordt.
		if (token.getType() == null) {
			// Het karakter wordt niet herkent, gooi een exceptie.
			throw new LexerException("Non expected character found: '" + c + "'", lineNumber, columnNumber);
		}

		// Geef het token terug.
		return token;
	}

	/**
	 * Bouwt een identifier op.
	 */
	private String buildIdentifier(char c) {
		StringBuilder s = new StringBuilder();
		s.append(c);

		// Zolang we het einde van de expressie niet bereikt hebben en het volgende karakter alphanumeriek is of een
		// underscore plakken we deze karakters aan de string.
		while (!isEndOfInput() && isAlphaNumericOrUnderscore(peekChar())) {
			s.append(nextChar());
		}

		// Geef de identifier teug.
		return s.toString();
	}

	/**
	 * Bouwt een string op.
	 * @throws LexerException
	 */
	private String buildString() throws LexerException {
		StringBuilder s = new StringBuilder();

		// Zolang we het einde van de expressie niet bereikt hebben proberen we het volgende karakter op te halen.
		while (true) {
			if (isEndOfInput()) {
				throw new LexerException("Unexpected end of expression.", lineNumber, columnNumber - 1);
			}

			// Haal het volgende teken op.
			char n = nextChar();
			// Als het volgende teken een \ is moeten we iets escapen.
			if (n == '\\') {
				if (isEndOfInput()) {
					throw new LexerException("Unexpected end of expression.", lineNumber, 
							columnNumber);
				}
				s.append(nextChar());
			// Als het teken een ' is, is de string afgelopen en moeten we de lus afbreken.
			} else if (n == '\'') {
				break;
			// Anders is het een gewoon teken wat we achter aan de string plakken.
			} else {
				s.append(n);
			}
		}

		// Geef het eindresultaat terug.
		return s.toString();
	}

	/**
	 * Bouwt een getal op.
	 * @throws LexerException
	 */
	private String buildNumber(char c) throws LexerException {
		StringBuilder s = new StringBuilder();
		s.append(c);

		boolean hasDot = false;
		
		while (true) {
			if (isEndOfInput()) {
				break;
				// throw new LexerException("Unexpected end of expression.");
			}

			// Haal het volgende teken op.
			char n = peekChar();
			// Wanneer het volgende teken een . is en er nog geen punt gevonden is voegen we deze op het einde toe.
			if (n == '.') {
				if (hasDot == true) {
					throw new LexerException("Illegal number format; unexpected '.'.", lineNumber, columnNumber);
				}
				hasDot = true;
				s.append(n);

			// Wanneer het teken niet een getal is stoppen we de lus.
			} else if (!isNumeric(n)) {
				break;

			// Anders is het teken gewoon een cijfer en voegen we hem achter aan de string toe.
			} else {
				s.append(n);
			}

			// Verplaats de pointer nog 1 naar links.
			nextChar();
		}

		// Geef het getal terug.
		return s.toString();
	}

	/**
	 * Bouwt een boolean op.
	 * @throws LexerException
	 */
	private String buildBoolean(char c) throws LexerException {
		StringBuilder s = new StringBuilder();

		// Sla de huidige regel en kolomnummer op.
		int tempColumnNumber = columnNumber;
		int tempLineNumber = lineNumber;

		if (isEndOfInput()) {
			throw new LexerException("Unexpected end of expression.", tempLineNumber, tempColumnNumber);
		}

		// Haal het volgende teken op.
		char n = nextChar();
		// Het volgende teken moet gelijk zijn aan c (| als | en & als &)
		if (n != c) {
			throw new LexerException("Expected '" + c + "'.", tempLineNumber, tempColumnNumber);
		}

		// Zet de tekens op de StringBuilder.
		s.append(c).append(n);
		
		return s.toString();
	}
}

