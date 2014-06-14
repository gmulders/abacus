package org.gertje.abacus.util;

public class JavaEscaper {

	/**
	 * Escapes a string so it can be used as a Java string in Java-source code.
	 * @param input The string to be escaped.
	 * @return The escaped string.
	 */
	public static String escapeJava(String input) {
		StringBuilder result = new StringBuilder(input.length() + 50);

		for (int i = 0; i < input.length(); i++) {

			// Determine the escaped value of the currect character.
			char currentChar = input.charAt(i);
			String filtered = null;
			if (currentChar == '"') {
				filtered = "\\\"";
			} else if (currentChar == '\\') {
				filtered = "\\\\";
			} else if (currentChar == '\b') {
				filtered = "\\b";
			} else if (currentChar == '\n') {
				filtered = "\\n";
			} else if (currentChar == '\t') {
				filtered = "\\t";
			} else if (currentChar == '\f') {
				filtered = "\\f";
			} else if (currentChar == '\r') {
				filtered = "\\r";
			} else if (currentChar < 0x20 || currentChar > 0x7f) {
				filtered = Integer.toHexString(currentChar | 0x10000).substring(1);
			}

			if (filtered == null) {
				result.append(input.charAt(i));
			} else {
				result.append(filtered);
			}
		}

		return result.toString();
	}
}
