package org.gertje.abacus.nodes;

import org.gertje.abacus.Token;
import org.gertje.abacus.nodevisitors.NodeVisitor;
import org.gertje.abacus.nodevisitors.VisitingException;

public class NotNode extends AbstractNode {

	AbstractNode argument;

	/**
	 * Constructor
	 */
	public NotNode(AbstractNode argument, Token token, NodeFactory nodeFactory) {
		super(2, token, nodeFactory);

		this.argument = argument;
	}

	@Override
	public boolean getIsConstant() {
		return false;
	}

	@Override
	public Class<?> getType() {
		return Boolean.class;
	}

	@Override
	public <R, X extends VisitingException> R accept(NodeVisitor<R, X> visitor) throws X {
		return visitor.visit(this);
	}

	public AbstractNode getArgument() {
		return argument;
	}

	public void setArgument(AbstractNode argument) {
		this.argument = argument;
	}
}
