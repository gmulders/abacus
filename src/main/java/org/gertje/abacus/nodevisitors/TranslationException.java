package org.gertje.abacus.nodevisitors;

import org.gertje.abacus.nodes.AbstractNode;

public class TranslationException extends VisitingException {

	public TranslationException(String message, AbstractNode node) {
		super(message, node);
	}
}
