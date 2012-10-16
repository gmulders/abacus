package org.gertje.abacus.nodes;

import org.gertje.abacus.AnalyserException;
import org.gertje.abacus.Token;
import org.gertje.abacus.nodevisitors.NodeVisitorInterface;
import org.gertje.abacus.nodevisitors.VisitingException;
import org.gertje.abacus.symboltable.SymbolTableInterface;

public class AssignmentNode extends AbstractNode {

	AbstractNode lhs;
	AbstractNode rhs;

	public AssignmentNode(AbstractNode lhs, AbstractNode rhs, Token token, NodeFactoryInterface nodeFactory) {
		super(1, token, nodeFactory);

		this.lhs = lhs;
		this.rhs = rhs;
	}

	@Override
	public AbstractNode analyse(SymbolTableInterface sym) throws AnalyserException {
		// Analyseer de linker en de rechterkant van de expressie.
		lhs = lhs.analyse(sym);
		rhs = rhs.analyse(sym);
		
		// Controleer of de linkerkant een variabele is.
		if (!checkLhs()) {
			throw new AnalyserException("Left side of assignment should be a variable or an assignment.", token);
		}
		
		// Controleer of de types van de linker en de rechterkant overeenkomen.
		if (lhs.getType() != rhs.getType()) {
			throw new AnalyserException("Expected expression of the same type as the variable.", token);
		}

		return this;
	}
	
	/**
	 * Controleert of de linkerkant van de assignment een variabele is, of een andere assignment met aan de rechterkant
	 * een variabele.
	 * @return <code>true</code> wanneer de linkerkant geldig is, anders <code>false</code>.
	 */
	private boolean checkLhs() {
		// Als de linkerkant een VariabeleNode is geven we true terug.
		if (lhs instanceof VariableNode) {
			return true;
		}
		
		// Als de linkerkant een AssignmentNode is geven we true terug wanneer de rhs hiervan een VariableNode is.
		if (lhs instanceof AssignmentNode) {
			return (((AssignmentNode)lhs).getRhs() instanceof VariableNode);
		}
		
		return false;
	}

	@Override
	public Object evaluate(SymbolTableInterface sym) {
		// Evalueer de rechterkant van de toekenning.
		Object result = rhs.evaluate(sym);

		// Zet het resultaat in de symboltable.
		// Wanneer de linkerkant een variabele is kunnen we het direct in de variabele zetten, anders moeten we eerst
		// de variabele uit de rechterkant halen.
		if (lhs instanceof VariableNode) {
			sym.setVariableValue(((VariableNode) lhs).getIdentifier(), result);
		} else {
			sym.setVariableValue(((VariableNode) ((AssignmentNode)lhs).getRhs()).getIdentifier(), result);
		}

		// Geef het resultaat terug.
		return result;
	}

	@Override
	public boolean getIsConstant() {
		return false;
	}

	@Override
	public Class<?> getType() {
		return rhs.getType();
	}

	@Override
	public void accept(NodeVisitorInterface visitor) throws VisitingException {
		visitor.visit(this);		
	}

	public AbstractNode getLhs() {
		return lhs;
	}

	public void setLhs(VariableNode lhs) {
		this.lhs = lhs;
	}

	public AbstractNode getRhs() {
		return rhs;
	}

	public void setRhs(AbstractNode rhs) {
		this.rhs = rhs;
	}
}
