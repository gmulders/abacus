package org.gertje.abacus.nodes;

import org.gertje.abacus.AnalyserException;
import org.gertje.abacus.Token;
import org.gertje.abacus.symboltable.SymbolTableInterface;

public class AssignmentNode extends AbstractNode {

	VariableNode lhs;
	AbstractNode rhs;

	public AssignmentNode(VariableNode lhs, AbstractNode rhs, Token token, NodeFactoryInterface nodeFactory) {
		super(0, token, nodeFactory);

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
	public String generateJavascript(SymbolTableInterface sym) {
		return lhs.generateJavascript(sym) + " = " + generateJavascriptNodePart(sym, rhs);
	}

	@Override
	public boolean getIsConstant() {
		return false;
	}

	@Override
	public Class<?> getType() {
		return rhs.getType();
	}

}
