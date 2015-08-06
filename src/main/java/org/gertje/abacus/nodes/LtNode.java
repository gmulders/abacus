package org.gertje.abacus.nodes;

import org.gertje.abacus.token.Token;
import org.gertje.abacus.nodevisitors.NodeVisitor;
import org.gertje.abacus.nodevisitors.VisitingException;

/**
 * Node that represents a less comparison.
 */
public class LtNode extends AbstractComparisonNode {

	/**
	 * Constructor
	 */
	public LtNode(Node lhs, Node rhs, Token token) {
		super(lhs, rhs, token, 6);
	}

	@Override
	public <R, X extends VisitingException> R accept(NodeVisitor<R, X> visitor) throws X {
		return visitor.visit(this);
	}
}
