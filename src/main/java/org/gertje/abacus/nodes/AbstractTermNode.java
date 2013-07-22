package org.gertje.abacus.nodes;

import java.math.BigDecimal;
import java.math.BigInteger;

import org.gertje.abacus.AnalyserException;
import org.gertje.abacus.EvaluationException;
import org.gertje.abacus.Token;
import org.gertje.abacus.symboltable.SymbolTableInterface;

abstract class AbstractTermNode extends AbstractNode {

	protected AbstractNode lhs;
	protected AbstractNode rhs;

	public AbstractTermNode(AbstractNode lhs, AbstractNode rhs, Token token, int precedence,
			NodeFactoryInterface nodeFactory) {
		super(precedence, token, nodeFactory);
		this.lhs = lhs;
		this.rhs = rhs;
	}

	@Override
	public Number evaluate(SymbolTableInterface sym) throws EvaluationException {
		Number leftValue = (Number) lhs.evaluate(sym);
		Number rightValue = (Number) rhs.evaluate(sym);
		
		if (leftValue == null || rightValue == null) {
			return null;
		}
		
		// Bepaal aan de hand van het type van links en rechts welke term we aan moeten roepen.
		if (leftValue instanceof BigDecimal && rightValue instanceof BigDecimal) {
			return term((BigDecimal)leftValue, (BigDecimal)rightValue);
		} else if (leftValue instanceof BigDecimal && rightValue instanceof BigInteger) {
			return term((BigDecimal)leftValue, (BigInteger)rightValue);
		} else if (leftValue instanceof BigInteger && rightValue instanceof BigDecimal) {
			return term((BigInteger)leftValue, (BigDecimal)rightValue);
		} else {
			return term((BigInteger)leftValue, (BigInteger)rightValue);
		}
	}

	abstract protected Number term(BigDecimal left, BigDecimal right);
	abstract protected Number term(BigDecimal left, BigInteger right);
	abstract protected Number term(BigInteger left, BigDecimal right);
	abstract protected Number term(BigInteger left, BigInteger right);
	
	@Override
	public AbstractNode analyse(SymbolTableInterface sym) throws AnalyserException {
		// Vereenvoudig de nodes indien mogelijk.
		lhs = lhs.analyse(sym);
		rhs = rhs.analyse(sym);

		// Beide zijden moeten van het type 'number' zijn.
		if ((!lhs.getType().equals(BigDecimal.class) && !lhs.getType().equals(BigInteger.class))
				|| (!rhs.getType().equals(BigDecimal.class) && !rhs.getType().equals(BigInteger.class))) {
			throw new AnalyserException("Expected two parameters of type 'number' to term-expression.", token);
		}

		// Wanneer beide zijden constant zijn kunnen we de node vereenvoudigen.
		if (lhs.getIsConstant() && rhs.getIsConstant()) {
			Number value;
			try {
				value = evaluate(sym);
			} catch (EvaluationException e) {
				throw new AnalyserException(e.getMessage(), token);
			}
			if (value instanceof BigDecimal) {
				return nodeFactory.createFloatNode((BigDecimal)value, token);
			}
			return nodeFactory.createIntegerNode((BigInteger)value, token);
		}

		// Geef de huidige instantie terug.
		return this;
	}

	@Override
	public boolean getIsConstant() {
		return false;
	}

	public AbstractNode getLhs() {
		return lhs;
	}

	public void setLhs(AbstractNode lhs) {
		this.lhs = lhs;
	}

	public AbstractNode getRhs() {
		return rhs;
	}

	public void setRhs(AbstractNode rhs) {
		this.rhs = rhs;
	}
	
	
}
