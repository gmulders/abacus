package org.gertje.abacus.nodes;

import org.gertje.abacus.token.Token;
import org.gertje.abacus.nodevisitors.NodeVisitor;
import org.gertje.abacus.nodevisitors.VisitingException;
import org.gertje.abacus.types.Type;

/**
 * Node that represents a boolean.
 */
public class BooleanNode extends AbstractExpressionNode {

	private Boolean value;

	/**
	 * Constructor
	 */
	public BooleanNode(Boolean value, Token token) {
		super(1, token);

		this.value = value;
	}

	@Override
	public Type getType() {
		return Type.BOOLEAN;
	}

	@Override
	public boolean getIsConstant() {
		return true;
	}

	@Override
	public <R, X extends VisitingException> R accept(NodeVisitor<R, X> visitor) throws X {
		return visitor.visit(this);
	}

	public Boolean getValue() {
		return value;
	}

	public void setValue(Boolean value) {
		this.value = value;
	}
}
