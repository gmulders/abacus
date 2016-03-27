package org.gertje.abacus.context;

import org.gertje.abacus.symboltable.SymbolTable;

import java.math.MathContext;

/**
 * Context for an evaluator.
 */
public interface AbacusContext {

	/**
	 * Returns the {@link SymbolTable} for the current context.
	 * @return A {@link SymbolTable}.
	 */
	SymbolTable getSymbolTable();

	/**
	 * Returns the {@link MathContext} for the {@link java.math.BigDecimal} calculations in the current context.
	 * @return A {@link MathContext}.
	 */
	MathContext getMathContext();
}
