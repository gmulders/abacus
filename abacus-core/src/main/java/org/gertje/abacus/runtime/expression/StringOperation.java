package org.gertje.abacus.runtime.expression;

/**
 * Defines a function to do string concatenation.
 */
public class StringOperation {

	/**
	 * Concatenates the two strings and returns the result.
	 * @param left The left side of the concatenation.
	 * @param right The right side of the concatenation.
	 * @return The result of the concatenation.
	 */
	public static String concat(String left, String right) {
		if (left == null || right == null) {
			return null;
		}

		return left + right;
	}
}
