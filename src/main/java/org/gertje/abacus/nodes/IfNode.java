package org.gertje.abacus.nodes;

import org.gertje.abacus.Token;
import org.gertje.abacus.nodevisitors.NodeVisitor;
import org.gertje.abacus.nodevisitors.VisitingException;

public class IfNode extends AbstractNode {

	private AbstractNode condition;
	private AbstractNode ifBody;
	private AbstractNode elseBody;
	private Class<?> type;

	/**
	 * Constructor
	 */
	public IfNode(AbstractNode condition, AbstractNode ifBody, AbstractNode elseBody, Token token) {
		super(10, token);

		this.condition = condition;
		this.ifBody = ifBody;
		this.elseBody = elseBody;
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

	public AbstractNode getCondition() {
		return condition;
	}

	public void setCondition(AbstractNode condition) {
		this.condition = condition;
	}

	public AbstractNode getIfBody() {
		return ifBody;
	}

	public void setIfBody(AbstractNode ifBody) {
		this.ifBody = ifBody;
	}

	public AbstractNode getElseBody() {
		return elseBody;
	}

	public void setElseBody(AbstractNode elseBody) {
		this.elseBody = elseBody;
	}

	public void setType(Class<?> type) {
		this.type = type;
	}
}
