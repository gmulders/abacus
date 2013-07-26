package org.gertje.abacus.nodevisitors;

import org.gertje.abacus.nodes.AbstractNode;

abstract public class AbstractNodeVisitor<R, X extends VisitingException> implements NodeVisitor<R, X> {

	/**
	 * De 'default' visit methode, deze wordt aangeroepen wanneer een Node bezocht wordt waarvoor geen visit-methode
	 * gedefinieerd is.
	 */
	@Override
	public R visit(AbstractNode node) throws X {
		throw new RuntimeException("Unknown node found: " + node.getToken().getType().toString());
	}
}
