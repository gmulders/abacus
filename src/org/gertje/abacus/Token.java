package org.gertje.abacus;

public class Token {

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
		NUMBER,
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
		ELSE,
		PERCENT,
		POWER,
		ASSIGNMENT
	}

	private TokenType type;
	private String value;
	private int lineNumber;
	private int columnNumber;

	public Token(int lineNumber, int columnNumber) {
		this.lineNumber = lineNumber;
		this.columnNumber = columnNumber;
	}

	public void setType(TokenType type) {
		this.type = type;
	}

	public TokenType getType() {
		return type;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public int getLineNumber() {
		return lineNumber;
	}

	public int getColumnNumber() {
		return columnNumber;
	}
}
