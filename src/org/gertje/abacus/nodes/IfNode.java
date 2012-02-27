package org.gertje.abacus.nodes;

import org.gertje.abacus.AnalyserException;
import org.gertje.abacus.Token;
import org.gertje.abacus.symboltable.SymbolTableInterface;

public class IfNode extends AbstractNode {

	private AbstractNode condition;
	private AbstractNode ifbody;
	private AbstractNode elsebody;

	/**
	 * Constructor
	 */
	public IfNode(AbstractNode condition, AbstractNode ifbody, AbstractNode elsebody, Token token, 
			NodeFactoryInterface nodeFactory) {
		super(10, token, nodeFactory);

		this.condition = condition;
		this.ifbody = ifbody;
		this.elsebody = elsebody;
	}

	public Object evaluate(SymbolTableInterface sym) {
		// Wanneer de conditie waar is geven we de geevalueerde if body terug, anders geven we de else body terug.
		if (Boolean.TRUE.equals(condition.evaluate(sym))) {
			return ifbody.evaluate(sym);
		}

		// We moeten de else body geevaluateueerd teruggeven.
		return elsebody.evaluate(sym);
	}

	public AbstractNode analyse(SymbolTableInterface sym) throws AnalyserException {
		// Probeer de nodes zoveel mogelijk te vereenvoudigen.
		condition = condition.analyse(sym);
		ifbody = ifbody.analyse(sym);
		elsebody = elsebody.analyse(sym);

		// De waarde van de conditie moet van het type 'boolean' zijn.
		if (!condition.getType().equals(Boolean.class)) {
			throw new AnalyserException("Expected boolean parameter to IF-expression.", token);
		}

		// De waardes van beide bodies moeten van het zelfde type zijn.
		if (ifbody.getType() != elsebody.getType()) {
			throw new AnalyserException("IF-body and ELSE-body should have the same type.", token);
		}

		// Wanneer de conditie constant is geven we afhankelijk van de waarde een body terug.
		if (condition.getIsConstant()) {
			if (Boolean.TRUE.equals(condition.evaluate(sym))) {
				return ifbody;
			} else {
				return elsebody;
			}
		}

		// We kunnen niets vereenvoudigen, geef de huidige instantie terug.
		return this;
	}

	public String generateJavascript(SymbolTableInterface sym) {
		return generateJavascriptNodePart(sym, condition) + " ? " 
				+ generateJavascriptNodePart(sym, ifbody) + " : "
				+ generateJavascriptNodePart(sym, elsebody);
	}

	public Class<?> getType() {
		return ifbody.getType();
	}

	public boolean getIsConstant() {
		return false;
	}
}
