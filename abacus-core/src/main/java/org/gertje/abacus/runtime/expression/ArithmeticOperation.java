package org.gertje.abacus.runtime.expression;

import org.gertje.abacus.runtime.expression.math.BigDecimalMath;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;

/**
 * Defines a set of static methods and constants for arithmetic operations.
 */
public class ArithmeticOperation {

	/**
	 * Returns the negative of the given {@code number}.
	 * @param number The number.
	 * @return The negative of the given {@code number}.
	 */
	public static BigDecimal negate(BigDecimal number) {
		if (number == null) {
			return null;
		}
		return number.negate();
	}

	/**
	 * Returns the negative of the given {@code number}.
	 * @param number The number.
	 * @return The negative of the given {@code number}.
	 */
	public static Long negate(Long number) {
		if (number == null) {
			return null;
		}
		return -number;
	}

	/**
	 * Calculates the sum of {@code left} and {@code right}.
	 * @param left The left addend.
	 * @param right The right addend.
	 * @param mathContext The {@link MathContext} for the operation.
	 * @return The result of {@code left + right}.
	 */
	public static BigDecimal sum(BigDecimal left, BigDecimal right, MathContext mathContext) {
		if (left == null || right == null) {
			return null;
		}
		return left.add(right, mathContext);
	}

	/**
	 * Calculates the sum of {@code left} and {@code right}.
	 * @param left The left addend.
	 * @param right The right addend.
	 * @param mathContext The {@link MathContext} for the operation.
	 * @return The result of {@code left + right}.
	 */
	public static BigDecimal sum(BigDecimal left, Long right, MathContext mathContext) {
		return sum(left, right == null ? null : BigDecimal.valueOf(right), mathContext);
	}

	/**
	 * Calculates the sum of {@code left} and {@code right}.
	 * @param left The left addend.
	 * @param right The right addend.
	 * @param mathContext The {@link MathContext} for the operation.
	 * @return The result of {@code left + right}.
	 */
	public static BigDecimal sum(Long left, BigDecimal right, MathContext mathContext) {
		return sum(left == null ? null : BigDecimal.valueOf(left), right, mathContext);
	}

	/**
	 * Calculates the sum of {@code left} and {@code right}.
	 * @param left The left addend.
	 * @param right The right addend.
	 * @return The result of {@code left + right}.
	 */
	public static Long sum(Long left, Long right) {
		if (left == null || right == null) {
			return null;
		}
		return left + right;
	}

	/**
	 * Calculates the difference of {@code left} and {@code right}.
	 * @param left The minuend.
	 * @param right The subtrahend.
	 * @param mathContext The {@link MathContext} for the operation.
	 * @return The result of {@code left - right}.
	 */
	public static BigDecimal subtract(BigDecimal left, BigDecimal right, MathContext mathContext) {
		if (left == null || right == null) {
			return null;
		}
		return left.subtract(right, mathContext);
	}

	/**
	 * Calculates the difference of {@code left} and {@code right}.
	 * @param left The minuend.
	 * @param right The subtrahend.
	 * @param mathContext The {@link MathContext} for the operation.
	 * @return The result of {@code left - right}.
	 */
	public static BigDecimal subtract(BigDecimal left, Long right, MathContext mathContext) {
		return subtract(left, right == null ? null : BigDecimal.valueOf(right), mathContext);
	}

	/**
	 * Calculates the difference of {@code left} and {@code right}.
	 * @param left The minuend.
	 * @param right The subtrahend.
	 * @param mathContext The {@link MathContext} for the operation.
	 * @return The result of {@code left - right}.
	 */
	public static BigDecimal subtract(Long left, BigDecimal right, MathContext mathContext) {
		return subtract(left == null ? null : BigDecimal.valueOf(left), right, mathContext);
	}

	/**
	 * Calculates the difference of {@code left} and {@code right}.
	 * @param left The minuend.
	 * @param right The subtrahend.
	 * @return The result of {@code left - right}.
	 */
	public static Long subtract(Long left, Long right) {
		if (left == null || right == null) {
			return null;
		}
		return left - right;
	}

	/**
	 * Calculates the product of {@code left} and {@code right}.
	 * @param left The multiplier.
	 * @param right The multiplicand.
	 * @param mathContext The {@link MathContext} for the operation.
	 * @return The result of {@code left * right}.
	 */
	public static BigDecimal multiply(BigDecimal left, BigDecimal right, MathContext mathContext) {
		if (left == null || right == null) {
			return null;
		}
		return left.multiply(right, mathContext);
	}

	/**
	 * Calculates the product of {@code left} and {@code right}.
	 * @param left The multiplier.
	 * @param right The multiplicand.
	 * @param mathContext The {@link MathContext} for the operation.
	 * @return The result of {@code left * right}.
	 */
	public static BigDecimal multiply(BigDecimal left, Long right, MathContext mathContext) {
		return multiply(left, right == null ? null : BigDecimal.valueOf(right), mathContext);
	}

	/**
	 * Calculates the product of {@code left} and {@code right}.
	 * @param left The multiplier.
	 * @param right The multiplicand.
	 * @param mathContext The {@link MathContext} for the operation.
	 * @return The result of {@code left * right}.
	 */
	public static BigDecimal multiply(Long left, BigDecimal right, MathContext mathContext) {
		return multiply(left == null ? null : BigDecimal.valueOf(left), right, mathContext);
	}

