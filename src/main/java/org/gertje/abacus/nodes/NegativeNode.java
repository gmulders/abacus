package org.gertje.abacus.nodes;

import java.math.BigDecimal;
import java.math.BigInteger;

import org.gertje.abacus.AnalyserException;
import org.gertje.abacus.EvaluationException;
import org.gertje.abacus.Token;
import org.gertje.abacus.nodevisitors.NodeVisitor;
import org.gertje.abacus.nodevisitors.VisitingException;
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

	@Override
	public Number evaluate(SymbolTableInterface sym) throws EvaluationException {
		// Bepaal het getal dat we negatief gaan maken.
		Number number = (Number)argument.evaluate(sym);
		
		// Wanneer het getal leeg is, is het resultaat van deze expressie ook leeg.
		if (number == null) {
			return null;
		}

		// Cast het argument naar het juiste type voordat we negate erop aan kunnen roepen.
		if (argument.getType().equals(BigDecimal.class)) {
			return ((BigDecimal) number).negate();
		}
		return ((BigInteger) number).negate();
	}

	/**
	 * Controleert of de node correct is van syntax.
	 * @throws AnalyserException
	 */
	private void checkNode() throws AnalyserException {
		// Het argument moet een getal zijn.
		if (!isNumber(argument.getType())) {
			throw new AnalyserException("Expected a number expression in NegativeNode.", token);
		}		
	}
	
	@Override
	public AbstractNode analyse(SymbolTableInterface sym) throws AnalyserException {
		argument = argument.analyse(sym);

		// Controleer de node.
		checkNode();

		try {
			// Wanneer het argument constant is en een float kunnen we hem vereenvoudigen.
			if (argument.getIsConstant() && argument.getType().equals(BigDecimal.class)) {
				return nodeFactory.createFloatNode((BigDecimal)evaluate(sym), token);
			}

			// Wanneer het argument constant is en een integer kunnen we hem vereenvoudigen.
			if (argument.getIsConstant() && argument.getType().equals(Integer.class)) {
				return nodeFactory.createIntegerNode((BigInteger)evaluate(sym), token);
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
		return argument.getType();
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
