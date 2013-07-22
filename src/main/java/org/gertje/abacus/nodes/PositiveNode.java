package org.gertje.abacus.nodes;

import java.math.BigDecimal;
import java.math.BigInteger;

import org.gertje.abacus.AnalyserException;
import org.gertje.abacus.EvaluationException;
import org.gertje.abacus.Token;
import org.gertje.abacus.nodevisitors.NodeVisitorInterface;
import org.gertje.abacus.nodevisitors.VisitingException;
import org.gertje.abacus.symboltable.SymbolTableInterface;

public class PositiveNode extends AbstractNode {

	AbstractNode argument;

	/**
	 * Constructor
	 */
	public PositiveNode(AbstractNode argument, Token token, NodeFactoryInterface nodeFactory) {
		super(2, token, nodeFactory);

		this.argument = argument;
	}

	@Override
	public Number evaluate(SymbolTableInterface sym) throws EvaluationException {
		return (Number) argument.evaluate(sym);
	}

	@Override
	public AbstractNode analyse(SymbolTableInterface sym) throws AnalyserException {
		argument = argument.analyse(sym);

		// Het argument moet een float of een integer zijn.
		if (!argument.getType().equals(BigDecimal.class) && !argument.getType().equals(BigInteger.class)) {
			throw new AnalyserException("Expected a boolean expression in PositiveNode.", token);
		}

		// Eigenlijk doet deze node niets... Geef daarom altijd het argument terug.
		return argument;
	}

	@Override
	public boolean getIsConstant() {
		return false;
	}

	@Override
	public Class<?> getType() {
		return argument.getType();
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