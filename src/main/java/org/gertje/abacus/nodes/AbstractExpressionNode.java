package org.gertje.abacus.nodes;

import org.gertje.abacus.token.Token;

/**
 * Deze klasse stelt een node in een AbstractSyntaxTree voor.
 */
abstract public class AbstractExpressionNode extends AbstractNode implements ExpressionNode {

	/**
	 * Getal wat de volgorde van uitvoering van operatoren aangeeft. Voor het geval operatoren niet commuteren.
	 */
	protected int precedence;

	/**
	 * Contructor.
	 * 
	 * @param precedence Getal wat de volgorde van uitvoering van operatoren aangeeft. Voor het geval operatoren niet 
	 * commuteren.
	 * @param token Bevat het token waaruit deze node is ontstaan.
	 */
	public AbstractExpressionNode(int precedence, Token token) {
		super(token);
		this.precedence = precedence;
	}

	@Override
	public int getPrecedence() {
		return precedence;
	}

	@Override
	public NodeType getNodeType() {
		return NodeType.EXPRESSION;
	}
}
