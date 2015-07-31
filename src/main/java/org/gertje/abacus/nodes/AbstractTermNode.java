package org.gertje.abacus.nodes;

import org.gertje.abacus.token.Token;

/**
 * Abstract super class for all term nodes.
 */
public abstract class AbstractTermNode extends AbstractNode implements BinaryOperationNode {

	protected Node lhs;
	protected Node rhs;

	public AbstractTermNode(Node lhs, Node rhs, Token token, int precedence) {
		super(precedence, token);
		this.lhs = lhs;
		this.rhs = rhs;
	}

	@Override
	public boolean getIsConstant() {
		return false;
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
