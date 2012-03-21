package org.gertje.abacus.nodes;

import java.math.BigDecimal;
import java.math.BigInteger;

import org.gertje.abacus.Token;

public class DivideNode extends AbstractTermNode {

	/**
	 * Constructor
	 */
	public DivideNode(AbstractNode lhs, AbstractNode rhs, Token token, NodeFactoryInterface nodeFactory) {
		super(lhs, rhs, token, 4, "/", nodeFactory);
	}

	@Override
	protected BigDecimal term(BigDecimal left, BigDecimal right) {
		return left.divide(right);
	}

	@Override
	protected BigDecimal term(BigDecimal left, BigInteger right) {
		return left.divide(new BigDecimal(right));
	}

	@Override
	protected Number term(BigInteger left, BigDecimal right) {
		return (new BigDecimal(left)).divide(right);
	}

	@Override
	protected Number term(BigInteger left, BigInteger right) {
		return left.divide(right);
	}

	@Override
	public Class<?> getType() {
		return lhs.getType().equals(BigDecimal.class) || rhs.getType().equals(BigDecimal.class) 
				? BigDecimal.class
				: BigInteger.class;
	}
}
