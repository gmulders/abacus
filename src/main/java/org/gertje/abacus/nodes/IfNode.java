package org.gertje.abacus.nodes;

import org.gertje.abacus.AnalyserException;
import org.gertje.abacus.EvaluationException;
import org.gertje.abacus.Token;
import org.gertje.abacus.nodevisitors.NodeVisitorInterface;
import org.gertje.abacus.nodevisitors.VisitingException;
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

	@Override
	public Object evaluate(SymbolTableInterface sym) throws EvaluationException {
		// Evauleer de conditie.
		Boolean cond = (Boolean)condition.evaluate(sym);
		
		// Wanneer de conditie leeg is kunnen we ook geen uitspraak doen over het resultaat, geef dus null terug.
		if (cond == null) {
			return null;
		}

		// Wanneer de conditie waar is geven we de geevalueerde if body terug, anders geven we de else body terug.
		if (Boolean.TRUE.equals(cond)) {
			return ifbody.evaluate(sym);
		}

		// We moeten de else body geevaluateueerd teruggeven.
		return elsebody.evaluate(sym);
	}

	@Override
	public AbstractNode analyse(SymbolTableInterface sym) throws AnalyserException {
		// Probeer de nodes zoveel mogelijk te vereenvoudigen.
		condition = condition.analyse(sym);
		ifbody = ifbody.analyse(sym);
		elsebody = elsebody.analyse(sym);

		// De waarde van de conditie moet van het type 'boolean' zijn.
		if (!condition.getType().equals(Boolean.class)) {
			throw new AnalyserException("Expected boolean parameter to IF-expression.", token);
		}

		// De waardes van beide bodies moeten van het zelfde type zijn of een van beide mag null zijn.
		if (ifbody.getType() != elsebody.getType() 
				&& !ifbody.getType().equals(Object.class) 
				&& !elsebody.getType().equals(Object.class)) {
			throw new AnalyserException("IF-body and ELSE-body should have the same type.", token);
		}

		// De waardes van de bodies mogen niet allebei null zijn.
		if (ifbody.getType().equals(Object.class) && elsebody.getType().equals(Object.class)) {
			throw new AnalyserException("IF-body and ELSE-body should not be both null.", token);
		}
		
		// Wanneer de conditie constant is geven we afhankelijk van de waarde een body terug.
		if (condition.getIsConstant()) {
			try {
				if (Boolean.TRUE.equals(condition.evaluate(sym))) {
					return ifbody;
				} else {
					return elsebody;
				}
			} catch (EvaluationException e) {
				throw new AnalyserException(e.getMessage(), token);
			}
		}

		// We kunnen niets vereenvoudigen, geef de huidige instantie terug.
		return this;
	}

	@Override
	public Class<?> getType() {
		return ifbody.getType();
	}

	@Override
	public boolean getIsConstant() {
		return false;
	}

	@Override
	public void accept(NodeVisitorInterface visitor) throws VisitingException {
		visitor.visit(this);		
	}

	public AbstractNode getCondition() {
		return condition;
	}

	public void setCondition(AbstractNode condition) {
		this.condition = condition;
	}

	public AbstractNode getIfbody() {
		return ifbody;
	}

	public void setIfbody(AbstractNode ifbody) {
		this.ifbody = ifbody;
	}

	public AbstractNode getElsebody() {
		return elsebody;
	}

	public void setElsebody(AbstractNode elsebody) {
		this.elsebody = elsebody;
	}
}
