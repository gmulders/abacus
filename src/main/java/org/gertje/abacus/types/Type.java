package org.gertje.abacus.types;

/**
 * Enum of all types.
 */
public enum Type {
	STRING,
	INTEGER,
	DECIMAL,
	BOOLEAN,
	DATE;

	/**
	 * Bepaalt of het meegegeven type een nummer is.
	 * @param type Het type waarvan de methode bepaalt of het een nummer is.
	 * @return {@code true} wanneer het meegegeven type een nummer is, anders {@code false}.
	 */
	public static boolean isNumber(Type type) {
		return DECIMAL == type || INTEGER == type;
	}

	/**
	 * Bepaalt of het meegegeven type het type onbekend is.
	 * @param type Het type waarvan de methode bepaalt of het het type van null is.
	 * @return {@code true} wanneer het meegegeven type het type van null is, anders {@code false}.
	 */
	public static boolean isUnknown(Type type) {
		return type == null;
	}

	/**
	 * Bepaalt of het meegegeven type een nummer of onbekend is.
	 * @param type Het type waarvan de methode bepaalt of het een nummer is.
	 * @return {@code true} wanneer het meegegeven type een nummer is, anders {@code false}.
	 */
	public static boolean isNumberOrUnknown(Type type) {
		return isNumber(type) || isUnknown(type);
	}

	/**
	 * Bepaalt of het meegegeven type een string of onbekend is.
	 * @param type Het type waarvan de methode bepaalt of het een nummer is.
	 * @return {@code true} wanneer het meegegeven type een nummer is, anders {@code false}.
	 */
	public static boolean isStringOrUnknown(Type type) {
		return type == STRING || isUnknown(type);
	}

	/**
	 * Bepaalt of het meegegeven type een string of onbekend is.
	 * @param type Het type waarvan de methode bepaalt of het een nummer is.
	 * @return {@code true} wanneer het meegegeven type een nummer is, anders {@code false}.
	 */
	public static boolean isBooleanOrUnknown(Type type) {
		return type == BOOLEAN || isUnknown(type);
	}
}