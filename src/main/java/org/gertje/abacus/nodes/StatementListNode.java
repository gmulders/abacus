package org.gertje.abacus.nodes;

import org.gertje.abacus.token.Token;
import org.gertje.abacus.nodevisitors.NodeVisitor;
import org.gertje.abacus.nodevisitors.VisitingException;

/**
 * Klasse die een lijst van expressies voorstelt.
 */
public class StatementListNode extends NodeListNode<AbstractNode> {

	/**
	 * Constructor
	 */
	public StatementListNode(Token token) {
		super(token);
	}

	@Override
	public <R, X extends VisitingException> R accept(NodeVisitor<R, X> visitor) throws X {
		return visitor.visit(this);
	}

	// Voor de rest gebeurt alle magie in NodeListNode<T>.
}
