package org.gertje.abacus.nodes;

import java.math.BigDecimal;
import java.math.BigInteger;

import org.gertje.abacus.AnalyserException;
import org.gertje.abacus.EvaluationException;
import org.gertje.abacus.Token;
import org.gertje.abacus.nodevisitors.NodeVisitor;
import org.gertje.abacus.nodevisitors.VisitingException;
import org.gertje.abacus.symboltable.SymbolTable;

public class SubstractNode extends AbstractNode {

	private AbstractNode lhs;
	private AbstractNode rhs;

	/**
	 * Constructor
	 */
	public SubstractNode(AbstractNode lhs, AbstractNode rhs, Token token, NodeFactory nodeFactory) {
		super(5, token, nodeFactory);

		this.lhs = lhs;
		this.rhs = rhs;
	}

	@Override
	public Number evaluate(SymbolTable sym) throws EvaluationException {
		Number lhsValue = (Number) lhs.evaluate(sym);
		Number rhsValue = (Number) rhs.evaluate(sym);
		
		// Wanneer de linkerkant of de rechterkant leeg zijn, is het resultaat van deze expressie ook leeg.
		if (lhsValue == null || rhsValue == null) {
			return null;
		}
		
		// Wanneer een van beide zijden een BigDecimal is, is het resultaat een BigDecimal, anders een BigInteger.
		if (lhsValue instanceof BigDecimal && rhsValue instanceof BigDecimal) {
			return ((BigDecimal)lhsValue).subtract((BigDecimal)rhsValue);
		} else if (lhsValue instanceof BigDecimal && rhsValue instanceof BigInteger) {
			return ((BigDecimal)lhsValue).subtract(new BigDecimal((BigInteger)rhsValue));
		} else if (lhsValue instanceof BigInteger && rhsValue instanceof BigDecimal) {
			return (new BigDecimal((BigInteger)lhsValue)).subtract((BigDecimal)rhsValue);
		} else {
			return ((BigInteger)lhsValue).subtract((BigInteger)rhsValue);
		}
	}

	/**
	 * Controleert of de node correct is van syntax.
	 */
	private boolean checkTypes() {
		// Zowel de lhs als de rhs moet een getal zijn.
		return isNumber(lhs.getType()) && isNumber(rhs.getType());
	}

	@Override
	public AbstractNode analyse(SymbolTable sym) throws AnalyserException {
        // Vereenvoudig de nodes indien mogelijk.
		lhs = lhs.analyse(sym);
		rhs = rhs.analyse(sym);

		// Beide zijden moeten van het type 'number' zijn.
		if (!checkTypes()) {
			throw new AnalyserException("Expected two parameters of number type to SUBSTRACT-expression.", token);
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
	public Class<?> getType() {
		if (lhs.getType().equals(BigDecimal.class) || rhs.getType().equals(BigDecimal.class)) {
			return BigDecimal.class;
		}
		return BigInteger.class;
	}

	@Override
	public boolean getIsConstant() {
		return false;
	}

	@Override
	public <R, X extends VisitingException> R accept(NodeVisitor<R, X> visitor) throws X {
		return visitor.visit(this);
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
