package org.gertje.abacus.util;

import org.gertje.abacus.types.Type;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;

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
	public static Object add(Object left, Type leftType, Object right, Type rightType) {
		if (left == null || right == null) {
			return null;
		}

		// Map het type naar een compatible type.
		left = mapTypeToCompatibleType(left, leftType);
		right = mapTypeToCompatibleType(right, rightType);

		// Wanneer het type een number is moeten we gewoon plus doen, anders gebruiken we een plus om de strings aan
		// elkaar te plakken.
		if (left instanceof BigDecimal && right instanceof BigDecimal) {
			return ((BigDecimal)left).add((BigDecimal)right);
		} else if (left instanceof BigDecimal && right instanceof BigInteger) {
			return ((BigDecimal)left).add(new BigDecimal((BigInteger)right));
		} else if (left instanceof BigInteger && right instanceof BigDecimal) {
			return (new BigDecimal((BigInteger)left)).add((BigDecimal)right);
		} else if (left instanceof BigInteger && right instanceof BigInteger) {
			return ((BigInteger)left).add((BigInteger)right);
		}

		return ((String)left) + right;
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
		return term(left, leftType, right, rightType, new TermEvaluator() {
			@Override
			public BigDecimal term(BigDecimal left, BigDecimal right) {
				return left.divide(right, mathContext);
			}

			@Override
			public BigDecimal term(BigDecimal left, BigInteger right) {
				return left.divide(new BigDecimal(right), mathContext);
			}

			@Override
			public Number term(BigInteger left, BigDecimal right) {
				return (new BigDecimal(left)).divide(right, mathContext);
			}

			@Override
			public Number term(BigInteger left, BigInteger right) {
				return left.divide(right);
			}
		});
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
		return comparison(left, leftType, right, rightType, new ComparisonEvaluator() {
				@Override
				public <T extends Comparable<? super T>> boolean compare(T left, T right) {
					return left.compareTo(right) == 0;
				}
		});
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
		return comparison(left, leftType, right, rightType, new ComparisonEvaluator() {
				@Override
				public <T extends Comparable<? super T>> boolean compare(T left, T right) {
					return left.compareTo(right) >= 0;
				}
		});
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
		return comparison(left, leftType, right, rightType, new ComparisonEvaluator() {
			@Override
			public <T extends Comparable<? super T>> boolean compare(T left, T right) {
				return left.compareTo(right) > 0;
			}
		});
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
		return comparison(left, leftType, right, rightType, new ComparisonEvaluator() {
			@Override
			public <T extends Comparable<? super T>> boolean compare(T left, T right) {
				return left.compareTo(right) <= 0;
			}
		});
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
		return comparison(left, leftType, right, rightType, new ComparisonEvaluator() {
			@Override
			public <T extends Comparable<? super T>> boolean compare(T left, T right) {
				return left.compareTo(right) < 0;
			}
		});
	}

	/**
	 * Calculates the modulo of two {@code Number}s and returns the result.
	 * @param left The left side of the modulo.
	 * @param leftType The type of the left side of the modulo.
	 * @param right The right side of the modulo.
	 * @param rightType The type of the right side of the modulo.
	 * @return The result of the calculation.
	 */
	public static BigInteger modulo(Number left, Type leftType, Number right, Type rightType) {
		return (BigInteger) term(left, leftType, right, rightType, new TermEvaluator() {
			@Override
			public BigInteger term(BigDecimal left, BigDecimal right) {
				return left.toBigInteger().mod(right.toBigInteger());
			}

			@Override
			public BigInteger term(BigDecimal left, BigInteger right) {
				return left.toBigInteger().mod(right);
			}

			@Override
			public BigInteger term(BigInteger left, BigDecimal right) {
				return left.mod(right.toBigInteger());
			}

			@Override
			public BigInteger term(BigInteger left, BigInteger right) {
				return left.mod(right);
			}
		});
	}

	/**
	 * Calculates the multiplication of two {@code Number}s and returns the result.
	 * @param left The left side of the multiplication.
	 * @param leftType The type of the left side of the multiplication.
	 * @param right The right side of the multiplication.
	 * @param rightType The type of the right side of the multiplication.
	 * @return The result of the multiplication.
	 */
	public static Number multiply(Number left, Type leftType, Number right, Type rightType) {
		return term(left, leftType, right, rightType, new TermEvaluator() {
			@Override
			public BigDecimal term(BigDecimal left, BigDecimal right) {
				return left.multiply(right);
			}

			@Override
			public BigDecimal term(BigDecimal left, BigInteger right) {
				return left.multiply(new BigDecimal(right));
			}

			@Override
			public Number term(BigInteger left, BigDecimal right) {
				return (new BigDecimal(left)).multiply(right);
			}

			@Override
			public Number term(BigInteger left, BigInteger right) {
				return left.multiply(right);
			}
		});
	}

	/**
	 * Returns the negative of the given {@code Number}.
	 * @param number The number.
	 * @param type The type of the number.
	 * @return The negative of the given {@code Number}.
	 */
	public static Number negative(Number number, Type type) {
		// Wanneer het getal leeg is, is het resultaat van deze expressie ook leeg.
		if (number == null) {
			return null;
		}

		// Map het type naar een compatible type.
		number = (Number)mapTypeToCompatibleType(number, type);

		// Cast het argument naar het juiste type voordat we negate erop aan kunnen roepen.
		if (number instanceof BigDecimal) {
			return ((BigDecimal) number).negate();
		}

		return ((BigInteger) number).negate();
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
		return comparison(left, leftType, right, rightType, new ComparisonEvaluator() {
			@Override
			public <T extends Comparable<? super T>> boolean compare(T left, T right) {
				return left.compareTo(right) != 0;
			}
		});
	}

	/**
	 * Returns the logical not of the given {@code Boolean}.
	 * @param bool The boolean.
	 * @return The logical not of the given {@code Boolean}.
	 */
	public static Boolean not(Boolean bool) {
		// Wanneer de boolean leeg is, is het resultaat van deze expressie ook leeg.
		if (bool == null) {
			return null;
		}

		return Boolean.valueOf(!bool.booleanValue());
	}

	/**
	 * Calculates the {@code baseValue} to the power {@code powerValue}.
	 * @param baseValue The base.
	 * @param baseType The type of the base.
	 * @param powerValue The power.
	 * @param powerType The type of the power.
	 * @return The result of the calculation.
	 */
	public static BigDecimal power(Number baseValue, Type baseType, Number powerValue, Type powerType) {
		// Wanneer de basis of de macht leeg is, is het resultaat van deze expressie ook leeg.
		if (baseValue == null || powerValue == null) {
			return null;
		}

		// Map het type naar een compatible type.
		baseValue = (Number)mapTypeToCompatibleType(baseValue, baseType);
		powerValue = (Number)mapTypeToCompatibleType(powerValue, powerType);

		return BigDecimal.valueOf(Math.pow(baseValue.doubleValue(), powerValue.doubleValue()));
	}

	/**
	 * Substracts two {@code Number}s and returns the result.
	 * @param left The left side of the substraction.
	 * @param leftType The type of the left side of the substraction.
	 * @param right The right side of the substraction.
	 * @param rightType The type of the right side of the substraction.
	 * @return The result of the substraction.
	 */
	public static Number substract(Number left, Type leftType, Number right, Type rightType) {
		// Wanneer de linkerkant of de rechterkant leeg zijn, is het resultaat van deze expressie ook leeg.
		if (left == null || right == null) {
			return null;
		}

		// Map het type naar een compatible type.
		left = (Number)mapTypeToCompatibleType(left, leftType);
		right = (Number)mapTypeToCompatibleType(right, rightType);

		// Wanneer een van beide zijden een BigDecimal is, is het resultaat een BigDecimal, anders een BigInteger.
		if (left instanceof BigDecimal && right instanceof BigDecimal) {
			return ((BigDecimal)left).subtract((BigDecimal)right);
		} else if (left instanceof BigDecimal && right instanceof BigInteger) {
			return ((BigDecimal)left).subtract(new BigDecimal((BigInteger)right));
		} else if (left instanceof BigInteger && right instanceof BigDecimal) {
			return (new BigDecimal((BigInteger)left)).subtract((BigDecimal)right);
		} else {
			return ((BigInteger)left).subtract((BigInteger)right);
		}
	}

	/**
	 * Lokale interface waarin gedefinieerd wordt hoe de bewerking voor twee objecten gedaan moet worden.
	 */
	private interface TermEvaluator {
		Number term(BigDecimal left, BigDecimal right);
		Number term(BigDecimal left, BigInteger right);
		Number term(BigInteger left, BigDecimal right);
		Number term(BigInteger left, BigInteger right);
	}

	/**
	 * Bepaalt de uitkomst voor de de term-nodes.
	 */
	private static Number term(Object left, Type leftType, Object right, Type rightType, TermEvaluator termEvaluator) {
		if (left == null || right == null) {
			return null;
		}

		// Map het type naar een compatible type.
		left = mapTypeToCompatibleType(left, leftType);
		right = mapTypeToCompatibleType(right, rightType);

		// Bepaal aan de hand van het type van links en rechts welke term we aan moeten roepen.
		if (left instanceof BigDecimal && right instanceof BigDecimal) {
			return termEvaluator.term((BigDecimal)left, (BigDecimal)right);
		} else if (left instanceof BigDecimal && right instanceof BigInteger) {
			return termEvaluator.term((BigDecimal)left, (BigInteger)right);
		} else if (left instanceof BigInteger && right instanceof BigDecimal) {
			return termEvaluator.term((BigInteger)left, (BigDecimal)right);
		} else {
			return termEvaluator.term((BigInteger)left, (BigInteger)right);
		}
	}

	/**
	 * Converts the given object to an instance of a compatible class.
	 *
	 * It converts numeric types to BigInteger of BigDecimal.
	 *
	 * @param object The object to cast.
	 * @param type The type to cast the object to.
	 * @return The instance of a compatible class.
	 */
	private static Object mapTypeToCompatibleType(Object object, Type type) {
		if (type == Type.INTEGER) {
			if (object.getClass() == BigInteger.class) {
				return object;
			}

			if (!(object instanceof Number)) {
				throw new IllegalArgumentException("Illegal type found: " + object.getClass().getName() + " expected: "
						+ Number.class.getName());
			}

			return new BigInteger(object.toString());
		}

		if (type == Type.DECIMAL) {
			if (object.getClass() == BigDecimal.class) {
				return object;
			}

			if (!(object instanceof Number)) {
				throw new IllegalArgumentException("Illegal type found: " + object.getClass().getName() + " expected: "
						+ Number.class.getName());
			}

			return new BigDecimal(object.toString());
		}

		return object;
	}

	/**
	 * Lokale interface waarin gedefinieerd wordt hoe de vergelijking voor twee objecten gedaan moet worden.
	 */
	private interface ComparisonEvaluator {
		<T extends Comparable<? super T>> boolean compare(T left, T right);
	}

	/**
	 * Bepaalt de uitkomst voor de comparison-nodes.
	 */
	private static Boolean comparison(Object left, Type leftType, Object right, Type rightType,
			ComparisonEvaluator comparisonEvaluator) {
		if (left == null || right == null) {
			return null;
		}

		// Map het type naar een compatible type.
		left = mapTypeToCompatibleType(left, leftType);
		right = mapTypeToCompatibleType(right, rightType);

		// Wanneer de waarde een BigInteger is casten we het naar een BigDecimal.
		if (left instanceof BigInteger) {
			left = new BigDecimal((BigInteger)left);
		}

		// Wanneer de waarde een BigInteger is casten we het naar een BigDecimal.
		if (right instanceof BigInteger) {
			right = new BigDecimal((BigInteger)right);
		}

		return Boolean.valueOf(comparisonEvaluator.compare((Comparable<Object>)left, (Comparable<Object>)right));
	}
}
