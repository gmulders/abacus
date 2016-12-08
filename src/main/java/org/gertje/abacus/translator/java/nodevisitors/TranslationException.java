package org.gertje.abacus.translator.java.nodevisitors;

import org.gertje.abacus.nodes.Node;
import org.gertje.abacus.nodevisitors.VisitingException;

public class TranslationException extends VisitingException {

	public TranslationException(String message, Node node, Exception cause) {
		super(message, node, cause);
	}

	public TranslationException(String message, Node node) {
		super(message, node);
	}
}
