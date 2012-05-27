package org.gertje.abacus.nodes;

import org.gertje.abacus.AnalyserException;
import org.gertje.abacus.Token;
import org.gertje.abacus.nodevisitors.NodeVisitorInterface;
import org.gertje.abacus.nodevisitors.VisitingException;
import org.gertje.abacus.symboltable.SymbolTableInterface;

public class NotNode extends AbstractNode {

	AbstractNode argument;

	/**
	 * Constructor
	 */
	public NotNode(AbstractNode argument, Token token, NodeFactoryInterface nodeFactory) {
		super(2, token, nodeFactory);

		this.argument = argument;
	}

	@Override
	public Boolean evaluate(SymbolTableInterface sym) {
		// Bepaal de waarde van de boolean.
		Boolean bool = (Boolean) argument.evaluate(sym);
		
		// Wanneer de boolean leeg is, is het resultaat van deze expressie ook leeg.
		if (bool == null) {
			return null;
		}

		return Boolean.valueOf(!bool.booleanValue());
	}

	@Override
	public AbstractNode analyse(SymbolTableInterface sym) throws AnalyserException {
		argument = argument.analyse(sym);

		// Het argument moet een boolean zijn.
		if (argument.getType().equals(Boolean.class)) {
			throw new AnalyserException("Expected a boolean expression in NotNode.", token);
		}

		// Wanneer het argument constant is kunnen we hem vereenvoudigen.
		if (argument.getIsConstant()) {
			return nodeFactory.createBooleanNode(evaluate(sym), token);
		}

		// We kunnen de node niet vereenvoudigen, geef de huidige instantie terug.
		return this;
	}

	@Override
	public boolean getIsConstant() {
		return false;
	}

	@Override
	public Class<?> getType() {
		return Boolean.class;
	}

	@Override
	public void accept(NodeVisitorInterface visitor) throws VisitingException {
		visitor.visit(this);		
	}

	public AbstractNode getArgument() {
		return argument;
	}

	public void setArgument(AbstractNode argument) {
		this.argument = argument;
	}
}
