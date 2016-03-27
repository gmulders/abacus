package org.gertje.abacus.nodes;

import org.gertje.abacus.token.Token;
import org.gertje.abacus.types.Type;

/**
 * Abstract super class for all comparison nodes.
 */
public abstract class AbstractComparisonNode extends AbstractExpressionNode implements BinaryOperationNode {

	protected ExpressionNode lhs;
	protected ExpressionNode rhs;

	public AbstractComparisonNode(ExpressionNode lhs, ExpressionNode rhs, Token token, int precedence) {
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
	public Type getType() {
		return Type.BOOLEAN;
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
