package org.gertje.abacus.nodes;

import org.gertje.abacus.nodevisitors.NodeVisitor;
import org.gertje.abacus.nodevisitors.VisitingException;
import org.gertje.abacus.token.Token;
import org.gertje.abacus.types.Type;

import java.sql.Date;

/**
 * Node that represents a date.
 */
public class DateNode extends AbstractNode {

	private Date value;

	/**
	 * Constructor
	 */
	public DateNode(Date value, Token token) {
		super(1, token);

		this.value = value;
	}

	@Override
	public Type getType() {
		return Type.DATE;
	}

	@Override
	public boolean getIsConstant() {
		return true;
	}

	@Override
	public <R, X extends VisitingException> R accept(NodeVisitor<R, X> visitor) throws X {
		return visitor.visit(this);
	}

	public Date getValue() {
		return value;
	}

	public void setValue(Date value) {
		this.value = value;
	}
}
