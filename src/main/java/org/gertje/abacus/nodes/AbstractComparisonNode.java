package org.gertje.abacus.nodes;

import org.gertje.abacus.token.Token;

public abstract class AbstractComparisonNode extends AbstractNode implements BinaryOperationNode {

	protected AbstractNode lhs;
	protected AbstractNode rhs;

	public AbstractComparisonNode(AbstractNode lhs, AbstractNode rhs, Token token, int precedence) {
		super(precedence, token);
		this.lhs = lhs;
		this.rhs = rhs;
	}

	@Override
	public boolean getIsConstant() {
		// Geen enkele AbstractCompareNode is constant. 
		return false;
	}

	@Override
	public Class<?> getType() {
		return Boolean.class;
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
