package org.gertje.abacus.nodes;

import org.gertje.abacus.AnalyserException;
import org.gertje.abacus.Token;
import org.gertje.abacus.nodevisitors.NodeVisitorInterface;
import org.gertje.abacus.nodevisitors.VisitingException;
import org.gertje.abacus.symboltable.SymbolTableInterface;

public class AssignmentNode extends AbstractNode {

	VariableNode lhs;
	AbstractNode rhs;

	public AssignmentNode(VariableNode lhs, AbstractNode rhs, Token token, NodeFactoryInterface nodeFactory) {
		super(1, token, nodeFactory);

		this.lhs = lhs;
		this.rhs = rhs;
	}

	@Override
	public AbstractNode analyse(SymbolTableInterface sym) throws AnalyserException {
		// Vereenvoudig de rechterkant van de expressie.
		rhs = rhs.analyse(sym);

		return this;
	}

	@Override
	public Object evaluate(SymbolTableInterface sym) {
		// Evalueer de rechterkant van de toekenning.
		Object result = rhs.evaluate(sym);
		// Zet het resultaat in de symboltable.
		sym.setVariableValue(((VariableNode) lhs).getIdentifier(), result);
		
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

	public VariableNode getLhs() {
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
