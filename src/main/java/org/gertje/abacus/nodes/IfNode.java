package org.gertje.abacus.nodes;

import org.gertje.abacus.AnalyserException;
import org.gertje.abacus.EvaluationException;
import org.gertje.abacus.Token;
import org.gertje.abacus.nodevisitors.NodeVisitor;
import org.gertje.abacus.nodevisitors.VisitingException;
import org.gertje.abacus.symboltable.SymbolTableInterface;

public class IfNode extends AbstractNode {

	private AbstractNode condition;
	private AbstractNode ifBody;
	private AbstractNode elseBody;

	/**
	 * Constructor
	 */
	public IfNode(AbstractNode condition, AbstractNode ifBody, AbstractNode elseBody, Token token, 
			NodeFactoryInterface nodeFactory) {
		super(10, token, nodeFactory);

		this.condition = condition;
		this.ifBody = ifBody;
		this.elseBody = elseBody;
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
			return ifBody.evaluate(sym);
		}

		// We moeten de else body geevaluateueerd teruggeven.
		return elseBody.evaluate(sym);
	}

	@Override
	public AbstractNode analyse(SymbolTableInterface sym) throws AnalyserException {
		// Probeer de nodes zoveel mogelijk te vereenvoudigen.
		condition = condition.analyse(sym);
		ifBody = ifBody.analyse(sym);
		elseBody = elseBody.analyse(sym);

		// De waarde van de conditie moet van het type 'boolean' zijn.
		if (!condition.getType().equals(Boolean.class)) {
			throw new AnalyserException("Expected boolean parameter to IF-expression.", token);
		}

		// De waardes van beide bodies moeten van het zelfde type zijn of een van beide mag null zijn.
		if (ifBody.getType() != elseBody.getType() 
				&& !ifBody.getType().equals(Object.class) 
				&& !elseBody.getType().equals(Object.class)) {
			throw new AnalyserException("IF-body and ELSE-body should have the same type.", token);
		}

		// De waardes van de bodies mogen niet allebei null zijn.
		if (ifBody.getType().equals(Object.class) && elseBody.getType().equals(Object.class)) {
			throw new AnalyserException("IF-body and ELSE-body should not be both null.", token);
		}
		
		// Wanneer de conditie constant is geven we afhankelijk van de waarde een body terug.
		if (condition.getIsConstant()) {
			try {
				if (Boolean.TRUE.equals(condition.evaluate(sym))) {
					return ifBody;
				} else {
					return elseBody;
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
		return ifBody.getType();
	}

	@Override
	public boolean getIsConstant() {
		return false;
	}

	@Override
	public <R, X extends VisitingException> R accept(NodeVisitor<R, X> visitor) throws X {
		return visitor.visit(this);
	}

	public AbstractNode getCondition() {
		return condition;
	}

	public void setCondition(AbstractNode condition) {
		this.condition = condition;
	}

	public AbstractNode getIfBody() {
		return ifBody;
	}

	public void setIfBody(AbstractNode ifBody) {
		this.ifBody = ifBody;
	}

	public AbstractNode getElseBody() {
		return elseBody;
	}

	public void setElseBody(AbstractNode elseBody) {
		this.elseBody = elseBody;
	}
}
