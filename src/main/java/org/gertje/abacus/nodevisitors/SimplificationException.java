package org.gertje.abacus.nodevisitors;

import org.gertje.abacus.AbacusException;
import org.gertje.abacus.nodes.AbstractNode;

public class SimplificationException extends VisitingException {

	public SimplificationException(String message, AbstractNode node) {
		super(message, node);
	}
}
