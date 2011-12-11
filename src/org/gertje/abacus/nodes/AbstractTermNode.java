package org.gertje.abacus.nodes;

import java.math.BigDecimal;

import org.gertje.abacus.AnalyserException;
import org.gertje.abacus.Token;
import org.gertje.abacus.symboltable.SymbolTableInterface;

abstract class AbstractTermNode extends AbstractNode {

	protected AbstractNode lhs;
	protected AbstractNode rhs;

	protected String operator;
	
	public AbstractTermNode(AbstractNode lhs, AbstractNode rhs, Token token, int precedence, String operator) {
		this.lhs = lhs;
		this.rhs = rhs;
		this.token = token;
		this.precedence = precedence;
		this.operator = operator;
	}

	@Override
	public BigDecimal evaluate(SymbolTableInterface sym) {
		return term((BigDecimal) lhs.evaluate(sym), (BigDecimal) rhs.evaluate(sym));
	}

	abstract protected BigDecimal term(BigDecimal left, BigDecimal right);
	
	@Override
	public AbstractNode analyse(SymbolTableInterface sym) throws AnalyserException {
		// Vereenvoudig de nodes indien mogelijk.
		lhs = lhs.analyse(sym);
		rhs = rhs.analyse(sym);

		// Beide zijden moeten van het type 'number' zijn.
		if (!lhs.getType().equals(BigDecimal.class) || !rhs.getType().equals(BigDecimal.class)) {
			throw new AnalyserException("Expected two parameters of type 'number' to term-expression.", token);
		}

		// Wanneer beide zijden constant zijn kunnen we de node vereenvoudigen.
		if (lhs.getIsConstant() && rhs.getIsConstant()) {
			return new NumberNode(evaluate(sym), token);
		}

		// Geef de huidige instantie terug.
		return this;
	}

	@Override
	public String generateJavascript(SymbolTableInterface sym) {
		return generateJavascriptNodePart(sym, lhs) + " " + operator + " " + generateJavascriptNodePart(sym, rhs);
	}

	@Override
	public boolean getIsConstant() {
		// Geen enkele AbstractTermNode is constant. 
		return false;
	}

	@Override
	public Class<?> getType() {
		return BigDecimal.class;
	}
}
