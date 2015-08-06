package org.gertje.abacus.nodes;

import org.gertje.abacus.nodevisitors.NodeVisitor;
import org.gertje.abacus.nodevisitors.VisitingException;
import org.gertje.abacus.token.Token;
import org.gertje.abacus.types.Type;

/**
 * Node that represents a logical and.
 */
public class AndNode extends AbstractNode implements BinaryOperationNode {

	private Node lhs;
	private Node rhs;

	/**
	 * Constructor
	 */
	public AndNode(Node lhs, Node rhs, Token token) {
		super(8, token);

		this.lhs = lhs;
		this.rhs = rhs;
	}

	@Override
	public Type getType() {
		return Type.BOOLEAN;
	}

	@Override
	public boolean getIsConstant() {
		return false;
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
