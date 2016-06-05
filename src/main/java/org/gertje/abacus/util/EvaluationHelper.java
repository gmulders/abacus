package org.gertje.abacus.util;

import org.gertje.abacus.runtime.expression.ArithmeticOperation;
import org.gertje.abacus.runtime.expression.BooleanOperation;
import org.gertje.abacus.types.Type;

import java.math.BigDecimal;
import java.math.MathContext;
import java.sql.Date;

import static org.gertje.abacus.runtime.expression.BooleanOperation.EQUALS;
import static org.gertje.abacus.runtime.expression.BooleanOperation.GREATER_THAN;
import static org.gertje.abacus.runtime.expression.BooleanOperation.GREATER_THAN_EQUALS;
import static org.gertje.abacus.runtime.expression.BooleanOperation.LESS_THAN;
import static org.gertje.abacus.runtime.expression.BooleanOperation.LESS_THAN_EQUALS;
import static org.gertje.abacus.runtime.expression.BooleanOperation.NOT_EQUALS;

/**
 * Class with utils for evaluations.
 */
public class EvaluationHelper {

	/**
	 * Adds two objects and returns the result.
	 * @param left The left side of the addition.
	 * @param leftType The type of the left side of the addition.
	 * @param right The right side of the addition.
	 * @param rightType The type of the right side of the addition.
	 * @return The result of the addition.
	 */
	public static Object sum(Object left, Type leftType, Object right, Type rightType, MathContext mathContext) {
		if (left == null || right == null) {
			return null;
		}

		if (leftType == Type.DECIMAL && rightType == Type.DECIMAL) {
			return ArithmeticOperation.sum((BigDecimal) left, (BigDecimal) right, mathContext);
		}
		if (leftType == Type.DECIMAL && rightType == Type.INTEGER) {
			return ArithmeticOperation.sum((BigDecimal) left, (Long) right, mathContext);
		}
		if (leftType == Type.INTEGER && rightType == Type.DECIMAL) {
			return ArithmeticOperation.sum((Long) left, (BigDecimal) right, mathContext);
		}
		return ArithmeticOperation.sum((Long) left, (Long) right);
	}

	/**
	 * Divides two {@code Number}s and returns the result.
	 * @param left The left side of the division.
	 * @param leftType The type of the left side of the division.
	 * @param right The right side of the division.
	 * @param rightType The type of the right side of the division.
	 * @param mathContext The {@code MathContext} used when one of the numbers is a BigDecimal.
	 * @return The result of the division.
	 */
	public static Number divide(Number left, Type leftType, Number right, Type rightType, MathContext mathContext) {
		if (left == null || right == null) {
			return null;
		}

		if (leftType == Type.DECIMAL && rightType == Type.DECIMAL) {
			return ArithmeticOperation.divide((BigDecimal) left, (BigDecimal) right, mathContext);
		}
		if (leftType == Type.DECIMAL && rightType == Type.INTEGER) {
			return ArithmeticOperation.divide((BigDecimal) left, (Long) right, mathContext);
		}
		if (leftType == Type.INTEGER && rightType == Type.DECIMAL) {
			return ArithmeticOperation.divide((Long) left, (BigDecimal) right, mathContext);
		}
		return ArithmeticOperation.divide((Long) left, (Long) right);
	}

	/**
	 * Compares two objects for equality and returns the result.
	 * @param left The left side of the comparison.
	 * @param leftType The type of the left side of the comparison.
	 * @param right The right side of the comparison.
	 * @param rightType The type of the right side of the comparison.
	 * @return The result of the comparison.
	 */
	public static Boolean eq(Object left, Type leftType, Object right, Type rightType) {
		if (leftType == Type.DECIMAL && rightType == Type.INTEGER) {
			return BooleanOperation.compare((BigDecimal) left, (Long) right, EQUALS);
		}
		if (leftType == Type.INTEGER && rightType == Type.DECIMAL) {
			return BooleanOperation.compare((Long) left, (BigDecimal) right, EQUALS);
		}
		if (leftType == Type.INTEGER || rightType == Type.INTEGER) {
			return BooleanOperation.compare((Long) left, (Long) right, EQUALS);
		}
		if (leftType == Type.DECIMAL || rightType == Type.DECIMAL) {
			return BooleanOperation.compare((BigDecimal) left, (BigDecimal) right, EQUALS);
		}
		if (leftType == Type.BOOLEAN || rightType == Type.BOOLEAN) {
			return BooleanOperation.compare((Boolean) left, (Boolean) right, EQUALS);
		}
		if (leftType == Type.DATE || rightType == Type.DATE) {
			return BooleanOperation.compare((Date) left, (Date) right, EQUALS);
		}
		return BooleanOperation.compare((String) left, (String) right, EQUALS);
	}

