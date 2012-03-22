package org.gertje.abacus.nodes;

import java.math.BigDecimal;
import java.math.BigInteger;

import org.gertje.abacus.Token;

public class MultiplyNode extends AbstractTermNode {

	/**
	 * Constructor
	 */
	public MultiplyNode(AbstractNode lhs, AbstractNode rhs, Token token, NodeFactoryInterface nodeFactory) {
		super(lhs, rhs, token, 4, "*", nodeFactory);
	}

	@Override
	protected BigDecimal term(BigDecimal left, BigDecimal right) {
		return left.multiply(right);
	}

	@Override
	protected Number term(BigDecimal left, BigInteger right) {
		return left.multiply(new BigDecimal(right));
	}

	@Override
	protected Number term(BigInteger left, BigDecimal right) {
		return (new BigDecimal(left)).multiply(right);
	}

	@Override
	protected Number term(BigInteger left, BigInteger right) {
		return left.multiply(right);
	}

	@Override
	public Class<?> getType() {
		return lhs.getType().equals(BigDecimal.class) || rhs.getType().equals(BigDecimal.class) 
				? BigDecimal.class
				: BigInteger.class;
	}
}