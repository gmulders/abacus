package org.gertje.abacus.nodes;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.ArrayList;

import org.gertje.abacus.Token;
import org.gertje.abacus.nodevisitors.NodeVisitor;
import org.gertje.abacus.nodevisitors.VisitingException;

public class EqNode extends AbstractComparisonNode {

	/**
	 * Constructor
	 */
	public EqNode(AbstractNode lhs, AbstractNode rhs, Token token, NodeFactoryInterface nodeFactory) {
		super(lhs, rhs, token, 7, nodeFactory);

		allowedTypes = new ArrayList<Class<?>>();
		allowedTypes.add(Boolean.class);
		allowedTypes.add(BigDecimal.class);
		allowedTypes.add(String.class);
		allowedTypes.add(Date.class);
	}

	@Override
	protected <T extends Comparable<? super T>> boolean compare(T left, T right) {
		return left.compareTo(right) == 0;
	}

	@Override
	public <R, X extends VisitingException> R accept(NodeVisitor<R, X> visitor) throws X {
		return visitor.visit(this);
	}
}
