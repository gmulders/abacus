package org.gertje.abacus.nodes;

import org.gertje.abacus.Token;
import org.gertje.abacus.nodevisitors.NodeVisitor;
import org.gertje.abacus.nodevisitors.VisitingException;

import java.math.BigInteger;

public class ModuloNode extends AbstractTermNode {

	/**
	 * Constructor
	 */
	public ModuloNode(AbstractNode lhs, AbstractNode rhs, Token token) {
		super(lhs, rhs, token, 4);
	}

	@Override
	public Class<?> getType() {
		return BigInteger.class;
	}

	@Override
	public <R, X extends VisitingException> R accept(NodeVisitor<R, X> visitor) throws X {
		return visitor.visit(this);
	}
}
