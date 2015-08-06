package org.gertje.abacus.nodes;

import org.gertje.abacus.nodevisitors.NodeVisitor;
import org.gertje.abacus.nodevisitors.VisitingException;
import org.gertje.abacus.token.Token;
import org.gertje.abacus.types.Type;

/**
 * Node that represents null.
 */
public class NullNode extends AbstractNode {

	/**
	 * Constructor
	 */
	public NullNode(Token token) {
		super(1, token);
	}

	@Override
	public boolean getIsConstant() {
		return true;
	}

	@Override
	public Type getType() {
		return null;
	}

	@Override
	public <R, X extends VisitingException> R accept(NodeVisitor<R, X> visitor) throws X {
		return visitor.visit(this);
	}
}
