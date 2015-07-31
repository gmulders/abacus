package org.gertje.abacus.nodes;

import org.gertje.abacus.nodevisitors.NodeVisitor;
import org.gertje.abacus.nodevisitors.VisitingException;
import org.gertje.abacus.token.Token;
import org.gertje.abacus.types.Type;

/**
 * Node that represents an unary negation.
 */
public class NegativeNode extends AbstractNode {

	private Node argument;

	/**
	 * Constructor
	 */
	public NegativeNode(Node argument, Token token) {
		super(2, token);

		this.argument = argument;
	}

	@Override
	public boolean getIsConstant() {
		return false;
	}

	@Override
	public Type getType() {
		return argument.getType();
	}

	@Override
	public <R, X extends VisitingException> R accept(NodeVisitor<R, X> visitor) throws X {
		return visitor.visit(this);
	}

	public Node getArgument() {
		return argument;
	}

	public void setArgument(Node argument) {
		this.argument = argument;
	}
}
