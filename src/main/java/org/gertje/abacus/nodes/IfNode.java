package org.gertje.abacus.nodes;

import org.gertje.abacus.nodevisitors.NodeVisitor;
import org.gertje.abacus.nodevisitors.VisitingException;
import org.gertje.abacus.token.Token;
import org.gertje.abacus.types.Type;

/**
 * Node that represents an if.
 */
public class IfNode extends AbstractNode {

	private Node condition;
	private Node ifBody;
	private Node elseBody;
	private Type type;

	/**
	 * Constructor
	 */
	public IfNode(Node condition, Node ifBody, Node elseBody, Token token) {
		super(10, token);

		this.condition = condition;
		this.ifBody = ifBody;
		this.elseBody = elseBody;
	}

	@Override
	public Type getType() {
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

	public Node getCondition() {
		return condition;
	}

	public void setCondition(Node condition) {
		this.condition = condition;
	}

	public Node getIfBody() {
		return ifBody;
	}

	public void setIfBody(Node ifBody) {
		this.ifBody = ifBody;
	}

	public Node getElseBody() {
		return elseBody;
	}

	public void setElseBody(Node elseBody) {
		this.elseBody = elseBody;
	}

	public void setType(Type type) {
		this.type = type;
	}
}
