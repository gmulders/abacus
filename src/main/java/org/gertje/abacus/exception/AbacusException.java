package org.gertje.abacus.exception;

public class AbacusException extends Exception {
	private int lineNumber;
	private int columnNumber;

	public AbacusException(String message, int lineNumber, int columnNumber) {
		this(message, lineNumber, columnNumber, null);
	}

	public AbacusException(String message, int lineNumber, int columnNumber, Exception cause) {
		super(message + " at line: " + lineNumber + " column: " + columnNumber, cause);

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
