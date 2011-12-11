package org.gertje.abacus; 

public class AbacusException extends Exception {
	private int lineNumber;
	private int columnNumber;

	public AbacusException(String message, int lineNumber, int columnNumber) {
		super(message + " at line: " + lineNumber + " column: " + columnNumber);

		this.lineNumber = lineNumber;
		this.columnNumber = columnNumber;
	}

	public int getLineNumber() {
		return lineNumber;
	}

	public int getColumnNumber() {
		return columnNumber;
	}
}
