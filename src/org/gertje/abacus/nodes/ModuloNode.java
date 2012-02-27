package org.gertje.abacus.nodes;

import java.math.BigDecimal;

import org.gertje.abacus.Token;

public class ModuloNode extends AbstractTermNode {

	/**
	 * Constructor
	 */
	public ModuloNode(AbstractNode lhs, AbstractNode rhs, Token token, NodeFactoryInterface nodeFactory) {
		super(lhs, rhs, token, 4, "%", nodeFactory);
	}

	@Override
	protected BigDecimal term(BigDecimal left, BigDecimal right) {
		// We maken een mooie one-liner: converteer de bigdecimals naar integers om de modulo te kunnen nemen en 
		// converteer daarna de modulo terug naar een bigdecimal.
		return BigDecimal.valueOf(left.toBigInteger().mod(right.toBigInteger()).longValue());
	}
}