	/**
	 * Compares two objects for being greater or equal and returns the result.
	 * @param left The left side of the comparison.
	 * @param leftType The type of the left side of the comparison.
	 * @param right The right side of the comparison.
	 * @param rightType The type of the right side of the comparison.
	 * @return The result of the comparison.
	 */
	public static Boolean geq(Object left, Type leftType, Object right, Type rightType) {
		if (leftType == Type.DECIMAL && rightType == Type.INTEGER) {
			return BooleanOperation.compare((BigDecimal) left, (Long) right, GREATER_THAN_EQUALS);
		}
		if (leftType == Type.INTEGER && rightType == Type.DECIMAL) {
			return BooleanOperation.compare((Long) left, (BigDecimal) right, GREATER_THAN_EQUALS);
		}
		if (leftType == Type.INTEGER || rightType == Type.INTEGER) {
			return BooleanOperation.compare((Long) left, (Long) right, GREATER_THAN_EQUALS);
		}
		if (leftType == Type.DECIMAL || rightType == Type.DECIMAL) {
			return BooleanOperation.compare((BigDecimal) left, (BigDecimal) right, GREATER_THAN_EQUALS);
		}
		if (leftType == Type.BOOLEAN || rightType == Type.BOOLEAN) {
			return BooleanOperation.compare((Boolean) left, (Boolean) right, GREATER_THAN_EQUALS);
		}
		if (leftType == Type.DATE || rightType == Type.DATE) {
			return BooleanOperation.compare((Date) left, (Date) right, GREATER_THAN_EQUALS);
		}
		return BooleanOperation.compare((String) left, (String) right, GREATER_THAN_EQUALS);
	}

	/**
	 * Compares two objects for being greater and returns the result.
	 * @param left The left side of the comparison.
	 * @param leftType The type of the left side of the comparison.
	 * @param right The right side of the comparison.
	 * @param rightType The type of the right side of the comparison.
	 * @return The result of the comparison.
	 */
	public static Boolean gt(Object left, Type leftType, Object right, Type rightType) {
		if (leftType == Type.DECIMAL && rightType == Type.INTEGER) {
			return BooleanOperation.compare((BigDecimal) left, (Long) right, GREATER_THAN);
		}
		if (leftType == Type.INTEGER && rightType == Type.DECIMAL) {
			return BooleanOperation.compare((Long) left, (BigDecimal) right, GREATER_THAN);
		}
		if (leftType == Type.INTEGER || rightType == Type.INTEGER) {
			return BooleanOperation.compare((Long) left, (Long) right, GREATER_THAN);
		}
		if (leftType == Type.DECIMAL || rightType == Type.DECIMAL) {
			return BooleanOperation.compare((BigDecimal) left, (BigDecimal) right, GREATER_THAN);
		}
		if (leftType == Type.BOOLEAN || rightType == Type.BOOLEAN) {
			return BooleanOperation.compare((Boolean) left, (Boolean) right, GREATER_THAN);
		}
		if (leftType == Type.DATE || rightType == Type.DATE) {
			return BooleanOperation.compare((Date) left, (Date) right, GREATER_THAN);
		}
		return BooleanOperation.compare((String) left, (String) right, GREATER_THAN);
	}

	/**
	 * Compares two objects for being less or equal and returns the result.
	 * @param left The left side of the comparison.
	 * @param leftType The type of the left side of the comparison.
	 * @param right The right side of the comparison.
	 * @param rightType The type of the right side of the comparison.
	 * @return The result of the comparison.
	 */
	public static Boolean leq(Object left, Type leftType, Object right, Type rightType) {
		if (leftType == Type.DECIMAL && rightType == Type.INTEGER) {
			return BooleanOperation.compare((BigDecimal) left, (Long) right, LESS_THAN_EQUALS);
		}
		if (leftType == Type.INTEGER && rightType == Type.DECIMAL) {
			return BooleanOperation.compare((Long) left, (BigDecimal) right, LESS_THAN_EQUALS);
		}
		if (leftType == Type.INTEGER || rightType == Type.INTEGER) {
			return BooleanOperation.compare((Long) left, (Long) right, LESS_THAN_EQUALS);
		}
		if (leftType == Type.DECIMAL || rightType == Type.DECIMAL) {
			return BooleanOperation.compare((BigDecimal) left, (BigDecimal) right, LESS_THAN_EQUALS);
		}
		if (leftType == Type.BOOLEAN || rightType == Type.BOOLEAN) {
			return BooleanOperation.compare((Boolean) left, (Boolean) right, LESS_THAN_EQUALS);
		}
		if (leftType == Type.DATE || rightType == Type.DATE) {
			return BooleanOperation.compare((Date) left, (Date) right, LESS_THAN_EQUALS);
		}
		return BooleanOperation.compare((String) left, (String) right, LESS_THAN_EQUALS);
	}

