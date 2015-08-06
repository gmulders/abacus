package org.gertje.abacus.translator.java.nodevisitors;

import org.gertje.abacus.nodes.AbstractNode;
import org.gertje.abacus.nodevisitors.VisitingException;

public class TranslationException extends VisitingException {

	public TranslationException(String message, AbstractNode node) {
		super(message, node);
	}
}
