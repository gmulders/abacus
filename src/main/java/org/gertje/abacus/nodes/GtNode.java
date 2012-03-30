package org.gertje.abacus.nodes;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.ArrayList;

import org.gertje.abacus.Token;

public class GtNode extends AbstractComparisonNode {

	/**
	 * Constructor
	 */
	public GtNode(AbstractNode lhs, AbstractNode rhs, Token token, NodeFactoryInterface nodeFactory) {
		super(lhs, rhs, token, 6, ">", nodeFactory);
		
		allowedTypes = new ArrayList<Class<?>>();
		allowedTypes.add(BigDecimal.class);
		allowedTypes.add(String.class);
		allowedTypes.add(Date.class);
	}

	@Override
	protected <T extends Comparable<? super T>> boolean compare(T left, T right) {
		return left.compareTo(right) > 0;
	}
}
