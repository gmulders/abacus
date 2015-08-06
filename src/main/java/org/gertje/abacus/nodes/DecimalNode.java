package org.gertje.abacus.nodes;

import org.gertje.abacus.nodevisitors.NodeVisitor;
import org.gertje.abacus.nodevisitors.VisitingException;
import org.gertje.abacus.token.Token;
import org.gertje.abacus.types.Type;

import java.math.BigDecimal;

/**
 * Node that represents a decimal.
 */
public class DecimalNode extends AbstractNode {

	private BigDecimal value;

	/**
	 * Constructor
	 */
	public DecimalNode(BigDecimal value, Token token) {
		super(1, token);

		this.value = value;
	}

	@Override
	public Type getType() {
		return Type.DECIMAL;
	}

	@Override
	public boolean getIsConstant() {
		return true;
	}

	@Override
	public <R, X extends VisitingException> R accept(NodeVisitor<R, X> visitor) throws X {
		return visitor.visit(this);
	}

	public BigDecimal getValue() {
		return value;
	}

	public void setValue(BigDecimal value) {
		this.value = value;
	}
}
