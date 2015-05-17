package org.gertje.abacus.nodes;

import org.gertje.abacus.Token;

public abstract class AbstractTermNode extends AbstractNode implements BinaryOperationNode {

	protected AbstractNode lhs;
	protected AbstractNode rhs;

	public AbstractTermNode(AbstractNode lhs, AbstractNode rhs, Token token, int precedence) {
		super(precedence, token);
		this.lhs = lhs;
		this.rhs = rhs;
	}

	@Override
	public boolean getIsConstant() {
		return false;
	}

	public AbstractNode getLhs() {
		return lhs;
	}

	public void setLhs(AbstractNode lhs) {
		this.lhs = lhs;
	}

	public AbstractNode getRhs() {
		return rhs;
	}

	public void setRhs(AbstractNode rhs) {
		this.rhs = rhs;
	}
}
