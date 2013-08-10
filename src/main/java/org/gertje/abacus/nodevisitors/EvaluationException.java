package org.gertje.abacus.nodevisitors;

import org.gertje.abacus.nodes.AbstractNode;

public class EvaluationException extends VisitingException {

	public EvaluationException(String message, AbstractNode node) {
		super(message, node);
	}
}
