package org.gertje.abacus.nodes;

import java.math.BigDecimal;

import org.gertje.abacus.AnalyserException;
import org.gertje.abacus.Token;
import org.gertje.abacus.symboltable.SymbolTableInterface;


public class AddNode extends AbstractNode {

	private AbstractNode lhs;
	private AbstractNode rhs;

	/**
	 * Constructor
	 */
	public AddNode(AbstractNode lhs, AbstractNode rhs, Token token, NodeFactoryInterface nodeFactory) {
		super (5, token, nodeFactory);

		this.lhs = lhs;
		this.rhs = rhs;
	}

	public Object evaluate(SymbolTableInterface sym) {
		Object left = lhs.evaluate(sym);
		Object right = rhs.evaluate(sym);

		// Wanneer het type een number is moeten we gewoon plus doen, anders gebruiken we een punt om de strings aan
		// elkaar te plakken.
		if (left instanceof BigDecimal) {
			return ((BigDecimal)left).add((BigDecimal)right);
		}
		return ((String)left)+((String)right);
	}

	public AbstractNode analyse(SymbolTableInterface sym) throws AnalyserException {
        // Vereenvoudig de nodes indien mogelijk.
		lhs = lhs.analyse(sym);
		rhs = rhs.analyse(sym);

		// Beide zijden moeten van het type 'number' of 'string'.
		if (!(  
				lhs.getType().equals(BigDecimal.class) && rhs.getType().equals(BigDecimal.class)
				|| lhs.getType().equals(String.class) && rhs.getType().equals(String.class))) {
			throw new AnalyserException("Expected two parameters of the same type to ADD-expression.", token);
		}

		// Wanneer beide zijden constant zijn kunnen we de node vereenvoudigen.
		if (lhs.getIsConstant() && rhs.getIsConstant()) {
			if (lhs.getType().equals(BigDecimal.class)) {
				return nodeFactory.createNumberNode((BigDecimal) evaluate(sym), token);
			} else {
				return nodeFactory.createStringNode((String) evaluate(sym), token);
			}
		}

		// Geef de huidige instantie terug.
		return this;
	}

	public String generateJavascript(SymbolTableInterface sym) {
		return generateJavascriptNodePart(sym, lhs) + " + " + generateJavascriptNodePart(sym, rhs);
	}

	public Class<?> getType() {
		return lhs.getType();
	}

	public boolean getIsConstant() {
		return false;
	}
}
