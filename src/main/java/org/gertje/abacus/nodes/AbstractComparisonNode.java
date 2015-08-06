package org.gertje.abacus.nodes;

import org.gertje.abacus.token.Token;
import org.gertje.abacus.types.Type;

/**
 * Abstract super class for all comparison nodes.
 */
public abstract class AbstractComparisonNode extends AbstractNode implements BinaryOperationNode {

	protected Node lhs;
	protected Node rhs;

	public AbstractComparisonNode(Node lhs, Node rhs, Token token, int precedence) {
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

	public Node getLhs() {
		return lhs;
	}

	public void setLhs(Node lhs) {
		this.lhs = lhs;
	}

	public Node getRhs() {
		return rhs;
	}

	public void setRhs(Node rhs) {
		this.rhs = rhs;
	}
}
