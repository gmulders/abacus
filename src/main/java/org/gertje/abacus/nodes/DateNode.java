package org.gertje.abacus.nodes;

import java.sql.Date;

import org.gertje.abacus.Token;
import org.gertje.abacus.nodevisitors.NodeVisitor;
import org.gertje.abacus.nodevisitors.VisitingException;
import org.gertje.abacus.symboltable.SymbolTableInterface;

public class DateNode extends AbstractNode {

	private Date value;

	/**
	 * Constructor
	 */
	public DateNode(Date value, Token token, NodeFactoryInterface nodeFactory) {
		super(1, token, nodeFactory);

		this.value = value;
	}

	@Override
	public Date evaluate(SymbolTableInterface sym) {
		return value;
	}

	@Override
	public DateNode analyse(SymbolTableInterface sym) {
		// Deze node kunnen we niet eenvoudiger maken. Geef de huidige instantie terug.	
		return this;
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
