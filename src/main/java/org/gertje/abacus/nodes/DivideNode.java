package org.gertje.abacus.nodes;

import java.math.BigDecimal;
import java.math.BigInteger;

import org.gertje.abacus.Token;
import org.gertje.abacus.nodevisitors.NodeVisitor;
import org.gertje.abacus.nodevisitors.VisitingException;

public class DivideNode extends AbstractTermNode {

	/**
	 * Constructor
	 */
	public DivideNode(AbstractNode lhs, AbstractNode rhs, Token token, NodeFactory nodeFactory) {
		super(lhs, rhs, token, 4, nodeFactory);
	}

	@Override
	public Class<?> getType() {
		return lhs.getType().equals(BigDecimal.class) || rhs.getType().equals(BigDecimal.class) 
				? BigDecimal.class
				: BigInteger.class;
	}

	@Override
	public <R, X extends VisitingException> R accept(NodeVisitor<R, X> visitor) throws X {
		return visitor.visit(this);
	}
}
