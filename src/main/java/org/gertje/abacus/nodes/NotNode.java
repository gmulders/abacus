package org.gertje.abacus.nodes;

import org.gertje.abacus.AnalyserException;
import org.gertje.abacus.EvaluationException;
import org.gertje.abacus.Token;
import org.gertje.abacus.nodevisitors.NodeVisitor;
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
	public Boolean evaluate(SymbolTableInterface sym) throws EvaluationException {
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

		try {
			// Wanneer het argument constant is kunnen we hem vereenvoudigen.
			if (argument.getIsConstant()) {
				return nodeFactory.createBooleanNode(evaluate(sym), token);
			}
		} catch (EvaluationException e) {
			throw new AnalyserException(e.getMessage(), token);
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
	public <R, X extends VisitingException> R accept(NodeVisitor<R, X> visitor) throws X {
		return visitor.visit(this);
	}

	public AbstractNode getArgument() {
		return argument;
	}

	public void setArgument(AbstractNode argument) {
		this.argument = argument;
	}
}
