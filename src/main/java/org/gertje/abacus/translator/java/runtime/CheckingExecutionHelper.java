package org.gertje.abacus.translator.java.runtime;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Date;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class CheckingExecutionHelper {

	private static Class<?>[] ALLOWED_TYPES_EQUALS = {Boolean.class, BigDecimal.class, String.class, Date.class};
	private static Class<?>[] ALLOWED_TYPES_GREATER_THEN_EQUALS = {BigDecimal.class, String.class, Date.class};
	private static Class<?>[] ALLOWED_TYPES_GREATER_THEN = {BigDecimal.class, String.class, Date.class};
	private static Class<?>[] ALLOWED_TYPES_LESS_THEN_EQUALS = {BigDecimal.class, String.class, Date.class};
	private static Class<?>[] ALLOWED_TYPES_LESS_THEN = {BigDecimal.class, String.class, Date.class};
	private static Class<?>[] ALLOWED_TYPES_NOT_EQUALS = {Boolean.class, BigDecimal.class, String.class, Date.class};

	public static Object add(Object left, Object right, int lineNumber, int columnNumber) throws JavaExecutionException {
		if (left == null || right == null) {
			return null;
		}

		// Map het type naar een compatible type.
		left = mapTypeToCompatibleType(left);
		right = mapTypeToCompatibleType(right);

		// Wanneer niet beide zijden van het type 'String' of 'Number' zijn moeten we een exceptie gooien.
		if (!(left instanceof String && right instanceof String)
				&& !(isNumber(left) && isNumber(right))) {
			throw new JavaExecutionException(
					"Expected two parameters of type 'Number' or type 'String' to ADD-expression.",
					lineNumber, columnNumber);
		}

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

		return ((String)left)+((String)right);
	}

	public static Number divide(Object left, Object right, int lineNumber, int columnNumber) throws JavaExecutionException {
		return term(left, right, lineNumber, columnNumber, "divide", new TermEvaluator() {
				@Override
				public BigDecimal term(BigDecimal left, BigDecimal right) {
					return left.divide(right);
				}

				@Override
				public BigDecimal term(BigDecimal left, BigInteger right) {
					return left.divide(new BigDecimal(right));
				}

				@Override
				public Number term(BigInteger left, BigDecimal right) {
					return (new BigDecimal(left)).divide(right);
				}

				@Override
				public Number term(BigInteger left, BigInteger right) {
					return left.divide(right);
				}
		});
	}

	public static Boolean eq(Object left, Object right, int lineNumber, int columnNumber) throws JavaExecutionException {

		return comparison(left, right, ALLOWED_TYPES_EQUALS, lineNumber, columnNumber, "equals", new ComparisonEvaluator() {
				@Override
				public <T extends Comparable<? super T>> boolean compare(T left, T right) {
					return left.compareTo(right) == 0;
				}
		});
	}

	public static Boolean geq(Object left, Object right, int lineNumber, int columnNumber) throws JavaExecutionException {
		return comparison(left, right, ALLOWED_TYPES_GREATER_THEN_EQUALS, lineNumber, columnNumber, "greater-then-equals", new ComparisonEvaluator() {
				@Override
				public <T extends Comparable<? super T>> boolean compare(T left, T right) {
					return left.compareTo(right) >= 0;
				}
		});
	}

	public static Boolean gt(Object left, Object right, int lineNumber, int columnNumber) throws JavaExecutionException {
		return comparison(left, right, ALLOWED_TYPES_GREATER_THEN, lineNumber, columnNumber, "greater-then", new ComparisonEvaluator() {
				@Override
				public <T extends Comparable<? super T>> boolean compare(T left, T right) {
					return left.compareTo(right) > 0;
				}
		});
	}

	public static Boolean leq(Object left, Object right, int lineNumber, int columnNumber) throws JavaExecutionException {
		return comparison(left, right, ALLOWED_TYPES_LESS_THEN_EQUALS, lineNumber, columnNumber, "less-then-equals", new ComparisonEvaluator() {
				@Override
				public <T extends Comparable<? super T>> boolean compare(T left, T right) {
					return left.compareTo(right) <= 0;
				}
		});
	}

	public static Boolean lt(Object left, Object right, int lineNumber, int columnNumber) throws JavaExecutionException {
		return comparison(left, right, ALLOWED_TYPES_LESS_THEN, lineNumber, columnNumber, "less-then", new ComparisonEvaluator() {
				@Override
				public <T extends Comparable<? super T>> boolean compare(T left, T right) {
					return left.compareTo(right) < 0;
				}
		});
	}

	public static Number modulo(Object left, Object right, int lineNumber, int columnNumber) throws JavaExecutionException {
		return term(left, right, lineNumber, columnNumber, "modulo", new TermEvaluator() {
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

	public static Number multiply(Object left, Object right, int lineNumber, int columnNumber) throws JavaExecutionException {
		return term(left, right, lineNumber, columnNumber, "multiply", new TermEvaluator() {
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

	public static Number negative(Object number, int lineNumber, int columnNumber) throws JavaExecutionException {
		// Wanneer het getal leeg is, is het resultaat van deze expressie ook leeg.
		if (number == null) {
			return null;
		}

		// Map het type naar een compatible type.
		number = mapTypeToCompatibleType(number);

		// Het argument moet een getal zijn.
		if (!isNumber(number)) {
			throw new JavaExecutionException("Expected a number expression in NegativeNode.", lineNumber, columnNumber);
		}

		// Cast het argument naar het juiste type voordat we negate erop aan kunnen roepen.
		if (number instanceof BigDecimal) {
			return ((BigDecimal) number).negate();
		}

		return ((BigInteger) number).negate();
	}

	public static Boolean neq(Object left, Object right, int lineNumber, int columnNumber) throws JavaExecutionException {
		return comparison(left, right, ALLOWED_TYPES_NOT_EQUALS, lineNumber, columnNumber, "not-equals", new ComparisonEvaluator() {
				@Override
				public <T extends Comparable<? super T>> boolean compare(T left, T right) {
					return left.compareTo(right) != 0;
				}
		});
	}

	public static Boolean not(Object bool, int lineNumber, int columnNumber) throws JavaExecutionException {
		// Wanneer de boolean leeg is, is het resultaat van deze expressie ook leeg.
		if (bool == null) {
			return null;
		}

		// Het argument moet een boolean zijn.
		if (!(bool instanceof Boolean)) {
			throw new JavaExecutionException("Expected a boolean expression in NotNode.", lineNumber, columnNumber);
		}

		return !((Boolean)bool);
	}

	public static BigDecimal power(Object baseValue, Object powerValue, int lineNumber, int columnNumber) throws JavaExecutionException {
		// Wanneer de basis of de macht leeg is, is het resultaat van deze expressie ook leeg.
		if (baseValue == null || powerValue == null) {
			return null;
		}

		// Map het type naar een compatible type.
		baseValue = mapTypeToCompatibleType(baseValue);
		powerValue = mapTypeToCompatibleType(powerValue);

		// Beide zijden moeten van het type 'number' zijn.
		if (!isNumber(baseValue) || !isNumber(powerValue)) {
			throw new JavaExecutionException("Expected two parameters of type 'number' to POWER-expression.", lineNumber, columnNumber);
		}

		return BigDecimal.valueOf(Math.pow(((Number)baseValue).doubleValue(), ((Number)powerValue).doubleValue()));
	}

	public static Number substract(Object left, Object right, int lineNumber, int columnNumber) throws JavaExecutionException {
		// Wanneer de linkerkant of de rechterkant leeg zijn, is het resultaat van deze expressie ook leeg.
		if (left == null || right == null) {
			return null;
		}

		// Map het type naar een compatible type.
		left = mapTypeToCompatibleType(left);
		right = mapTypeToCompatibleType(right);

		// Beide zijden moeten van het type 'number' zijn.
		if (!isNumber(left) || !isNumber(right)) {
			throw new JavaExecutionException("Expected two parameters of number type to SUBSTRACT-expression.", lineNumber, columnNumber);
		}

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
	private static Number term(Object left, Object right, int lineNumber, int columnNumber, String expressionName,
			TermEvaluator termEvaluator) throws JavaExecutionException {

		// Wanneer links of rechts null is, is het resultaat van de expressie null.
		if (left == null || right == null) {
			return null;
		}

		// Map het type naar een compatible type.
		left = mapTypeToCompatibleType(left);
		right = mapTypeToCompatibleType(right);

		// Beide zijden moeten van het type 'number' zijn.
		if (!isNumber(left) || !isNumber(right)) {
			throw new JavaExecutionException(
					"Expected two parameters of type 'number' to " + expressionName + "-expression.",
					lineNumber, columnNumber);
		}

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

	private static Object mapTypeToCompatibleType(Object object) {
		// Wanneer het object van een floatingpoint type is moeten we er een bigdecimal van maken.
		if (object instanceof Float || object instanceof Double) {
			object = BigDecimal.valueOf(((Number) object).doubleValue());

		// Wanneer het object van een integraal type is moeten we er een biginteger van maken.
		} else if (object instanceof AtomicInteger || object instanceof AtomicLong || object instanceof Byte ||
				object instanceof Integer || object instanceof Long || object instanceof Short) {
			object = BigInteger.valueOf(((Number) object).longValue());
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
	private static Boolean comparison(Object left, Object right, Class<?>[] allowedTypes, int lineNumber,
			int columnNumber, String expressionName, ComparisonEvaluator comparisonEvaluator) throws JavaExecutionException {
		if (left == null || right == null) {
			return null;
		}

		// Map het type naar een compatible type.
		left = mapTypeToCompatibleType(left);
		right = mapTypeToCompatibleType(right);

		// Wanneer de waarde een BigInteger is casten we het naar een BigDecimal.
		if (left instanceof BigInteger) {
			left = new BigDecimal((BigInteger)left);
		}

		// Wanneer de waarde een BigInteger is casten we het naar een BigDecimal.
		if (right instanceof BigInteger) {
			right = new BigDecimal((BigInteger)right);
		}

		if (!checkComparisonTypes(left.getClass(), right.getClass(), allowedTypes)) {
			throw new JavaExecutionException(
					"Expected two parameters of the same type to " + expressionName + "-expression.",
					lineNumber, columnNumber);
		}

		return Boolean.valueOf(comparisonEvaluator.compare((Comparable<Object>)left, (Comparable<Object>)right));
	}

	/**
	 * Determines wether the object is of a numeric type; BigDecimal or BigInteger.
	 * @param object The object whose type is to be determined.
	 * @return {@code true} if the object is an instance of BigDecimal or BigInteger, {@code false} otherwise.
	 */
	private static boolean isNumber(Object object) {
		return object instanceof BigDecimal || object instanceof BigInteger;
	}

	/**
	 * Controleert de typen van de lhs en de rhs, wanneer beiden niet van het zelfde type zijn of ze komen niet voor in
	 * de lijst met toegestane typen geeft de methode false terug.
	 * @return <code>true</code> wanneer de typen goed zijn, anders <code>false</code>.
	 */
	protected static boolean checkComparisonTypes(Class<?> lhsType, Class<?> rhsType, Class<?>[] allowedTypes) {
		// Check all allowed types. If left and right are both an allowed type the method returns true.
		for(Class<?> type : allowedTypes) {
			if (lhsType.equals(type) && rhsType.equals(type)) {
				return true;
			}
		}
		return false;
	}
}
