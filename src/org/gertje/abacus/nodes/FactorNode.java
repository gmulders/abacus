package org.gertje.abacus.nodes;

import org.gertje.abacus.AnalyserException;
import org.gertje.abacus.Token;
import org.gertje.abacus.symboltable.SymbolTableInterface;

public class FactorNode extends AbstractNode {

	private AbstractNode argument;

	/**
	 * Constructor
	 */
	public FactorNode(AbstractNode argument, Token token) {
		precedence = 0;

		this.argument = argument;
		this.token = token;
	}

	public Object evaluate(SymbolTableInterface sym) {
		// Deze methode op deze node zou nooit aangeroepen mogen worden, want dat betekend dat de boom niet geanalyseerd
		// is.
		return argument.evaluate(sym);
	}

	public AbstractNode analyse(SymbolTableInterface sym) throws AnalyserException {
		// De meest voor de hand liggende vereenvoudiging van alle nodes, gewoon zijn argument...
		return argument.analyse(sym);
	}

	public String generateJavascript(SymbolTableInterface sym) {
		return "(" + argument.generateJavascript(sym) + ")";
	}

	public Class<?> getType() {
		// Het type van de factor node is het type van zijn argument.
		return argument.getType();
	}

	public boolean getIsConstant() {
		// Deze methode zou op deze node eigenlijk nooit aangeroepen mogen worden, want we kunnen deze node altijd 
		// vereenvoudigen. Maar als dit wel het geval zou zijn zou deze funtie false moeten returnen.
		return false;
	}
}
