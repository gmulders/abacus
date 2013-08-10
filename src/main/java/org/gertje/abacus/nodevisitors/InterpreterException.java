package org.gertje.abacus.nodevisitors;

import org.gertje.abacus.nodes.AbstractNode;

public class InterpreterException extends VisitingException {

	public InterpreterException(String message, AbstractNode node) {
		super(message, node);
	}
}