	/**
	 * Compares two objects for being less and returns the result.
	 * @param left The left side of the comparison.
	 * @param leftType The type of the left side of the comparison.
	 * @param right The right side of the comparison.
	 * @param rightType The type of the right side of the comparison.
	 * @return The result of the comparison.
	 */
	public static Boolean lt(Object left, Type leftType, Object right, Type rightType) {
		if (leftType == Type.DECIMAL && rightType == Type.INTEGER) {
			return BooleanOperation.compare((BigDecimal) left, (Long) right, LESS_THAN);
		}
		if (leftType == Type.INTEGER && rightType == Type.DECIMAL) {
			return BooleanOperation.compare((Long) left, (BigDecimal) right, LESS_THAN);
		}
		if (leftType == Type.INTEGER || rightType == Type.INTEGER) {
			return BooleanOperation.compare((Long) left, (Long) right, LESS_THAN);
		}
		if (leftType == Type.DECIMAL || rightType == Type.DECIMAL) {
			return BooleanOperation.compare((BigDecimal) left, (BigDecimal) right, LESS_THAN);
		}
		if (leftType == Type.BOOLEAN || rightType == Type.BOOLEAN) {
			return BooleanOperation.compare((Boolean) left, (Boolean) right, LESS_THAN);
		}
		if (leftType == Type.DATE || rightType == Type.DATE) {
			return BooleanOperation.compare((Date) left, (Date) right, LESS_THAN);
		}
		return BooleanOperation.compare((String) left, (String) right, LESS_THAN);
	}

	/**
	 * Calculates the modulo of two {@code Number}s and returns the result.
	 * @param left The left side of the modulo.
	 * @param leftType The type of the left side of the modulo.
	 * @param right The right side of the modulo.
	 * @param rightType The type of the right side of the modulo.
	 * @return The result of the calculation.
	 */
	public static Long modulo(Number left, Type leftType, Number right, Type rightType) {
		if (left == null || right == null) {
			return null;
		}

		if (leftType == Type.DECIMAL && rightType == Type.DECIMAL) {
			return ArithmeticOperation.modulo((BigDecimal) left, (BigDecimal) right);
		}
		if (leftType == Type.DECIMAL && rightType == Type.INTEGER) {
			return ArithmeticOperation.modulo((BigDecimal) left, (Long) right);
		}
		if (leftType == Type.INTEGER && rightType == Type.DECIMAL) {
			return ArithmeticOperation.modulo((Long) left, (BigDecimal) right);
		}
		return ArithmeticOperation.modulo((Long) left, (Long) right);
	}

	/**
	 * Calculates the multiplication of two {@code Number}s and returns the result.
	 * @param left The left side of the multiplication.
	 * @param leftType The type of the left side of the multiplication.
	 * @param right The right side of the multiplication.
	 * @param rightType The type of the right side of the multiplication.
	 * @param mathContext The {@code MathContext} used when one of the numbers is a BigDecimal.
	 * @return The result of the multiplication.
	 */
	public static Number multiply(Number left, Type leftType, Number right, Type rightType, MathContext mathContext) {
		if (left == null || right == null) {
			return null;
		}

		if (leftType == Type.DECIMAL && rightType == Type.DECIMAL) {
			return ArithmeticOperation.multiply((BigDecimal) left, (BigDecimal) right, mathContext);
		}
		if (leftType == Type.DECIMAL && rightType == Type.INTEGER) {
			return ArithmeticOperation.multiply((BigDecimal) left, (Long) right, mathContext);
		}
		if (leftType == Type.INTEGER && rightType == Type.DECIMAL) {
			return ArithmeticOperation.multiply((Long) left, (BigDecimal) right, mathContext);
		}
		return ArithmeticOperation.multiply((Long) left, (Long) right);
	}

