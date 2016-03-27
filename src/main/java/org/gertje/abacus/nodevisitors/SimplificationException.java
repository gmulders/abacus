package org.gertje.abacus.nodevisitors;

import org.gertje.abacus.nodes.ExpressionNode;

public class SimplificationException extends VisitingException {

	public SimplificationException(String message, ExpressionNode node) {
		super(message, node);
	}
}
