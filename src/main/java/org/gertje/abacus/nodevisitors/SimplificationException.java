package org.gertje.abacus.nodevisitors;

import org.gertje.abacus.nodes.Node;

public class SimplificationException extends VisitingException {

	public SimplificationException(String message, Node node) {
		super(message, node);
	}
}
