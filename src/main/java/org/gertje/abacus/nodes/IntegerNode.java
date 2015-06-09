package org.gertje.abacus.nodes;

import org.gertje.abacus.token.Token;
import org.gertje.abacus.nodevisitors.NodeVisitor;
import org.gertje.abacus.nodevisitors.VisitingException;

import java.math.BigInteger;

public class IntegerNode extends AbstractNode {

	private BigInteger value;

	/**
	 * Constructor
	 */
	public IntegerNode(BigInteger value, Token token) {
		super(1, token);

		this.value = value;
	}

	@Override
	public Class<?> getType() {
		return BigInteger.class;
	}

	@Override
	public boolean getIsConstant() {
		return true;
	}

	@Override
	public <R, X extends VisitingException> R accept(NodeVisitor<R, X> visitor) throws X {
		return visitor.visit(this);
	}

	public BigInteger getValue() {
		return value;
	}

	public void setValue(BigInteger value) {
		this.value = value;
	}
}
