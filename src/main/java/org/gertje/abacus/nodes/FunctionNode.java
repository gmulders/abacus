package org.gertje.abacus.nodes;

import org.gertje.abacus.Token;
import org.gertje.abacus.nodevisitors.NodeVisitor;
import org.gertje.abacus.nodevisitors.VisitingException;

import java.util.List;

public class FunctionNode extends AbstractNode {

	private String identifier;
	private List<AbstractNode> parameters;
	private Class<?> returnType;

	/**
	 * Constructor
	 */
	public FunctionNode(String identifier, List<AbstractNode> parameters, Token token) {
		super(1, token);

		this.identifier = identifier;
		this.parameters = parameters;
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
