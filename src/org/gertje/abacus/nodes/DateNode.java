package org.gertje.abacus.nodes;

import java.sql.Date;

import org.gertje.abacus.Token;
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

	public Date evaluate(SymbolTableInterface sym) {
		return value;
	}

	public DateNode analyse(SymbolTableInterface sym) {
		// Deze node kunnen we niet eenvoudiger maken. Geef de huidige instantie terug.	
		return this;
	}

	@Override
	public String generateJavascript(SymbolTableInterface sym) {
		// TODO Auto-generated method stub
		return null;
	}

	public Class<?> getType() {
		return Date.class;
	}

	public boolean getIsConstant() {
		return true;
	}
}
