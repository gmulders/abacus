package org.gertje.abacus.nodes;

import org.gertje.abacus.token.Token;
import org.gertje.abacus.nodevisitors.NodeVisitor;
import org.gertje.abacus.nodevisitors.VisitingException;

public class NeqNode extends AbstractComparisonNode {

	/**
	 * Constructor
	 */
	public NeqNode(AbstractNode lhs, AbstractNode rhs, Token token) {
		super(lhs, rhs, token, 7);
	}

	@Override
	public <R, X extends VisitingException> R accept(NodeVisitor<R, X> visitor) throws X {
		return visitor.visit(this);
	}
}
