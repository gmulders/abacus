package org.gertje.abacus.nodes;

import org.gertje.abacus.Token;
import org.gertje.abacus.nodevisitors.NodeVisitor;
import org.gertje.abacus.nodevisitors.VisitingException;

public class NullNode extends AbstractNode {

	public NullNode(Token token, NodeFactory nodeFactory) {
		super(1, token, nodeFactory);
	}

	@Override
	public boolean getIsConstant() {
		return true;
	}

	@Override
	public Class<?> getType() {
		return Object.class;
	}

	@Override
	public <R, X extends VisitingException> R accept(NodeVisitor<R, X> visitor) throws X {
		return visitor.visit(this);
	}
}
