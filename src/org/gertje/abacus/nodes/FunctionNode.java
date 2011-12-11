package org.gertje.abacus.nodes;

import java.util.List;

import org.gertje.abacus.AnalyserException;
import org.gertje.abacus.Token;
import org.gertje.abacus.symboltable.SymbolTableInterface;


public class FunctionNode extends AbstractNode {

	private String identifier;
	private List<AbstractNode> parameters;
	private Class<?> returnType;

	/**
	 * Constructor
	 */
	public FunctionNode(String identifier, List<AbstractNode> parameters, Token token) {
		this.identifier = identifier;
		this.parameters = parameters;
		this.token = token;
	}

	public Object evaluate(SymbolTableInterface sym) {
		return sym.getFunctionReturnValue(identifier, parameters);
	}

	public AbstractNode analyse(SymbolTableInterface sym) throws AnalyserException {
		// Controleer of de variabele bestaat. Als deze niet bestaat gooien we een exceptie.
		if (!sym.getExistsFunction(identifier, parameters)) {
			throw new AnalyserException("Function '" + identifier + "' does not exist.", token);
		}

		// Haal het type van de variabele op.
		returnType = sym.getFunctionReturnType(identifier, parameters);
		return this;
	}

	public String generateJavascript(SymbolTableInterface sym) {
		return identifier + "Function";
	}

	public Class<?> getType() {
		return returnType;
	}

	public boolean getIsConstant() {
		return false;
	}
}
