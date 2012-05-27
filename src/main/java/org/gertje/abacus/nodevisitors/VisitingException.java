package org.gertje.abacus.nodevisitors;

import org.gertje.abacus.AbacusException;
import org.gertje.abacus.nodes.AbstractNode;

public class VisitingException extends AbacusException {

	public VisitingException(String message, AbstractNode node) {
		super(message, node.getToken().getLineNumber(), node.getToken().getColumnNumber());
	}

}
