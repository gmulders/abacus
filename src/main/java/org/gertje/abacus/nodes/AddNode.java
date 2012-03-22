package org.gertje.abacus.nodes;

import java.math.BigDecimal;
import java.math.BigInteger;

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

	@Override
	public Object evaluate(SymbolTableInterface sym) {
		Object left = lhs.evaluate(sym);
		Object right = rhs.evaluate(sym);

		if (left == null || right == null) {
			return null;
		}
		
		// Wanneer het type een number is moeten we gewoon plus doen, anders gebruiken we een punt om de strings aan
		// elkaar te plakken.
		if (left instanceof BigDecimal && right instanceof BigDecimal) {
			return ((BigDecimal)left).add((BigDecimal)right);
		} else if (left instanceof BigDecimal && right instanceof BigInteger) {
			return ((BigDecimal)left).add(new BigDecimal((BigInteger)right));
		} else if (left instanceof BigInteger && right instanceof BigDecimal) {
			return (new BigDecimal((BigInteger)left)).add((BigDecimal)right);
		} else if (left instanceof BigInteger && right instanceof BigInteger) {
			return ((BigInteger)left).add((BigInteger)right);
		}
		return ((String)left)+((String)right);
	}

	/**
	 * Controleert of de node correct is van syntax.
	 */
	private boolean checkTypes() {
		// Wanneer beide zijden van het type 'string' zijn hoeven we geen exceptie te gooien.
		if (lhs.getType().equals(String.class) && rhs.getType().equals(String.class)) {
			return true;
		}

		// Wanneer beide zijden getallen zijn hoeven we geen exceptie te gooien.
		if (isNumber(rhs.getType()) && isNumber(rhs.getType())) {
			return true;
		}

		// Wanneer we hier komen zijn de de parameters niet correct en moeten we een exceptie gooien.
		return false;
	}
	
	@Override
	public AbstractNode analyse(SymbolTableInterface sym) throws AnalyserException {
        // Vereenvoudig de nodes indien mogelijk.
		lhs = lhs.analyse(sym);
		rhs = rhs.analyse(sym);

		// Controleer de node.
		if (!checkTypes()) {
			throw new AnalyserException("Expected two parameters of type 'Number' or type 'String' to ADD-expression.", token);
		}

		// Wanneer beide zijden constant zijn kunnen we de node vereenvoudigen.
		if (lhs.getIsConstant() && rhs.getIsConstant()) {
			if (getType().equals(BigDecimal.class)) {
				return nodeFactory.createFloatNode((BigDecimal) evaluate(sym), token);
			} else if (getType().equals(BigInteger.class)) {
				return nodeFactory.createIntegerNode((BigInteger) evaluate(sym), token);
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
		if (lhs.getType().equals(String.class)) {
			return String.class;
		}
		if (lhs.getType().equals(BigDecimal.class) || rhs.getType().equals(BigDecimal.class)) {
			return BigDecimal.class;
		}
		return BigInteger.class;
	}

	public boolean getIsConstant() {
		return false;
	}
}
