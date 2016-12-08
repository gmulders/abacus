package org.gertje.abacus.nodes;

import org.gertje.abacus.nodevisitors.NodeVisitor;
import org.gertje.abacus.nodevisitors.VisitingException;
import org.gertje.abacus.token.Token;
import org.gertje.abacus.types.Type;

/**
 * Node that represents a division.
 */
public class DivideNode extends AbstractTermNode {

	/**
	 * Constructor
	 */
	public DivideNode(ExpressionNode lhs, ExpressionNode rhs, Token token) {
		super(lhs, rhs, token, 6);
	}

	@Override
	public Type getType() {
		if (lhs.getType() == rhs.getType()) {
			return lhs.getType();
		}

		if (Type.isNumber(lhs.getType()) && Type.isNumber(rhs.getType())) {
			return Type.DECIMAL;
		}

		if (lhs.getType() == null) {
			return rhs.getType();
		}

		return lhs.getType();
	}

	@Override
	public <R, X extends VisitingException> R accept(NodeVisitor<R, X> visitor) throws X {
		return visitor.visit(this);
	}
}