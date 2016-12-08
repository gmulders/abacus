package org.gertje.abacus.nodes;

import org.gertje.abacus.nodevisitors.NodeVisitor;
import org.gertje.abacus.nodevisitors.VisitingException;
import org.gertje.abacus.token.Token;
import org.gertje.abacus.types.Type;

/**
 * Node that represents the power.
 */
public class PowerNode extends AbstractExpressionNode implements BinaryOperationNode {

	private ExpressionNode base;
	private ExpressionNode power;

	/**
	 * Constructor
	 */
	public PowerNode(ExpressionNode base, ExpressionNode power, Token token) {
		super(6, token);

		this.base = base;
		this.power = power;
	}

	@Override
	public Type getType() {
		if (Type.equals(base.getType(), Type.INTEGER) && Type.equals(power.getType(), Type.INTEGER)) {
			return Type.INTEGER;
		}
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

	public ExpressionNode getBase() {
		return base;
	}

	public void setBase(ExpressionNode base) {
		this.base = base;
	}

	public ExpressionNode getPower() {
		return power;
	}

	public void setPower(ExpressionNode power) {
		this.power = power;
	}

	@Override
	public ExpressionNode getLhs() {
		return base;
	}

	@Override
	public void setLhs(ExpressionNode node) {
		this.base = node;
	}

	@Override
	public ExpressionNode getRhs() {
		return power;
	}

	@Override
	public void setRhs(ExpressionNode node) {
		this.power = node;
	}
}
