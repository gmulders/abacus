package org.gertje.abacus.nodes;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

import org.gertje.abacus.AnalyserException;
import org.gertje.abacus.Token;
import org.gertje.abacus.symboltable.SymbolTableInterface;

public abstract class AbstractComparisonNode extends AbstractNode {

	protected AbstractNode lhs;
	protected AbstractNode rhs;
	
	/**
	 * Lijst met toegestane types voor deze operatie.
	 */
	protected List<Class<?>> allowedTypes;

	/**
	 * Bevat de operator zoals weegegeven in javascript.
	 */
	protected String operator;
	
	public AbstractComparisonNode(AbstractNode lhs, AbstractNode rhs, Token token, int precedence, String operator, 
			NodeFactoryInterface nodeFactory) {
		super(precedence, token, nodeFactory);
		this.lhs = lhs;
		this.rhs = rhs;

		this.operator = operator;
	}

	@Override
	public Boolean evaluate(SymbolTableInterface sym) {
		Object left = lhs.evaluate(sym);
		Object right = rhs.evaluate(sym);

		if (left == null || right == null) {
			return null;
		}
		
		// Wanneer de waarde een BigInteger is casten we het naar een BigDecimal.
		if (left instanceof BigInteger) {
			left = new BigDecimal((BigInteger)left);
		}
		// Wanneer de waarde een BigInteger is casten we het naar een BigDecimal.
		if (right instanceof BigInteger) {
			right = new BigDecimal((BigInteger)right);
		}
		return Boolean.valueOf(compare(left, right));
	}

	private <T extends Comparable<? super T>> boolean compare(Object left, Object right) {
		return compare((Comparable<T>) left, (T) right);
	}

	abstract protected <T extends Comparable<? super T>> boolean compare(Comparable<T> left, T right);
	
	@Override
	public AbstractNode analyse(SymbolTableInterface sym) throws AnalyserException {
		// Vereenvoudig de nodes indien mogelijk.
		lhs = lhs.analyse(sym);
		rhs = rhs.analyse(sym);

		// Beide zijden moeten van het hetzelfde, toegestane type zijn.
		if (!checkTypes()) {
			throw new AnalyserException("Expected two parameters of the same type to comparison-expression.", token);
		}

		// Wanneer beide zijden constant zijn kunnen we de node vereenvoudigen.
		if (lhs.getIsConstant() && rhs.getIsConstant()) {
			return nodeFactory.createBooleanNode(evaluate(sym), token);
		}

		// Geef de huidige instantie terug.
		return this;
	}

	/**
	 * Controleert de typen van de lhs en de rhs, wanneer beiden niet van het zelfde type zijn of ze komen niet voor in
	 * de lijst met toegestane typen geeft de methode false terug.
	 * @return <code>true</code> wanneer de typen goed zijn, anders <code>false</code>.
	 */
	private boolean checkTypes() {
		for(Class<?> type : allowedTypes) {
			Class<?> lhsType = lhs.getType();
			Class<?> rhsType = rhs.getType();
			
			// We casten de BigInteger's naar BigDecimal's, omdat dit makkelijk te vergelijken is.
			if (lhsType.equals(BigInteger.class)) {
				lhsType = BigDecimal.class;
			}
			// We casten de BigInteger's naar BigDecimal's, omdat dit makkelijk te vergelijken is.
			if (rhsType.equals(BigInteger.class)) {
				rhsType = BigDecimal.class;
			}
			
			if (lhsType.equals(type) && rhsType.equals(type)) {
				return true;
			}
		}
		
		return false;
	}

	@Override
	public String generateJavascript(SymbolTableInterface sym) {
		return generateJavascriptNodePart(sym, lhs) + " " + operator + " " + generateJavascriptNodePart(sym, rhs);
	}

	@Override
	public boolean getIsConstant() {
		// Geen enkele AbstractCompareNode is constant. 
		return false;
	}

	@Override
	public Class<?> getType() {
		return Boolean.class;
	}
}
