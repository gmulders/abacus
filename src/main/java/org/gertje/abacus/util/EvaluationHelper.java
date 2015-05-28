package org.gertje.abacus.util;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class EvaluationHelper {

	public static Object add(Object left, Object right) {
		if (left == null || right == null) {
			return null;
		}

		// Map het type naar een compatible type.
		left = mapTypeToCompatibleType(left);
		right = mapTypeToCompatibleType(right);

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

	public static Number divide(Number left, Number right) {
		return term(left, right, new TermEvaluator() {
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

	public static Boolean eq(Object left, Object right) {
		return comparison(left, right, new ComparisonEvaluator() {
				@Override
				public <T extends Comparable<? super T>> boolean compare(T left, T right) {
					return left.compareTo(right) == 0;
				}
		});
	}

	public static Boolean geq(Object left, Object right) {
		return comparison(left, right, new ComparisonEvaluator() {
				@Override
				public <T extends Comparable<? super T>> boolean compare(T left, T right) {
					return left.compareTo(right) >= 0;
				}
		});
	}

	public static Boolean gt(Object left, Object right) {
		return comparison(left, right, new ComparisonEvaluator() {
				@Override
				public <T extends Comparable<? super T>> boolean compare(T left, T right) {
					return left.compareTo(right) > 0;
				}
		});
	}

	public static Boolean leq(Object left, Object right) {
		return comparison(left, right, new ComparisonEvaluator() {
				@Override
				public <T extends Comparable<? super T>> boolean compare(T left, T right) {
					return left.compareTo(right) <= 0;
				}
		});
	}

	public static Boolean lt(Object left, Object right) {
		return comparison(left, right, new ComparisonEvaluator() {
				@Override
				public <T extends Comparable<? super T>> boolean compare(T left, T right) {
					return left.compareTo(right) < 0;
				}
		});
	}

	public static Number modulo(Number left, Number right) {
		return term(left, right, new TermEvaluator() {
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

	public static Number multiply(Number left, Number right) {
		return term(left, right, new TermEvaluator() {
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

	public static Number negative(Number number) {
		// Wanneer het getal leeg is, is het resultaat van deze expressie ook leeg.
		if (number == null) {
			return null;
		}

		// Map het type naar een compatible type.
		number = (Number)mapTypeToCompatibleType(number);

		// Cast het argument naar het juiste type voordat we negate erop aan kunnen roepen.
		if (number instanceof BigDecimal) {
			return ((BigDecimal) number).negate();
		}

		return ((BigInteger) number).negate();
	}

	public static Boolean neq(Object left, Object right) {
		return comparison(left, right, new ComparisonEvaluator() {
				@Override
				public <T extends Comparable<? super T>> boolean compare(T left, T right) {
					return left.compareTo(right) != 0;
				}
		});
	}

	public static Boolean not(Boolean bool) {
		// Wanneer de boolean leeg is, is het resultaat van deze expressie ook leeg.
		if (bool == null) {
			return null;
		}

		return Boolean.valueOf(!bool.booleanValue());
	}

	public static BigDecimal power(Number baseValue, Number powerValue) {
		// Wanneer de basis of de macht leeg is, is het resultaat van deze expressie ook leeg.
		if (baseValue == null || powerValue == null) {
			return null;
		}

		// Map het type naar een compatible type.
		baseValue = (Number)mapTypeToCompatibleType(baseValue);
		powerValue = (Number)mapTypeToCompatibleType(powerValue);

		return BigDecimal.valueOf(Math.pow(baseValue.doubleValue(), powerValue.doubleValue()));
	}

	public static Number substract(Number left, Number right) {
		// Wanneer de linkerkant of de rechterkant leeg zijn, is het resultaat van deze expressie ook leeg.
		if (left == null || right == null) {
			return null;
		}

		// Map het type naar een compatible type.
		left = (Number)mapTypeToCompatibleType(left);
		right = (Number)mapTypeToCompatibleType(right);

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
	private static Number term(Object left, Object right, TermEvaluator termEvaluator) {
		if (left == null || right == null) {
			return null;
		}

		// Map het type naar een compatible type.
		left = mapTypeToCompatibleType(left);
		right = mapTypeToCompatibleType(right);

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
	private static Boolean comparison(Object left, Object right, ComparisonEvaluator comparisonEvaluator) {
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

		return Boolean.valueOf(comparisonEvaluator.compare((Comparable<Object>)left, (Comparable<Object>)right));
	}
}
