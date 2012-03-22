package org.gertje.abacus.functions;

import java.math.BigDecimal;
import java.util.List;


class RandFunction extends AbstractFunction {

	/**
	 * Constructor
	 */
	public RandFunction() {
		super(null, false);
	}

	public Object evaluate(List<Object> params) {
		return BigDecimal.valueOf(Math.random());
	}

	public Class<?> getReturnType() {
		return BigDecimal.class;
	}

	public String getName() {
		return "rand";
	}
}
