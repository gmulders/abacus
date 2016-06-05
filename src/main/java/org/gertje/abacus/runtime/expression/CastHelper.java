package org.gertje.abacus.runtime.expression;

import java.math.BigDecimal;

public class CastHelper {

	public static BigDecimal toDecimal(Long l) {
		return l == null ? null : new BigDecimal(l);
	}

	public static Long toInteger(BigDecimal b) {
		return b == null ? null : b.longValue();
	}
}
