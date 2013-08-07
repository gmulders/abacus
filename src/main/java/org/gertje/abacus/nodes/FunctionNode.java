package org.gertje.abacus.nodes;

import java.util.ArrayList;
import java.util.List;

import org.gertje.abacus.AnalyserException;
import org.gertje.abacus.EvaluationException;
import org.gertje.abacus.Token;
import org.gertje.abacus.nodevisitors.NodeVisitor;
import org.gertje.abacus.nodevisitors.VisitingException;
import org.gertje.abacus.symboltable.NoSuchFunctionException;
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
	public Object evaluate(SymbolTableInterface sym) throws EvaluationException {
		// Maak een lijst met alle resultaten van de evaluatie van de parameters.
		List<Object> paramResults = new ArrayList<Object>();
		// Maak een lijst met alle types van de parameters.
		List<Class<?>> paramTypes = new ArrayList<Class<?>>();

		// Loop over alle nodes heen en vul de lijsten met de geevaluuerde waarde en het type.
		for (AbstractNode node : parameters) {
			paramResults.add(node.evaluate(sym));
			paramTypes.add(node.getType());
		}

		try {
			return sym.getFunctionReturnValue(identifier, paramResults, paramTypes);
		} catch (NoSuchFunctionException e) {
			throw new EvaluationException(e.getMessage(), token);
		}
	}

	@Override
	public AbstractNode analyse(SymbolTableInterface sym) throws AnalyserException {
		// Maak een lijst van Objecten aan waarin we de parameters gaan evalueren.
		List<Class<?>> types = new ArrayList<Class<?>>();

		// Loop over alle nodes heen.
		for (AbstractNode param : parameters) {
			// Voeg het type van de node toe aan de lijst.
			types.add(param.getType());
		}


		// Controleer of de variabele bestaat. Als deze niet bestaat gooien we een exceptie.
		if (!sym.getExistsFunction(identifier, types)) {
			throw new AnalyserException("Function '" + identifier + "' does not exist.", token);
		}

		// Haal het type van de variabele op.
		try {
			returnType = sym.getFunctionReturnType(identifier, parameters);
		} catch (NoSuchFunctionException e) {
			throw new AnalyserException(e.getMessage(), token);
		}

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
	public <R, X extends VisitingException> R accept(NodeVisitor<R, X> visitor) throws X {
		return visitor.visit(this);
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
