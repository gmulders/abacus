package org.gertje.abacus.nodes;

import org.gertje.abacus.token.Token;

/**
 * Abstract super class for all term nodes.
 */
public abstract class AbstractTermNode extends AbstractExpressionNode implements BinaryOperationNode {

	protected ExpressionNode lhs;
	protected ExpressionNode rhs;

	public AbstractTermNode(ExpressionNode lhs, ExpressionNode rhs, Token token, int precedence) {
		super(precedence, token);
		this.lhs = lhs;
		this.rhs = rhs;
	}

	@Override
	public boolean getIsConstant() {
		return false;
	}

	public ExpressionNode getLhs() {
		return lhs;
	}

	public void setLhs(ExpressionNode lhs) {
		this.lhs = lhs;
	}

	public ExpressionNode getRhs() {
		return rhs;
	}

	public void setRhs(ExpressionNode rhs) {
		this.rhs = rhs;
	}
}
