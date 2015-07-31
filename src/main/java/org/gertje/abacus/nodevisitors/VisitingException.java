package org.gertje.abacus.nodevisitors;

import org.gertje.abacus.exception.AbacusException;
import org.gertje.abacus.nodes.Node;

public class VisitingException extends AbacusException {

	public VisitingException(String message, Node node, Exception cause) {
		super(message, node.getToken().getLineNumber(), node.getToken().getColumnNumber(), cause);
	}

	public VisitingException(String message, Node node) {
		super(message, node.getToken().getLineNumber(), node.getToken().getColumnNumber());
	}

}
