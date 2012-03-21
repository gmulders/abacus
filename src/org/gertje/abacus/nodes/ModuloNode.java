package org.gertje.abacus.nodes;

import java.math.BigDecimal;
import java.math.BigInteger;

import org.gertje.abacus.Token;

public class ModuloNode extends AbstractTermNode {

	/**
	 * Constructor
	 */
	public ModuloNode(AbstractNode lhs, AbstractNode rhs, Token token, NodeFactoryInterface nodeFactory) {
		super(lhs, rhs, token, 4, "%", nodeFactory);
	}

	@Override
	protected BigInteger term(BigDecimal left, BigDecimal right) {
		return left.toBigInteger().mod(right.toBigInteger());
	}

	@Override
	protected BigInteger term(BigDecimal left, BigInteger right) {
		return left.toBigInteger().mod(right);
	}

	@Override
	protected BigInteger term(BigInteger left, BigDecimal right) {
		return left.mod(right.toBigInteger());
	}

	@Override
	protected BigInteger term(BigInteger left, BigInteger right) {
		return left.mod(right);
	}

	@Override
	public Class<?> getType() {
		return BigInteger.class;
	}
}
