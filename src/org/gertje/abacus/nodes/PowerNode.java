package org.gertje.abacus.nodes;

import java.math.BigDecimal;

import org.gertje.abacus.AnalyserException;
import org.gertje.abacus.Token;
import org.gertje.abacus.symboltable.SymbolTableInterface;

public class PowerNode extends AbstractNode {

	private AbstractNode base;
	private AbstractNode power;

	/**
	 * Constructor
	 */
	public PowerNode(AbstractNode base, AbstractNode power, Token token, NodeFactoryInterface nodeFactory) {
		super(4, token, nodeFactory);

		this.base = base;
		this.power = power;
	}

	public BigDecimal evaluate(SymbolTableInterface sym) {
		return BigDecimal.valueOf(Math.pow(
				((BigDecimal) base.evaluate(sym)).doubleValue(), 
				((BigDecimal) power.evaluate(sym)).doubleValue()));
	}

	public AbstractNode analyse(SymbolTableInterface sym) throws AnalyserException {
        // Vereenvoudig de nodes indien mogelijk.
		base = base.analyse(sym);
		power = power.analyse(sym);

		// Beide zijden moeten van het type 'number' zijn.
		if (base.getType().equals(BigDecimal.class) || power.getType().equals(BigDecimal.class)) {
			throw new AnalyserException("Expected two parameters of type 'number' to POWER-expression.", token);
		}

		// Wanneer beide zijden constant zijn kunnen we de node vereenvoudigen.
		if (base.getIsConstant() && power.getIsConstant()) {
			return nodeFactory.createNumberNode(evaluate(sym), token);
		}

		// Geef de huidige instantie terug.
		return this;
	}

	public String generateJavascript(SymbolTableInterface sym) {
		return "Math.pow("+ generateJavascriptNodePart(sym, base) + ", " + generateJavascriptNodePart(sym, power) + ")";
	}

	public Class<?> getType() {
		return BigDecimal.class;
	}

	public boolean getIsConstant() {
		return false;
	}
}
