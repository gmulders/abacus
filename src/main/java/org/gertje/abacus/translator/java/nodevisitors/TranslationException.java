package org.gertje.abacus.translator.java.nodevisitors;

import org.gertje.abacus.nodes.AbstractExpressionNode;
import org.gertje.abacus.nodevisitors.VisitingException;

public class TranslationException extends VisitingException {

	public TranslationException(String message, AbstractExpressionNode node) {
		super(message, node);
	}
}
