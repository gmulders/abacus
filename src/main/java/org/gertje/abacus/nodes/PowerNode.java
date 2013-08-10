package org.gertje.abacus.nodes;

import java.math.BigDecimal;

import org.gertje.abacus.Token;
import org.gertje.abacus.nodevisitors.NodeVisitor;
import org.gertje.abacus.nodevisitors.VisitingException;

public class PowerNode extends AbstractNode {

	private AbstractNode base;
	private AbstractNode power;

	/**
	 * Constructor
	 */
	public PowerNode(AbstractNode base, AbstractNode power, Token token, NodeFactoryInterface nodeFactory) {
		super(4, token, nodeFactory);

		this.base = base;
		this.power = power;
	}

	@Override
	public Class<?> getType() {
		return BigDecimal.class;
	}

	@Override
	public boolean getIsConstant() {
		return false;
	}

	@Override
	public <R, X extends VisitingException> R accept(NodeVisitor<R, X> visitor) throws X {
		return visitor.visit(this);
	}

	public AbstractNode getBase() {
		return base;
	}

	public void setBase(AbstractNode base) {
		this.base = base;
	}

	public AbstractNode getPower() {
		return power;
	}

	public void setPower(AbstractNode power) {
		this.power = power;
	}
}
