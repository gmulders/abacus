package org.gertje.abacus.nodes;

import org.gertje.abacus.nodevisitors.NodeVisitor;
import org.gertje.abacus.nodevisitors.VisitingException;
import org.gertje.abacus.token.Token;
import org.gertje.abacus.types.Type;

/**
 * Node that represents the power.
 */
public class PowerNode extends AbstractNode implements BinaryOperationNode {

	private Node base;
	private Node power;

	/**
	 * Constructor
	 */
	public PowerNode(Node base, Node power, Token token) {
		super(4, token);

		this.base = base;
		this.power = power;
	}

	@Override
	public Type getType() {
		return Type.DECIMAL;
	}

	@Override
	public boolean getIsConstant() {
		return false;
	}

	@Override
	public <R, X extends VisitingException> R accept(NodeVisitor<R, X> visitor) throws X {
		return visitor.visit(this);
	}

	public Node getBase() {
		return base;
	}

	public void setBase(Node base) {
		this.base = base;
	}

	public Node getPower() {
		return power;
	}

	public void setPower(Node power) {
		this.power = power;
	}

	@Override
	public Node getLhs() {
		return base;
	}

	@Override
	public void setLhs(Node node) {
		this.base = node;
	}

	@Override
	public Node getRhs() {
		return power;
	}

	@Override
	public void setRhs(Node node) {
		this.power = node;
	}
}
