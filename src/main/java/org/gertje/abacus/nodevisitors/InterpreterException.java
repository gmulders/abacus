package org.gertje.abacus.nodevisitors;

import org.gertje.abacus.nodes.Node;

public class InterpreterException extends VisitingException {

	public InterpreterException(String message, Node node) {
		super(message, node);
	}

	public InterpreterException(String message, Node node, Exception cause) {
		super(message, node, cause);
	}
}
