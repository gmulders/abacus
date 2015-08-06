package org.gertje.abacus.translator;

import org.gertje.abacus.symboltable.SymbolTable;

public abstract class ExpressionWrapper<T> {

	public SymbolTable symbolTable;

	public abstract T f() throws Exception;

	public void setSymbolTable(SymbolTable sym) {
		this.symbolTable = sym;
	}

	public java.math.BigDecimal function_rand() {
		return new java.math.BigDecimal("0.5");
	}
}
