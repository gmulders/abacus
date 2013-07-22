package org.gertje.abacus.nodes;

import org.gertje.abacus.AnalyserException;
import org.gertje.abacus.EvaluationException;
import org.gertje.abacus.Token;
import org.gertje.abacus.nodevisitors.NodeVisitorInterface;
import org.gertje.abacus.nodevisitors.VisitingException;
import org.gertje.abacus.symboltable.NoSuchVariableException;
import org.gertje.abacus.symboltable.SymbolTableInterface;

public class VariableNode extends AbstractNode {

	private String identifier;
	private Class<?> type;

	/**
	 * Constructor
	 */
	public VariableNode(String identifier, Token token, NodeFactoryInterface nodeFactory) {
		super(1, token, nodeFactory);

		this.identifier = identifier;
	}

	@Override
	public Object evaluate(SymbolTableInterface sym) throws EvaluationException {
		try {
			return sym.getVariableValue(identifier);
		} catch (NoSuchVariableException e) {
			throw new EvaluationException(e.getMessage(), token);
		}
	}

	@Override
	public AbstractNode analyse(SymbolTableInterface sym) throws AnalyserException {
		// Controleer of de variabele bestaat. Als deze niet bestaat gooien we een exceptie.
		if (!sym.getExistsVariable(identifier)) {
			throw new AnalyserException("Variable '" + identifier + "' does not exist.", token);
		}

		try {
			// Haal het type van de variabele op.
			type = sym.getVariableType(identifier);
		} catch (NoSuchVariableException e) {
			throw new AnalyserException(e.getMessage(), token);
		}

		return this;
	}

	@Override
	public Class<?> getType() {
		return type;
	}

	@Override
	public boolean getIsConstant() {
		return false;
	}

	@Override
	public void accept(NodeVisitorInterface visitor) throws VisitingException {
		visitor.visit(this);		
	}
	
	public String getIdentifier() {
		return identifier;
	}
}
