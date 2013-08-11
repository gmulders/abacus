package org.gertje.abacus.nodes;

import org.gertje.abacus.Token;
import org.gertje.abacus.nodevisitors.NodeVisitor;
import org.gertje.abacus.nodevisitors.VisitingException;

/**
 * Deze klasse stelt een node in een AbstractSyntaxTree voor.
 */
abstract public class AbstractNode {

	/**
	 * Getal wat de volgorde van uitvoering van operatoren aangeeft. Voor het geval operatoren niet commuteren.
	 */
	protected int precedence;

	/**
	 * Bevat het token waaruit deze node is ontstaan.
	 */
	protected Token token;

	/**
	 * Bevat de factory die de node moet gebruiken wanneer deze nieuwe nodes aanmaakt.
	 */
	protected NodeFactory nodeFactory;

	/**
	 * Contructor.
	 * 
	 * @param precedence Getal wat de volgorde van uitvoering van operatoren aangeeft. Voor het geval operatoren niet 
	 * commuteren.
	 * @param token Bevat het token waaruit deze node is ontstaan.
	 * @param nodeFactory Bevat de factory die de node moet gebruiken wanneer deze nieuwe nodes aanmaakt.
	 */
	public AbstractNode(int precedence, Token token, NodeFactory nodeFactory) {
		this.precedence = precedence;
		this.token = token;
		this.nodeFactory = nodeFactory;
	}

	/**
	 * Geeft het type van de node terug.
	 */
	abstract public Class<?> getType();

	/**
	 * Geeft terug of de node constant is, dit is het geval wanneer:
	 * - de node niet een expressie is (er zijn geen subnodes)
	 * - EN de node niet een variabele is.
	 *
	 * Voorbeelden van constante nodes zijn (niet uitputtend):
	 * - StringNode
	 * - NumberNode
	 * - BooleanNode
	 * - DateNode
	 */
	abstract public boolean getIsConstant();

	/**
	 * Geeft de precedence terug.
	 */
	public int getPrecedence() {
		return precedence;
	}

	/**
	 * Registreert een visitor bij de node. 
	 * 
	 * De bedoeling is dat een node deze methode ook aanroept bij zijn child-nodes.
	 * 
	 * @param visitor De visitor.
	 * @throws VisitingException 
	 */
    abstract public <R, X extends VisitingException> R accept(NodeVisitor<R, X> visitor) throws X;

	/**
	 * Geeft het token terug.
	 * @return het token.
	 */
	public Token getToken() {
		return token;
	}
}
