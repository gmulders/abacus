package org.gertje.abacus.nodes;

import org.gertje.abacus.nodevisitors.NodeVisitor;
import org.gertje.abacus.nodevisitors.VisitingException;
import org.gertje.abacus.token.Token;
import org.gertje.abacus.types.Type;

/**
 * Node that represents an unary negation.
 */
public class NegativeNode extends AbstractExpressionNode {

	private ExpressionNode argument;

	/**
	 * Constructor
	 */
	public NegativeNode(ExpressionNode argument, Token token) {
		super(4, token);

		this.argument = argument;
	}

	@Override
	public boolean getIsConstant() {
		return false;
	}

	@Override
	public Type getType() {
		return argument.getType();
	}

	@Override
	public <R, X extends VisitingException> R accept(NodeVisitor<R, X> visitor) throws X {
		return visitor.visit(this);
	}

	public ExpressionNode getArgument() {
		return argument;
	}

	public void setArgument(ExpressionNode argument) {
		this.argument = argument;
	}
}
