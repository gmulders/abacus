package org.gertje.abacus.nodes;

import org.gertje.abacus.token.Token;
import org.gertje.abacus.nodevisitors.NodeVisitor;
import org.gertje.abacus.nodevisitors.VisitingException;
import org.gertje.abacus.types.Type;

/**
 * Klasse die een lijst van expressies voorstelt.
 */
public class StatementListNode extends NodeListNode<Node> {

	/**
	 * Constructor
	 */
	public StatementListNode(Token token) {
		super(token);
	}

	public Type getType() {
		// Get the last node in the list.
		Node lastNode = get(size() - 1);

		// If the last item in the list is an expression node we return its type, otherwise we return null.
		if (lastNode.getNodeType() == NodeType.EXPRESSION) {
			return ((ExpressionNode) lastNode).getType();
		}

		return null;
	}

	@Override
	public <R, X extends VisitingException> R accept(NodeVisitor<R, X> visitor) throws X {
		return visitor.visit(this);
	}

	// Voor de rest gebeurt alle magie in NodeListNode<T>.
}
