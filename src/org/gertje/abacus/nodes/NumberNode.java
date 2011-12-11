package org.gertje.abacus.nodes;

import java.math.BigDecimal;

import org.gertje.abacus.Token;
import org.gertje.abacus.symboltable.SymbolTableInterface;

public class NumberNode extends AbstractNode {

	private BigDecimal value;

	/**
	 * Constructor
	 */
	public NumberNode(BigDecimal value, Token token) {
		precedence = 1;

		this.value = value;
		this.token = token;
	}

	public BigDecimal evaluate(SymbolTableInterface sym) {
		return value;
	}

	public AbstractNode analyse(SymbolTableInterface sym) {
		// Deze node kunnen we niet eenvoudiger maken. Geef de huidige instantie terug.	
		return this;
	}

    public String generateJavascript(SymbolTableInterface sym) {
		return value.toString();
	}
	
	public Class<?> getType() {
		return BigDecimal.class;
	}

	public boolean getIsConstant() {
		return true;
	}
}
