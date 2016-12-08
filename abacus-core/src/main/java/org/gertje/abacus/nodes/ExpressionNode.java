package org.gertje.abacus.nodes;

import org.gertje.abacus.types.Type;

/**
 * Stelt een AST ExpressionNode voor.
 */
public interface ExpressionNode extends Node {

	/**
	 * Geeft het type van de node terug.
	 *
	 * Voordat deze methode aangeroepen kan worden moet eerst de node of de kinderen van de node bezocht zijn om het
	 * type te bepalen.
	 */
	Type getType();

	/**
	 * Geeft terug of de node constant is, dit is het geval wanneer:
	 * - de node niet een expressie is (er zijn geen subnodes)
	 * - EN de node niet een variabele is.
	 *
	 * Voorbeelden van constante nodes zijn (niet uitputtend):
	 * - StringNode
	 * - DecimalNode
	 * - BooleanNode
	 * - DateNode
	 */
	boolean getIsConstant();

	/**
	 * Geeft de precedence terug.
	 */
	int getPrecedence();

}
