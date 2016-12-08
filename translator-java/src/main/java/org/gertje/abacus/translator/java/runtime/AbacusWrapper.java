package org.gertje.abacus.translator.java.runtime;

import org.gertje.abacus.context.AbacusContext;

public abstract class AbacusWrapper<T> {

	protected AbacusContext abacusContext;

	public abstract T f() throws Exception;

	public void setAbacusContext(AbacusContext abacusContext) {
		this.abacusContext = abacusContext;
	}
}
