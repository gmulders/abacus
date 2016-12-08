package org.gertje.abacus.nodes;

import org.gertje.abacus.nodevisitors.NodeVisitor;
import org.gertje.abacus.nodevisitors.VisitingException;
import org.gertje.abacus.token.Token;
import org.gertje.abacus.types.Type;

/**
 * Node that represents a factor.
 */
public class FactorNode extends AbstractExpressionNode {

	private ExpressionNode argument;

	/**
	 * Constructor
	 */
	public FactorNode(ExpressionNode argument, Token token) {
		super(0, token);

		this.argument = argument;
	}

	@Override
	public Type getType() {
		// Het type van de factor node is het type van zijn argument.
		return argument.getType();
	}

	@Override
	public boolean getIsConstant() {
		// Deze methode zou op deze node eigenlijk nooit aangeroepen mogen worden, want we kunnen deze node altijd 
		// vereenvoudigen. Maar als dit wel het geval zou zijn zou deze funtie false moeten returnen.
		return false;
	}

	public ExpressionNode getArgument() {
		return argument;
	}

	public void setArgument(ExpressionNode argument) {
		this.argument = argument;
	}

	@Override
	public <R, X extends VisitingException> R accept(NodeVisitor<R, X> visitor) throws X {
		return visitor.visit(this);
	}
}
