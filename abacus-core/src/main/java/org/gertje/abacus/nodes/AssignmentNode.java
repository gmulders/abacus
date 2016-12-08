package org.gertje.abacus.nodes;

import org.gertje.abacus.nodevisitors.NodeVisitor;
import org.gertje.abacus.nodevisitors.VisitingException;
import org.gertje.abacus.token.Token;
import org.gertje.abacus.types.Type;

/**
 * Node that represents an assignment.
 */
public class AssignmentNode extends AbstractExpressionNode {

	protected ExpressionNode lhs;
	protected ExpressionNode rhs;

	public AssignmentNode(ExpressionNode lhs, ExpressionNode rhs, Token token) {
		super(1, token);

		this.lhs = lhs;
		this.rhs = rhs;
	}

	@Override
	public boolean getIsConstant() {
		return false;
	}

	@Override
	public Type getType() {
		return lhs.getType();
	}

	@Override
	public <R, X extends VisitingException> R accept(NodeVisitor<R, X> visitor) throws X {
		return visitor.visit(this);		
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
