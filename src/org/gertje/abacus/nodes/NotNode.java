package org.gertje.abacus.nodes;

import org.gertje.abacus.AnalyserException;
import org.gertje.abacus.Token;
import org.gertje.abacus.symboltable.SymbolTableInterface;

public class NotNode extends AbstractNode {

	AbstractNode argument;

	/**
	 * Constructor
	 */
	public NotNode(AbstractNode argument, Token token) {
		this.argument = argument;
		this.token = token;
		precedence = 2;
	}

	public Boolean evaluate(SymbolTableInterface sym) {
		return Boolean.valueOf(!((Boolean) argument.evaluate(sym)).booleanValue());
	}

	public AbstractNode analyse(SymbolTableInterface sym) throws AnalyserException {
		argument = argument.analyse(sym);

		// Het argument moet een boolean zijn.
		if (argument.getType().equals(Boolean.class)) {
			throw new AnalyserException("Expected a boolean expression in NotNode.", token);
		}

		// Wanneer het argument constant is kunnen we hem vereenvoudigen.
		if (argument.getIsConstant()) {
			return new BooleanNode(evaluate(sym), token);
		}

		// We kunnen de node niet vereenvoudigen, geef de huidige instantie terug.
		return this;
	}

	@Override
	public String generateJavascript(SymbolTableInterface sym) {
		return "!" + generateJavascriptNodePart(sym, argument);
	}

	@Override
	public boolean getIsConstant() {
		return false;
	}

	@Override
	public Class<?> getType() {
		return Boolean.class;
	}
}
