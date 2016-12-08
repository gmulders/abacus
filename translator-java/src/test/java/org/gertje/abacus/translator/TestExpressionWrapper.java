package org.gertje.abacus.translator;

import org.gertje.abacus.translator.java.runtime.AbacusWrapper;

public abstract class TestExpressionWrapper<T> extends AbacusWrapper<T> {

	public java.math.BigDecimal function_rand() {
		return new java.math.BigDecimal("0.5");
	}
}
