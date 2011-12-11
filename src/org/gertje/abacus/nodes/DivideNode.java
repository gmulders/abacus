package org.gertje.abacus.nodes;

import java.math.BigDecimal;

import org.gertje.abacus.Token;

public class DivideNode extends AbstractTermNode {

	/**
	 * Constructor
	 */
	public DivideNode(AbstractNode lhs, AbstractNode rhs, Token token) {
		super(lhs, rhs, token, 4, "/");
	}

	@Override
	protected BigDecimal term(BigDecimal left, BigDecimal right) {
		return left.divide(right);
	}
}
