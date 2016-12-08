package org.gertje.abacus.nodevisitors;

import org.gertje.abacus.nodes.Node;
import org.gertje.abacus.nodes.RootNode;
import org.gertje.abacus.nodes.StatementListNode;

abstract public class AbstractExpressionNodeVisitor<R, X extends VisitingException> implements NodeVisitor<R, X> {

	@Override
	public R visit(RootNode node) throws X {
		throw createIllegalStateException(node);
	}

	@Override
	public R visit(StatementListNode node) throws X {
		throw createIllegalStateException(node);
	}

	/**
	 * Creates an illegal state exception with the name of the class of the node.
	 * @param node The node.
	 */
	private static IllegalStateException createIllegalStateException(Node node) {
		return new IllegalStateException("Cannot visit '" + node.getClass().getSimpleName() + "' from expression.");
	}
}
