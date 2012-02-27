package org.gertje.abacus.nodes;

import org.gertje.abacus.AnalyserException;
import org.gertje.abacus.Token;
import org.gertje.abacus.symboltable.SymbolTableInterface;

public class OrNode extends AbstractNode {

	private AbstractNode lhs;
	private AbstractNode rhs;

	/**
	 * Constructor
	 */
	public OrNode(AbstractNode lhs, AbstractNode rhs, Token token, NodeFactoryInterface nodeFactory) {
		super(8, token, nodeFactory);

		this.lhs = lhs;
		this.rhs = rhs;
	}

	@Override
	public Boolean evaluate(SymbolTableInterface sym) {
		Boolean left = (Boolean) lhs.evaluate(sym);
		Boolean right = (Boolean) rhs.evaluate(sym);

		return Boolean.valueOf(left.booleanValue() || right.booleanValue());
	}

	public AbstractNode analyse(SymbolTableInterface sym) throws AnalyserException {
		// Vereenvoudig de nodes indien mogelijk.
		lhs = lhs.analyse(sym);
		rhs = rhs.analyse(sym);

		// Beide zijden moeten van het type 'boolean' zijn.
		if (!lhs.getType().equals(Boolean.class) || !rhs.getType().equals(Boolean.class)) {
			throw new AnalyserException("Expected two boolean parameters to OR-expression.", token);
		}

		// Wanneer beide zijden constant zijn kunnen we de node vereenvoudigen.
		if (lhs.getIsConstant() && rhs.getIsConstant()) {
			return nodeFactory.createBooleanNode(evaluate(sym), token);
		}

		// Wanneer slechts de linker zijde of de rechter zijde constant is en deze naar 'true' evalueert, evalueert de
		// hele expressie naar true en kunnen we de node vereenvoudigen.
		if (lhs.getIsConstant() && ((Boolean)lhs.evaluate(sym)).booleanValue()
				|| rhs.getIsConstant() && !((Boolean)rhs.evaluate(sym)).booleanValue()) {
			return nodeFactory.createBooleanNode(Boolean.TRUE, token);
		}

		// Wanneer slechts de linker zijde of de rechter zijde constant is en deze naar 'false' evalueert, evalueert de
		// huidige expressie naar de niet constante expressie.
		if (lhs.getIsConstant() && !((Boolean)lhs.evaluate(sym)).booleanValue()) {
			return rhs;
		} else if (rhs.getIsConstant() && !((Boolean)rhs.evaluate(sym)).booleanValue()) {
			return lhs;
		}

		// Geef de huidige instantie terug.
		return this;
	}

	public String generateJavascript(SymbolTableInterface sym) {
		return generateJavascriptNodePart(sym, lhs) + " || " + generateJavascriptNodePart(sym, rhs);
	}

	public Class<?> getType() {
		return Boolean.class;
	}

	public boolean getIsConstant() {
		return false;
	}
}
