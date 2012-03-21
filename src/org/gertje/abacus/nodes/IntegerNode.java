package org.gertje.abacus.nodes;

import java.math.BigInteger;

import org.gertje.abacus.Token;
import org.gertje.abacus.symboltable.SymbolTableInterface;

public class IntegerNode extends AbstractNode {

	private BigInteger value;

	/**
	 * Constructor
	 */
	public IntegerNode(BigInteger value, Token token, NodeFactoryInterface nodeFactory) {
		super(1, token, nodeFactory);

		this.value = value;
	}

	public BigInteger evaluate(SymbolTableInterface sym) {
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
		return BigInteger.class;
	}

	public boolean getIsConstant() {
		return true;
	}
}
