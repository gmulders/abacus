package org.gertje.abacus.nodes;

import org.gertje.abacus.token.Token;
import org.gertje.abacus.nodevisitors.NodeVisitor;
import org.gertje.abacus.nodevisitors.VisitingException;
import org.gertje.abacus.types.Type;

/**
 * Node that represents an integer.
 */
public class IntegerNode extends AbstractExpressionNode {

	private Long value;

	/**
	 * Constructor
	 */
	public IntegerNode(Long value, Token token) {
		super(1, token);

		this.value = value;
	}

	@Override
	public Type getType() {
		return Type.INTEGER;
	}

	@Override
	public boolean getIsConstant() {
		return true;
	}

	@Override
	public <R, X extends VisitingException> R accept(NodeVisitor<R, X> visitor) throws X {
		return visitor.visit(this);
	}

	public Long getValue() {
		return value;
	}

	public void setValue(Long value) {
		this.value = value;
	}
}
