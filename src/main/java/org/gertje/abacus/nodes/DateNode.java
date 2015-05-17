package org.gertje.abacus.nodes;

import org.gertje.abacus.Token;
import org.gertje.abacus.nodevisitors.NodeVisitor;
import org.gertje.abacus.nodevisitors.VisitingException;

import java.sql.Date;

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
	public Class<?> getType() {
		return Date.class;
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
