package org.gertje.abacus.nodes;

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
		return Boolean.valueOf(compare(lhs.evaluate(sym), rhs.evaluate(sym)));
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
			if (lhs.getType().equals(type) && rhs.getType().equals(type)) {
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
