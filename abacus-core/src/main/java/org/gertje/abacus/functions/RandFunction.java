package org.gertje.abacus.functions;

import org.gertje.abacus.types.Type;

import java.math.BigDecimal;
import java.util.List;


public class RandFunction extends AbstractFunction {

	/**
	 * Constructor
	 */
	public RandFunction() {
		super(null, false);
	}

	public Object evaluate(List<Object> params) {
		return BigDecimal.valueOf(Math.random());
	}

	public Type getReturnType() {
		return Type.DECIMAL;
	}

	public String getName() {
		return "rand";
	}
}
