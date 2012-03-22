package org.gertje.abacus.nodes;

import java.math.BigDecimal;
import java.math.BigInteger;

import org.gertje.abacus.AnalyserException;
import org.gertje.abacus.Token;
import org.gertje.abacus.symboltable.SymbolTableInterface;

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
	protected NodeFactoryInterface nodeFactory;

	/**
	 * Contructor.
	 * 
	 * @param precedence Getal wat de volgorde van uitvoering van operatoren aangeeft. Voor het geval operatoren niet 
	 * commuteren.
	 * @param token Bevat het token waaruit deze node is ontstaan.
	 * @param nodeFactory Bevat de factory die de node moet gebruiken wanneer deze nieuwe nodes aanmaakt.
	 */
	public AbstractNode(int precedence, Token token, NodeFactoryInterface nodeFactory) {
		this.precedence = precedence;
		this.token = token;
		this.nodeFactory = nodeFactory;
	}

	/**
	 * Evalueert de node, moet per node overschreven worden.
	 */
	abstract public Object evaluate(SymbolTableInterface sym);

	/**
	 * Analyseert de node, dit betekent:
	 * - controleert de node op correctheid
	 * - vereenvoudigd de node indien mogelijk.
	 * @throws AnalyserException
	 */
	abstract public AbstractNode analyse(SymbolTableInterface sym) throws AnalyserException;

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
	 * Genereert een stuk javascript dat deze node voorstelt.
	 */
	abstract public String generateJavascript(SymbolTableInterface sym);

	/**
	 * Maakt een stuk javascript voor een gegeven node.
	 */
	public String generateJavascriptNodePart(SymbolTableInterface sym, AbstractNode node) {
		String part = node.generateJavascript(sym);
		// Wanneer deze node een lagere prio heeft dan de node onder hem moeten we haakjes toevoegen.
		if (precedence < node.getPrecedence()) {
			part = "(" + part + ")";
		}
		return part;
	}

	/**
	 * Bepaalt of het meegegeven type een nummer is.
	 * @param type Het type waarvan de methode bepaalt of het een nummer is.
	 * @return <code>true</code> wanneer het meegegeven type een nummer is, anders <code>false</code>.
	 */
	protected boolean isNumber(Class<?> type) {
		return BigDecimal.class.equals(type) || BigInteger.class.equals(type);
	}
}
