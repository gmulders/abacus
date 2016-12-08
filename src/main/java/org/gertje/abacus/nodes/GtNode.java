package org.gertje.abacus.nodes;

import org.gertje.abacus.token.Token;
import org.gertje.abacus.nodevisitors.NodeVisitor;
import org.gertje.abacus.nodevisitors.VisitingException;

/**
 * Node that represents a greater comparison.
 */
public class GtNode extends AbstractComparisonNode {

	/**
	 * Constructor
	 */
	public GtNode(ExpressionNode lhs, ExpressionNode rhs, Token token) {
		super(lhs, rhs, token, 8);
	}

	@Override
	public <R, X extends VisitingException> R accept(NodeVisitor<R, X> visitor) throws X {
		return visitor.visit(this);
	}
}
