package org.gertje.abacus.nodes;

import org.gertje.abacus.Token;
import org.gertje.abacus.nodevisitors.NodeVisitorInterface;
import org.gertje.abacus.nodevisitors.VisitingException;

/**
 * Klasse die een lijst van expressies voorstelt.
 */
public class StatementListNode extends NodeListNode<AbstractNode> {

	public StatementListNode(Token token, NodeFactoryInterface nodeFactory) {
		super(token, nodeFactory);
	}

	@Override
	public void accept(NodeVisitorInterface visitor) throws VisitingException {
		visitor.visit(this);		
	}

	// Voor de rest gebeurt alle magie in NodeListNode<T>.
}