	/**
	 * Calculates the product of {@code left} and {@code right}.
	 * @param left The multiplier.
	 * @param right The multiplicand.
	 * @return The result of {@code left * right}.
	 */
	public static Long multiply(Long left, Long right) {
		if (left == null || right == null) {
			return null;
		}
		return left * right;
	}

	/**
	 * Calculates the quotient of {@code left} and {@code right}.
	 * @param left The dividend.
	 * @param right The divisor.
	 * @param mathContext The {@link MathContext} for the operation.
	 * @return The result of {@code left / right}.
	 */
	public static BigDecimal divide(BigDecimal left, BigDecimal right, MathContext mathContext) {
		if (left == null || right == null) {
			return null;
		}
		return left.divide(right, mathContext);
	}

	/**
	 * Calculates the quotient of {@code left} and {@code right}.
	 * @param left The dividend.
	 * @param right The divisor.
	 * @param mathContext The {@link MathContext} for the operation.
	 * @return The result of {@code left / right}.
	 */
	public static BigDecimal divide(BigDecimal left, Long right, MathContext mathContext) {
		return divide(left, right == null ? null : BigDecimal.valueOf(right), mathContext);
	}

	/**
	 * Calculates the quotient of {@code left} and {@code right}.
	 * @param left The dividend.
	 * @param right The divisor.
	 * @param mathContext The {@link MathContext} for the operation.
	 * @return The result of {@code left / right}.
	 */
	public static BigDecimal divide(Long left, BigDecimal right, MathContext mathContext) {
		return divide(left == null ? null : BigDecimal.valueOf(left), right, mathContext);
	}

	/**
	 * Calculates the quotient of {@code left} and {@code right}.
	 * @param left The dividend.
	 * @param right The divisor.
	 * @return The result of {@code left / right}.
	 */
	public static Long divide(Long left, Long right) {
		if (left == null || right == null) {
			return null;
		}
		return left / right;
	}

	/**
	 * Calculates the modulus of {@code left} and {@code right}.
	 * @param left The dividend.
	 * @param right The divisor.
	 * @return The result of {@code left % right}.
	 */
	public static Long modulo(BigDecimal left, BigDecimal right) {
		if (left == null || right == null) {
			return null;
		}
		return left.toBigInteger().mod(right.toBigInteger()).longValue();
	}

	/**
	 * Calculates the modulus of {@code left} and {@code right}.
	 * @param left The dividend.
	 * @param right The divisor.
	 * @return The result of {@code left % right}.
	 */
	public static Long modulo(BigDecimal left, Long right) {
		if (left == null || right == null) {
			return null;
		}
		return left.toBigInteger().mod(BigInteger.valueOf(right)).longValue();
	}

	/**
	 * Calculates the modulus of {@code left} and {@code right}.
	 * @param left The dividend.
	 * @param right The divisor.
	 * @return The result of {@code left % right}.
	 */
	public static Long modulo(Long left, BigDecimal right) {
		if (left == null || right == null) {
			return null;
		}
		return BigInteger.valueOf(left).mod(right.toBigInteger()).longValue();
	}

	/**
	 * Calculates the modulus of {@code left} and {@code right}.
	 * @param left The dividend.
	 * @param right The divisor.
	 * @return The result of {@code left % right}.
	 */
	public static Long modulo(Long left, Long right) {
		if (left == null || right == null) {
			return null;
		}
		return left % right;
	}

	/**
	 * Calculates the {@code base} to the power {@code exponent}.
	 * @param base The base.
	 * @param exponent The exponent.
	 * @return The result of {@code base ^ exponent}.
	 */
	public static BigDecimal power(BigDecimal base, BigDecimal exponent, MathContext mathContext) {
		// Return null if either of the arguments is null.
		if (base == null || exponent == null) {
			return null;
		}

		base = BigDecimalMath.scalePrec(base, mathContext.getPrecision() + 10);
		exponent = BigDecimalMath.scalePrec(exponent, mathContext.getPrecision() + 10);
		return BigDecimalMath.pow(base, exponent).round(mathContext);
	}

	/**
	 * Calculates the {@code base} to the power {@code exponent}.
	 * @param base The base.
	 * @param exponent The exponent.
	 * @return The result of {@code base ^ exponent}.
	 */
	public static BigDecimal power(BigDecimal base, Long exponent, MathContext mathContext) {
		// Return null if either of the arguments is null.
		if (base == null || exponent == null) {
			return null;
		}

		base = BigDecimalMath.scalePrec(base, mathContext.getPrecision() + 10);
		return BigDecimalMath.powRound(base, BigInteger.valueOf(exponent)).round(mathContext);
	}

	/**
	 * Calculates the {@code base} to the power {@code exponent}.
	 * @param base The base.
	 * @param exponent The exponent.
	 * @return The result of {@code base ^ exponent}.
	 */
	public static BigDecimal power(Long base, BigDecimal exponent, MathContext mathContext) {
		// Return null if either of the arguments is null.
		if (base == null || exponent == null) {
			return null;
		}

		return power(BigDecimal.valueOf(base), exponent, mathContext);
	}

	/**
	 * Calculates the {@code base} to the power {@code exponent}.
	 * @param base The base.
	 * @param exponent The exponent.
	 * @return The result of {@code base ^ exponent}.
	 */
	public static Long power(Long base, Long exponent) {
		// Return null if either of the arguments is null.
		if (base == null || exponent == null) {
			return null;
		}

		return BigInteger.valueOf(base).pow(exponent.intValue()).longValue();
	}
}
