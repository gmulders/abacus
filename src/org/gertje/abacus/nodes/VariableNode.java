package org.gertje.abacus.nodes;

import org.gertje.abacus.AnalyserException;
import org.gertje.abacus.Token;
import org.gertje.abacus.symboltable.SymbolTableInterface;

public class VariableNode extends AbstractNode {

	private String identifier;
	private Class<?> type;

	/**
	 * Constructor
	 */
	public VariableNode(String identifier, Token token) {
		this.identifier = identifier;
		this.token = token;
	}

	public Object evaluate(SymbolTableInterface sym) {
		return sym.getVariableValue(identifier);
	}

	public AbstractNode analyse(SymbolTableInterface sym) throws AnalyserException {
		// Controleer of de variabele bestaat. Als deze niet bestaat gooien we een exceptie.
		if (!sym.getExistsVariable(identifier)) {
			throw new AnalyserException("Variable '" + identifier + "' does not exist.", token);
		}

		// Haal het type van de variabele op.
		type = sym.getVariableType(identifier);
		return this;
	}

	public String generateJavascript(SymbolTableInterface sym) {
		return identifier;
	}

	public Class<?> getType() {
		return type;
	}

	public boolean getIsConstant() {
		return false;
	}
	
	public String getIdentifier() {
		return identifier;
	}
}
