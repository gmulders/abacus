package org.gertje.abacus.nodes;

import org.gertje.abacus.Token;
import org.gertje.abacus.symboltable.SymbolTableInterface;

public class StringNode extends AbstractNode {

	private String value;

	/**
	 * Constructor
	 */
	public StringNode(String value, Token token) {
		precedence = 1;

		this.value = value;
		this.token = token;
	}

	public String evaluate(SymbolTableInterface sym) {
		return value;
	}

	public AbstractNode analyse(SymbolTableInterface sym) {
		// Deze node kunnen we niet eenvoudiger maken. Geef de huidige instantie terug.	
		return this;
	}

	public String generateJavascript(SymbolTableInterface sym) {
		return "'" + value + "'";
	}

	public Class<?> getType() {
		return String.class;
	}

	public boolean getIsConstant() {
		return true;
	}
}
