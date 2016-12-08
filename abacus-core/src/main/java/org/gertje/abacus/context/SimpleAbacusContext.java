package org.gertje.abacus.context;

import org.gertje.abacus.symboltable.SymbolTable;

import java.math.MathContext;

/**
 * Simple implementation of the AbacusContext.
 */
public class SimpleAbacusContext implements AbacusContext {

	/**
	 * The symbol table of the context.
	 */
	private SymbolTable symbolTable;

	public SimpleAbacusContext(SymbolTable symbolTable) {
		this.symbolTable = symbolTable;
	}

	@Override
	public SymbolTable getSymbolTable() {
		return symbolTable;
	}

	@Override
	public MathContext getMathContext() {
		return MathContext.DECIMAL128;
	}
}
