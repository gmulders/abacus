package org.gertje.abacus.util;

import org.gertje.abacus.types.Type;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.Arrays;

/**
 * A class with utils for working with Java types.
 */
public class JavaTypeHelper {

	/**
	 * Determines the Java class that represents the given type.
	 * @param type The type
	 * @return the Java class that represents the given type.
	 */
	public static Class<?> determineJavaType(Type type) {
		// If the type is null we return the class of Object.
		if (type == null) {
			return Object.class;
		}

		// If the type is a primitive type we return the Java type of the base type.
		if (!type.isArray()) {
			return determineJavaType(type.getBaseType());
		}

		// The type is an array. We will construct a String that represents the class and get the class by calling 
		// {@link Class#forName(String)}. See {@link Class#getName()} for a detailed description of the format of class
		// names.
		// 
		// e.g. for an array of Longs with dimensionality 2 the name is '[[Ljava.lang.Long;':
		// - the number of repetitions of the '[' character represents the dimensionality
		// - the 'Ljava.lang.Long;' part is the type of the array
		//   - the 'L' tells the lexer that a class name follows
		//   - 'java.lang.Long' is the actual class name
		//   - the ';' character is used as terminator

		// Create an array with the length of the dimensionality of the type and fill the array with the '[' character.
		char[] chars = new char[type.getDimensionality()];
		Arrays.fill(chars, '[');

		// Construct the class name and return the class that is represented by that name.
		try {
			return Class.forName(new String(chars) + "L" + determineJavaType(type.getBaseType()).getName() + ";");
		} catch (ClassNotFoundException e) {
			// This should never happen.
			throw new IllegalStateException("Class for the constructed name could not be loaded.", e);
		}
	}

	/**
	 * Determines the Java type from the {@link Type.BaseType}.
	 * @param baseType The {@link Type.BaseType} to translate into a Java type.
	 * @return The Java type.
	 */
	public static Class<?> determineJavaType(Type.BaseType baseType) {

		switch (baseType) {
			case INTEGER:
				return Long.class;
			case BOOLEAN:
				return Boolean.class;
			case STRING:
				return String.class;
			case DECIMAL:
				return BigDecimal.class;
			default:
				return Date.class;
		}
	}
}
