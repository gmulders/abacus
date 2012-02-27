package org.gertje.abacus.nodes;

import org.gertje.abacus.Token;

/**
 * Klasse die een lijst van expressies voorstelt.
 */
public class StatementListNode extends NodeListNode<AbstractNode> {

	public StatementListNode(Token token, NodeFactoryInterface nodeFactory) {
		super(token, nodeFactory);
	}

	// Deze klasse doet niets wat niet ook al in NodeListNode<T> gebeurt.
}
