package org.gertje.abacus.nodes;

import org.gertje.abacus.nodevisitors.NodeVisitor;
import org.gertje.abacus.nodevisitors.VisitingException;
import org.gertje.abacus.token.Token;
import org.gertje.abacus.types.Type;

/**
 * Represents the dereferencing of an array i.e. the '[]'-operator.
 */
public class ArrayNode extends AbstractExpressionNode {

	private ExpressionNode array;
	private ExpressionNode index;

	private Type type;

	public ArrayNode(ExpressionNode array, ExpressionNode index, Token token) {
		super(0, token);

		this.array = array;
		this.index = index;
	}

	@Override
	public Type getType() {
		return type;
	}

	@Override
	public boolean getIsConstant() {
		return array.getIsConstant() && index.getIsConstant();
	}

	@Override
	public <R, X extends VisitingException> R accept(NodeVisitor<R, X> visitor) throws X {
		return visitor.visit(this);
	}

	public ExpressionNode getArray() {
		return array;
	}

	public void setArray(ExpressionNode array) {
		this.array = array;
	}

	public ExpressionNode getIndex() {
		return index;
	}

	public void setIndex(ExpressionNode index) {
		this.index = index;
	}

	public void setType(Type type) {
		this.type = type;
	}
}