	/**
	 * Returns the negative of the given {@code Number}.
	 * @param number The number.
	 * @param type The type of the number.
	 * @return The negative of the given {@code Number}.
	 */
	public static Number negate(Number number, Type type) {
		// Cast het argument naar het juiste type voordat we negate erop aan kunnen roepen.
		if (type == Type.DECIMAL) {
			return ArithmeticOperation.negate((BigDecimal) number);
		}

		return ArithmeticOperation.negate((Long) number);
	}

	/**
	 * Compares two objects for being unequal and returns the result.
	 * @param left The left side of the comparison.
	 * @param leftType The type of the left side of the comparison.
	 * @param right The right side of the comparison.
	 * @param rightType The type of the right side of the comparison.
	 * @return The result of the comparison.
	 */
	public static Boolean neq(Object left, Type leftType, Object right, Type rightType) {
		if (leftType == Type.DECIMAL && rightType == Type.INTEGER) {
			return BooleanOperation.compare((BigDecimal) left, (Long) right, NOT_EQUALS);
		}
		if (leftType == Type.INTEGER && rightType == Type.DECIMAL) {
			return BooleanOperation.compare((Long) left, (BigDecimal) right, NOT_EQUALS);
		}
		if (leftType == Type.INTEGER || rightType == Type.INTEGER) {
			return BooleanOperation.compare((Long) left, (Long) right, NOT_EQUALS);
		}
		if (leftType == Type.DECIMAL || rightType == Type.DECIMAL) {
			return BooleanOperation.compare((BigDecimal) left, (BigDecimal) right, NOT_EQUALS);
		}
		if (leftType == Type.BOOLEAN || rightType == Type.BOOLEAN) {
			return BooleanOperation.compare((Boolean) left, (Boolean) right, NOT_EQUALS);
		}
		if (leftType == Type.DATE || rightType == Type.DATE) {
			return BooleanOperation.compare((Date) left, (Date) right, NOT_EQUALS);
		}
		return BooleanOperation.compare((String) left, (String) right, NOT_EQUALS);
	}

	/**
	 * Calculates the {@code baseValue} to the power {@code powerValue}.
	 * @param baseValue The base.
	 * @param baseType The type of the base.
	 * @param powerValue The power.
	 * @param powerType The type of the power.
	 * @return The result of the calculation.
	 */
	public static Number power(Number baseValue, Type baseType, Number powerValue, Type powerType, MathContext mathContext) {
		if (baseValue == null || powerValue == null) {
			return null;
		}

		if (baseType == Type.DECIMAL && powerType == Type.DECIMAL) {
			return ArithmeticOperation.power((BigDecimal) baseValue, (BigDecimal) powerValue, mathContext);
		}
		if (baseType == Type.DECIMAL && powerType == Type.INTEGER) {
			return ArithmeticOperation.power((BigDecimal) baseValue, (Long) powerValue, mathContext);
		}
		if (baseType == Type.INTEGER && powerType == Type.DECIMAL) {
			return ArithmeticOperation.power((Long) baseValue, (BigDecimal) powerValue, mathContext);
		}
		return ArithmeticOperation.power((Long) baseValue, (Long) powerValue);
	}

	/**
	 * Substracts two {@code Number}s and returns the result.
	 * @param left The left side of the substraction.
	 * @param leftType The type of the left side of the substraction.
	 * @param right The right side of the substraction.
	 * @param rightType The type of the right side of the substraction.
	 * @param mathContext The {@code MathContext} used when one of the numbers is a BigDecimal.
	 * @return The result of the substraction.
	 */
	public static Number subtract(Number left, Type leftType, Number right, Type rightType, MathContext mathContext) {
		if (left == null || right == null) {
			return null;
		}

		if (leftType == Type.DECIMAL && rightType == Type.DECIMAL) {
			return ArithmeticOperation.subtract((BigDecimal) left, (BigDecimal) right, mathContext);
		}
		if (leftType == Type.DECIMAL && rightType == Type.INTEGER) {
			return ArithmeticOperation.subtract((BigDecimal) left, (Long) right, mathContext);
		}
		if (leftType == Type.INTEGER && rightType == Type.DECIMAL) {
			return ArithmeticOperation.subtract((Long) left, (BigDecimal) right, mathContext);
		}
		return ArithmeticOperation.subtract((Long) left, (Long) right);
	}
}
