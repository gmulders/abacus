package org.gertje.abacus.runtime.expression;

import java.math.BigDecimal;
import java.sql.Date;

/**
 * Defines a set of static methods and constants for boolean operations.
 */
public class BooleanOperation {

	/**
	 * Local interface that defines a method to compare two objects.
	 */
	public interface ComparisonEvaluator {
		<T extends Comparable<? super T>> Boolean compare(T left, T right);
	}

	/**
	 * Comparison evaluator that tests for equality (==).
	 */
	public static ComparisonEvaluator EQUALS = new ComparisonEvaluator() {
		@Override
		public <T extends Comparable<? super T>> Boolean compare(T left, T right) {
			if (left == null && right == null) {
				return true;
			}
			if (left == null || right == null) {
				return false;
			}
			return left.compareTo(right) == 0;
		}
	};

	/**
	 * Comparison evaluator that tests for in equality (!=).
	 */
	public static ComparisonEvaluator NOT_EQUALS = new ComparisonEvaluator() {
		@Override
		public <T extends Comparable<? super T>> Boolean compare(T left, T right) {
			if (left == null && right == null) {
				return false;
			}
			if (left == null || right == null) {
				return true;
			}
			return left.compareTo(right) != 0;
		}
	};

	/**
	 * Comparison evaluator that tests for less than or equals (<=).
	 */
	public static ComparisonEvaluator LESS_THAN_EQUALS = new ComparisonEvaluator() {
		@Override
		public <T extends Comparable<? super T>> Boolean compare(T left, T right) {
			if (left == null || right == null) {
				return null;
			}
			return left.compareTo(right) <= 0;
		}
	};

	/**
	 * Comparison evaluator that tests for less than (<).
	 */
	public static ComparisonEvaluator LESS_THAN = new ComparisonEvaluator() {
		@Override
		public <T extends Comparable<? super T>> Boolean compare(T left, T right) {
			if (left == null || right == null) {
				return null;
			}
			return left.compareTo(right) < 0;
		}
	};

	/**
	 * Comparison evaluator that tests for greater than or equals (>=).
	 */
	public static ComparisonEvaluator GREATER_THAN_EQUALS = new ComparisonEvaluator() {
		@Override
		public <T extends Comparable<? super T>> Boolean compare(T left, T right) {
			if (left == null || right == null) {
				return null;
			}
			return left.compareTo(right) >= 0;
		}
	};

	/**
	 * Comparison evaluator that tests for greater than (>).
	 */
	public static ComparisonEvaluator GREATER_THAN = new ComparisonEvaluator() {
		@Override
		public <T extends Comparable<? super T>> Boolean compare(T left, T right) {
			if (left == null || right == null) {
				return null;
			}
			return left.compareTo(right) > 0;
		}
	};

	/**
	 * Tests two objects of type {@code <T>} that implement the {@link Comparable} interface for equality.
	 * @param left The left argument.
	 * @param right The right argument.
	 * @param <T> The type of the {@link Comparable}s.
	 * @return Boolean indicating whether the two objects are equal.
	 */
	private static <T extends Comparable<? super T>> Boolean compareT(T left, T right, ComparisonEvaluator evaluator) {
		return evaluator.compare(left, right);
	}

	/**
	 * Tests two Strings for equality.
	 * @param left The left argument.
	 * @param right The right argument.
	 * @return Boolean indicating whether the two objects are equal.
	 */
	public static Boolean compare(String left, String right, ComparisonEvaluator evaluator) {
		return compareT(left, right, evaluator);
	}

	/**
	 * Tests two Boolean for equality.
	 * @param left The left argument.
	 * @param right The right argument.
	 * @return Boolean indicating whether the two objects are equal.
	 */
	public static Boolean compare(Boolean left, Boolean right, ComparisonEvaluator evaluator) {
		return compareT(left, right, evaluator);
	}

	/**
	 * Tests two Dates for equality.
	 * @param left The left argument.
	 * @param right The right argument.
	 * @return Boolean indicating whether the two objects are equal.
	 */
	public static Boolean compare(Date left, Date right, ComparisonEvaluator evaluator) {
		return compareT(left, right, evaluator);
	}

	/**
	 * Tests two Longs for equality.
	 * @param left The left argument.
	 * @param right The right argument.
	 * @return Boolean indicating whether the two objects are equal.
	 */
	public static Boolean compare(Long left, Long right, ComparisonEvaluator evaluator) {
		return compareT(left, right, evaluator);
	}

	/**
	 * Tests two BigDecimals for equality.
	 * @param left The left argument.
	 * @param right The right argument.
	 * @return Boolean indicating whether the two objects are equal.
	 */
	public static Boolean compare(BigDecimal left, BigDecimal right, ComparisonEvaluator evaluator) {
		return compareT(left, right, evaluator);
	}

	/**
	 * Tests a BigDecimal and a Long for equality.
	 * @param left The left argument.
	 * @param right The right argument.
	 * @return Boolean indicating whether the two objects are equal.
	 */
	public static Boolean compare(BigDecimal left, Long right, ComparisonEvaluator evaluator) {
		return compareT(left, right == null ? null : new BigDecimal(right), evaluator);
	}

	/**
	 * Tests a Long and a BigDecimal for equality.
	 * @param left The left argument.
	 * @param right The right argument.
	 * @return Boolean indicating whether the two objects are equal.
	 */
	public static Boolean compare(Long left, BigDecimal right, ComparisonEvaluator evaluator) {
		return compareT(left == null ? null : new BigDecimal(left), right, evaluator);
	}

	/**
	 * Returns the logical not of the given {@code Boolean}.
	 * @param bool The boolean.
	 * @return The logical not of the given {@code Boolean}.
	 */
	public static Boolean not(Boolean bool) {
		// Return null if the argument is null.
		if (bool == null) {
			return null;
		}

		return !bool;
	}
}
