package org.gertje.abacus.nodes;

import org.gertje.abacus.nodevisitors.NodeVisitor;
import org.gertje.abacus.nodevisitors.VisitingException;
import org.gertje.abacus.token.Token;
import org.gertje.abacus.types.Type;

/**
 * Node that represents an if.
 */
public class IfNode extends AbstractExpressionNode {

	private ExpressionNode condition;
	private ExpressionNode ifBody;
	private ExpressionNode elseBody;
	private Type type;

	/**
	 * Constructor
	 */
	public IfNode(ExpressionNode condition, ExpressionNode ifBody, ExpressionNode elseBody, Token token) {
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

	public ExpressionNode getCondition() {
		return condition;
	}

	public void setCondition(ExpressionNode condition) {
		this.condition = condition;
	}

	public ExpressionNode getIfBody() {
		return ifBody;
	}

	public void setIfBody(ExpressionNode ifBody) {
		this.ifBody = ifBody;
	}

	public ExpressionNode getElseBody() {
		return elseBody;
	}

	public void setElseBody(ExpressionNode elseBody) {
		this.elseBody = elseBody;
	}

	public void setType(Type type) {
		this.type = type;
	}
}
