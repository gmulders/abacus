package org.gertje.abacus.nodes;

import org.gertje.abacus.Token;
import org.gertje.abacus.nodevisitors.NodeVisitor;
import org.gertje.abacus.nodevisitors.VisitingException;

public class LtNode extends AbstractComparisonNode {

	/**
	 * Constructor
	 */
	public LtNode(AbstractNode lhs, AbstractNode rhs, Token token, NodeFactory nodeFactory) {
		super(lhs, rhs, token, 6, nodeFactory);
	}

	@Override
	public <R, X extends VisitingException> R accept(NodeVisitor<R, X> visitor) throws X {
		return visitor.visit(this);
	}
}
