package org.gertje.abacus.nodes;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.ArrayList;

import org.gertje.abacus.Token;

public class NeqNode extends AbstractComparisonNode {

	/**
	 * Constructor
	 */
	public NeqNode(AbstractNode lhs, AbstractNode rhs, Token token, NodeFactoryInterface nodeFactory) {
		super(lhs, rhs, token, 7, "!=", nodeFactory);

		allowedTypes = new ArrayList<Class<?>>();
		allowedTypes.add(Boolean.class);
		allowedTypes.add(BigDecimal.class);
		allowedTypes.add(String.class);
		allowedTypes.add(Date.class);
	}

	@Override
	protected <T extends Comparable<? super T>> boolean compare(Comparable<T> left, T right) {
		return left.compareTo(right) != 0;
	}
}
