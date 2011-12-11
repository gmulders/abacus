package org.gertje.abacus.nodes;

import java.math.BigDecimal;

import org.gertje.abacus.AnalyserException;
import org.gertje.abacus.Token;
import org.gertje.abacus.symboltable.SymbolTableInterface;

public class SubstractNode extends AbstractNode {

	private AbstractNode lhs;
	private AbstractNode rhs;

	/**
	 * Constructor
	 */
	public SubstractNode(AbstractNode lhs, AbstractNode rhs, Token token) {
		precedence = 5;

		this.lhs = lhs;
		this.rhs = rhs;
		this.token = token;
	}

	public BigDecimal evaluate(SymbolTableInterface sym) {
		// Trek het tweede getal van het eerste getal af.
		return ((BigDecimal) lhs.evaluate(sym)).subtract((BigDecimal) rhs.evaluate(sym));
	}

	public AbstractNode analyse(SymbolTableInterface sym) throws AnalyserException {
        // Vereenvoudig de nodes indien mogelijk.
		lhs = lhs.analyse(sym);
		rhs = rhs.analyse(sym);

		// Beide zijden moeten van het type 'number' zijn.
		if (!lhs.getType().equals(BigDecimal.class) || !rhs.getType().equals(BigDecimal.class)) {
			throw new AnalyserException("Expected two parameters of the same type to SUBSTRACT-expression.", token);
		}

		// Wanneer beide zijden constant zijn kunnen we de node vereenvoudigen.
		if (lhs.getIsConstant() && rhs.getIsConstant()) {
			return new NumberNode(evaluate(sym), token);
		}

		// Geef de huidige instantie terug.
		return this;
	}

	public String generateJavascript(SymbolTableInterface sym) {
		return generateJavascriptNodePart(sym, lhs) + " - " + generateJavascriptNodePart(sym, rhs);
	}

	public Class<?> getType() {
		return BigDecimal.class;
	}

	public boolean getIsConstant() {
		return false;
	}
}
