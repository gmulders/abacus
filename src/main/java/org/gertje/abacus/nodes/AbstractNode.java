package org.gertje.abacus.nodes;

import org.gertje.abacus.Token;

/**
 * Deze klasse stelt een node in een AbstractSyntaxTree voor.
 */
abstract public class AbstractNode implements Node {

	/**
	 * Getal wat de volgorde van uitvoering van operatoren aangeeft. Voor het geval operatoren niet commuteren.
	 */
	protected int precedence;

	/**
	 * Bevat het token waaruit deze node is ontstaan.
	 */
	protected Token token;

	/**
	 * Contructor.
	 * 
	 * @param precedence Getal wat de volgorde van uitvoering van operatoren aangeeft. Voor het geval operatoren niet 
	 * commuteren.
	 * @param token Bevat het token waaruit deze node is ontstaan.
	 */
	public AbstractNode(int precedence, Token token) {
		this.precedence = precedence;
		this.token = token;
	}

	@Override
	public int getPrecedence() {
		return precedence;
	}

	@Override
	public Token getToken() {
		return token;
	}
}
