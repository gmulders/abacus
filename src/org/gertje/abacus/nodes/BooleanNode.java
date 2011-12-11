package org.gertje.abacus.nodes;

import org.gertje.abacus.Token;
import org.gertje.abacus.symboltable.SymbolTableInterface;

public class BooleanNode extends AbstractNode {

	private Boolean value;

	/**
	 * Constructor
	 */
	public BooleanNode(Boolean value, Token token) {
		precedence = 1;

		this.value = value;
		this.token = token;
	}

	public Boolean evaluate(SymbolTableInterface sym) {
		return value;
	}

	public BooleanNode analyse(SymbolTableInterface sym) {
		// Deze node kunnen we niet eenvoudiger maken. Geef de huidige instantie terug.	
		return this;
	}

	public String generateJavascript(SymbolTableInterface sym) {
		return value.booleanValue() ? "true" : "false";
	}

	public Class<?> getType() {
		return Boolean.class;
	}

	public boolean getIsConstant() {
		return true;
	}
}
