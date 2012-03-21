package org.gertje.abacus.nodes;

import java.math.BigDecimal;

import org.gertje.abacus.Token;
import org.gertje.abacus.symboltable.SymbolTableInterface;

public class FloatNode extends AbstractNode {

	private BigDecimal value;

	/**
	 * Constructor
	 */
	public FloatNode(BigDecimal value, Token token, NodeFactoryInterface nodeFactory) {
		super(1, token, nodeFactory);

		this.value = value;
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
