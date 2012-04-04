package org.gertje.abacus;

public class Token {

	private TokenTypeInterface type;
	private String value;
	private int lineNumber;
	private int columnNumber;

	public Token(int lineNumber, int columnNumber) {
		this.lineNumber = lineNumber;
		this.columnNumber = columnNumber;
	}

	public void setType(TokenTypeInterface type) {
		this.type = type;
	}

	public TokenTypeInterface getType() {
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
