package org.gertje.abacus.nodes;

import java.math.BigDecimal;

import org.gertje.abacus.AnalyserException;
import org.gertje.abacus.Token;
import org.gertje.abacus.symboltable.SymbolTableInterface;

public class NegativeNode extends AbstractNode {

	AbstractNode argument;

	/**
	 * Constructor
	 */
	public NegativeNode(AbstractNode argument, Token token, NodeFactoryInterface nodeFactory) {
		super(2, token, nodeFactory);

		this.argument = argument;
	}

	public BigDecimal evaluate(SymbolTableInterface sym) {
		return ((BigDecimal) argument.evaluate(sym)).negate();
	}

	public AbstractNode analyse(SymbolTableInterface sym) throws AnalyserException {
		argument = argument.analyse(sym);

		// Het argument moet een boolean zijn.
		if (argument.getType().equals(BigDecimal.class)) {
			throw new AnalyserException("Expected a boolean expression in NegativeNode.", token);
		}

		// Wanneer het argument constant is kunnen we hem vereenvoudigen.
		if (argument.getIsConstant()) {
			return nodeFactory.createNumberNode(evaluate(sym), token);
		}

		// We kunnen de node niet vereenvoudigen, geef de huidige instantie terug.
		return this;
	}

	@Override
	public String generateJavascript(SymbolTableInterface sym) {
		return "-" + generateJavascriptNodePart(sym, argument);
	}

	@Override
	public boolean getIsConstant() {
		return false;
	}

	@Override
	public Class<?> getType() {
		return BigDecimal.class;
	}
}