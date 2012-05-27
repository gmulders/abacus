package org.gertje.abacus.nodevisitors;

import org.gertje.abacus.nodes.AbstractNode;

abstract public class AbstractNodeVisitor implements NodeVisitorInterface {

	/**
	 * De 'default' visit methode, deze wordt aangeroepen wanneer een Node bezocht wordt waarvoor geen visit-methode
	 * gedefinieerd is.
	 */
	@Override
	public void visit(AbstractNode node) throws VisitingException {
		throw new VisitingException("Unknown node found: " + node.getToken().getType().toString(), node);
	}
}
