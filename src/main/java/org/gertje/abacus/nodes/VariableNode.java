package org.gertje.abacus.nodes;

import org.gertje.abacus.Token;
import org.gertje.abacus.nodevisitors.NodeVisitor;
import org.gertje.abacus.nodevisitors.VisitingException;

public class VariableNode extends AbstractNode {

	private String identifier;
	private Class<?> type;

	/**
	 * Constructor
	 */
	public VariableNode(String identifier, Token token, NodeFactoryInterface nodeFactory) {
		super(1, token, nodeFactory);

		this.identifier = identifier;
	}

	@Override
	public Class<?> getType() {
		return type;
	}

	@Override
	public boolean getIsConstant() {
		return false;
	}

	@Override
	public <R, X extends VisitingException> R accept(NodeVisitor<R, X> visitor) throws X {
		return visitor.visit(this);
	}

	public void setType(Class<?> type) {
		this.type = type;
	}

	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}
}
