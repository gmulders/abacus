package org.gertje.abacus.nodevisitors;

import org.gertje.abacus.nodes.AbstractNode;

public class SemanticsCheckException extends VisitingException {

	public SemanticsCheckException(String message, AbstractNode node) {
		super(message, node);
	}
}
