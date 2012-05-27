package org.gertje.abacus.nodes;

import java.util.List;

import org.gertje.abacus.AnalyserException;
import org.gertje.abacus.Token;
import org.gertje.abacus.nodevisitors.NodeVisitorInterface;
import org.gertje.abacus.nodevisitors.VisitingException;
import org.gertje.abacus.symboltable.SymbolTableInterface;


public class FunctionNode extends AbstractNode {

	private String identifier;
	private List<AbstractNode> parameters;
	private Class<?> returnType;

	/**
	 * Constructor
	 */
	public FunctionNode(String identifier, List<AbstractNode> parameters, Token token, NodeFactoryInterface nodeFactory) {
		super(1, token, nodeFactory);

		this.identifier = identifier;
		this.parameters = parameters;
	}

	@Override
	public Object evaluate(SymbolTableInterface sym) {
		return sym.getFunctionReturnValue(identifier, parameters);
	}

	@Override
	public AbstractNode analyse(SymbolTableInterface sym) throws AnalyserException {
		// Controleer of de variabele bestaat. Als deze niet bestaat gooien we een exceptie.
		if (!sym.getExistsFunction(identifier, parameters)) {
			throw new AnalyserException("Function '" + identifier + "' does not exist.", token);
		}

		// Haal het type van de variabele op.
		returnType = sym.getFunctionReturnType(identifier, parameters);
		return this;
	}

	@Override
	public Class<?> getType() {
		return returnType;
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

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	public List<AbstractNode> getParameters() {
		return parameters;
	}

	public void setParameters(List<AbstractNode> parameters) {
		this.parameters = parameters;
	}

	public Class<?> getReturnType() {
		return returnType;
	}

	public void setReturnType(Class<?> returnType) {
		this.returnType = returnType;
	}
}
