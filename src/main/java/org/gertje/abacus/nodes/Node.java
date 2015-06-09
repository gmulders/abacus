package org.gertje.abacus.nodes;

import org.gertje.abacus.token.Token;
import org.gertje.abacus.nodevisitors.NodeVisitor;
import org.gertje.abacus.nodevisitors.VisitingException;

/**
 * Stelt een AST Node voor.
 */
public interface Node {
	/**
	 * Geeft het type van de node terug.
	 */
	Class<?> getType();

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
	boolean getIsConstant();

	/**
	 * Geeft de precedence terug.
	 */
	int getPrecedence();

	/**
	 * Registreert een visitor bij de node.
	 *
	 * De bedoeling is dat een node deze methode ook aanroept bij zijn child-nodes.
	 *
	 * @param visitor De visitor.
	 * @throws X
	 */
	<R, X extends VisitingException> R accept(NodeVisitor<R, X> visitor) throws X;

	/**
	 * Geeft het token terug.
	 * @return het token.
	 */
	Token getToken();
}
