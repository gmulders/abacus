package org.gertje.abacus.token;

/**
 * Klasse die een tokensoort voorstelt. 
 * 
 * De reden dat ik hier geen enum gebruik, is dat een enum niet te extenden is. Wanneer we een uitbreiding van Abacus
 * willen parsen (bijvoorbeeld Crayon), kunnen we een nieuwe AbacusLexer maken die een uitgebreide syntax parst. Om het juiste
 * tokensoort terug te kunnen geven moeten we deze wel overschrijven. (Zie ook het Crayon project.)
 * 
 * We maken de constructor protected, zodat er geen andere tokensoorten aangemaakt kunnen worden. We gebruiken de Java
 * == operator om de soorten te kunnen vergelijken.
 */
public class TokenType {
	
	public static final TokenType END_OF_INPUT = new TokenType("END_OF_INPUT");
	public static final TokenType END_OF_EXPRESSION = new TokenType("END_OF_EXPRESSION");
	public static final TokenType WHITE_SPACE = new TokenType("WHITE_SPACE");
	public static final TokenType NEW_LINE = new TokenType("NEW_LINE");
	public static final TokenType COMMA = new TokenType("COMMA");
	public static final TokenType IDENTIFIER = new TokenType("IDENTIFIER");
	public static final TokenType LEFT_PARENTHESIS = new TokenType("LEFT_PARENTHESIS");
	public static final TokenType RIGHT_PARENTHESIS = new TokenType("RIGHT_PARENTHESIS");
	public static final TokenType STRING = new TokenType("STRING");
	public static final TokenType FLOAT = new TokenType("FLOAT");
	public static final TokenType INTEGER = new TokenType("INTEGER");
	public static final TokenType BOOLEAN_AND = new TokenType("BOOLEAN_AND");
	public static final TokenType BOOLEAN_OR = new TokenType("BOOLEAN_OR");
	public static final TokenType PLUS = new TokenType("PLUS");
	public static final TokenType MINUS = new TokenType("MINUS");
	public static final TokenType MULTIPLY = new TokenType("MULTIPLY");
	public static final TokenType DIVIDE = new TokenType("DIVIDE");
	public static final TokenType NEQ = new TokenType("NEQ");
	public static final TokenType NOT = new TokenType("NOT");
	public static final TokenType LEQ = new TokenType("LEQ");
	public static final TokenType LT = new TokenType("LT");
	public static final TokenType GEQ = new TokenType("GEQ");
	public static final TokenType GT = new TokenType("GT");
	public static final TokenType EQ = new TokenType("EQ");
	public static final TokenType IF = new TokenType("IF");
	public static final TokenType COLON = new TokenType("COLON");
	public static final TokenType PERCENT = new TokenType("PERCENT");
	public static final TokenType POWER = new TokenType("POWER");
	public static final TokenType ASSIGNMENT = new TokenType("ASSIGNMENT");

	/**
	 * De naam van de tokensoort.
	 */
	protected String name;
	
	/**
	 * Constructor.
	 * @param name Naam van het token.
	 */
	protected TokenType(String name) {
		this.name = name;
	}

	/**
	 * Geeft de naam van de tokensoort terug.
	 * @return de naam van de tokensoort.
	 */
	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return name;
	}
}


/*
public enum TokenType {
	END_OF_INPUT,
	END_OF_EXPRESSION,
	WHITE_SPACE,
	NEW_LINE,
	COMMA,
	IDENTIFIER,
	LEFT_PARENTHESIS,
	RIGHT_PARENTHESIS,
	STRING,
	FLOAT,
	INTEGER,
	BOOLEAN_AND,
	BOOLEAN_OR,
	PLUS,
	MINUS,
	MULTIPLY,
	DIVIDE,
	NEQ,
	NOT,
	LEQ,
	LT,
	GEQ,
	GT,
	EQ,
	IF,
	COLON,
	PERCENT,
	POWER,
	ASSIGNMENT
}
*/