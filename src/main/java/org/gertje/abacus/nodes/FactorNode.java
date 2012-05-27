package org.gertje.abacus.nodes;

import org.gertje.abacus.AnalyserException;
import org.gertje.abacus.Token;
import org.gertje.abacus.nodevisitors.NodeVisitorInterface;
import org.gertje.abacus.nodevisitors.VisitingException;
import org.gertje.abacus.symboltable.SymbolTableInterface;

public class FactorNode extends AbstractNode {

	private AbstractNode argument;

	/**
	 * Constructor
	 */
	public FactorNode(AbstractNode argument, Token token, NodeFactoryInterface nodeFactory) {
		super(0, token, nodeFactory);

		this.argument = argument;
	}

	@Override
	public Object evaluate(SymbolTableInterface sym) {
		// Deze methode op deze node zou nooit aangeroepen mogen worden, want dat betekent dat de boom niet geanalyseerd
		// is.
		return argument.evaluate(sym);
	}

	@Override
	public AbstractNode analyse(SymbolTableInterface sym) throws AnalyserException {
		// De meest voor de hand liggende vereenvoudiging van alle nodes, gewoon zijn argument...
		return argument.analyse(sym);
	}

	@Override
	public Class<?> getType() {
		// Het type van de factor node is het type van zijn argument.
		return argument.getType();
	}

	@Override
	public boolean getIsConstant() {
		// Deze methode zou op deze node eigenlijk nooit aangeroepen mogen worden, want we kunnen deze node altijd 
		// vereenvoudigen. Maar als dit wel het geval zou zijn zou deze funtie false moeten returnen.
		return false;
	}

	public AbstractNode getArgument() {
		return argument;
	}

	public void setArgument(AbstractNode argument) {
		this.argument = argument;
	}

	@Override
	public void accept(NodeVisitorInterface visitor) throws VisitingException {
		visitor.visit(this);		
	}
}
