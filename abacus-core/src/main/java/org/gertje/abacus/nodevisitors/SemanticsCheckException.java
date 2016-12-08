package org.gertje.abacus.nodevisitors;

import org.gertje.abacus.nodes.AbstractExpressionNode;

public class SemanticsCheckException extends VisitingException {

	public SemanticsCheckException(String message, AbstractExpressionNode node) {
		super(message, node);
	}
}
