package org.gertje.abacus.nodes;

import org.gertje.abacus.nodevisitors.NodeVisitor;
import org.gertje.abacus.nodevisitors.VisitingException;
import org.gertje.abacus.token.Token;
import org.gertje.abacus.types.Type;

/**
 * Node that represents an assignment.
 */
public class AssignmentNode extends AbstractNode {

	protected Node lhs;
	protected Node rhs;

	public AssignmentNode(Node lhs, Node rhs, Token token) {
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
