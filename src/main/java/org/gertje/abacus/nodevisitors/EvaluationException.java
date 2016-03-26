package org.gertje.abacus.nodevisitors;

import org.gertje.abacus.nodes.Node;

public class EvaluationException extends VisitingException {

	public EvaluationException(String message, Node node, Exception cause) {
		super(message, node, cause);
	}

	public EvaluationException(String message, Node node) {
		super(message, node);
	}
}
