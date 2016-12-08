package org.gertje.abacus.util;

import org.gertje.abacus.types.Type;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * Class with utils for casting.
 */
public class CastHelper {

	/**
	 * Cast the value from the {@code fromType} to the {@code toType}.
	 * @param value The value to cast.
	 * @param fromType The original type of the value.
	 * @param toType The type to cast the value to.
	 * @return The value cast to its new type.
	 */
	public static Object castValue(Object value, Type fromType, Type toType) {
		if (value == null) {
			return null;
		}

		if (Type.equals(fromType, toType)) {
			return value;
		}

		// Cast the variable to the right type if necessary.
		if (Type.equals(fromType, Type.INTEGER) && Type.equals(toType, Type.DECIMAL)) {
			return BigDecimal.valueOf((Long)value);
		} else if (Type.equals(fromType, Type.DECIMAL) && Type.equals(toType, Type.INTEGER)) {
			return ((BigDecimal)value).longValue();
		}

		throw new IllegalArgumentException(fromType + " cannot be cast to " + toType);
	}
}
