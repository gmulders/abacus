package org.gertje.abacus.nodes;

import org.gertje.abacus.AnalyserException;
import org.gertje.abacus.EvaluationException;
import org.gertje.abacus.Token;
import org.gertje.abacus.nodevisitors.NodeVisitor;
import org.gertje.abacus.nodevisitors.VisitingException;
import org.gertje.abacus.symboltable.SymbolTable;

public class AndNode extends AbstractNode {

	private AbstractNode lhs;
	private AbstractNode rhs;

	/**
	 * Constructor
	 */
	public AndNode(AbstractNode lhs, AbstractNode rhs, Token token, NodeFactory nodeFactory) {
		super(8, token, nodeFactory);

		this.lhs = lhs;
		this.rhs = rhs;
	}

	@Override
	public Boolean evaluate(SymbolTable sym) throws EvaluationException {
		Boolean left = (Boolean) lhs.evaluate(sym);
		Boolean right = (Boolean) rhs.evaluate(sym);

		if (left == null || right == null) {
			return null;
		}
		
		return Boolean.valueOf(left.booleanValue() && right.booleanValue());
	}

	/**
	 * Controleert of de node correct is van syntax.
	 */
	private boolean checkTypes() {
		return lhs.getType().equals(Boolean.class) && rhs.getType().equals(Boolean.class);
	}

	@Override
	public AbstractNode analyse(SymbolTable sym) throws AnalyserException {
		// Vereenvoudig de nodes indien mogelijk.
		lhs = lhs.analyse(sym);
		rhs = rhs.analyse(sym);

		// Beide zijden moeten van het type 'boolean' zijn.
		if (!checkTypes()) {
			throw new AnalyserException("Expected two boolean parameters to OR-expression.", token);
		}

		try {
			// Wanneer beide zijden constant zijn kunnen we de node vereenvoudigen.
			if (lhs.getIsConstant() && rhs.getIsConstant()) {
				return nodeFactory.createBooleanNode(evaluate(sym), token);
			}

			// Wanneer slechts de linker zijde of de rechter zijde constant is en deze naar 'false' evalueert, evalueert de
			// hele expressie naar false en kunnen we de node vereenvoudigen.
			if (lhs.getIsConstant() && !((Boolean)lhs.evaluate(sym)).booleanValue()
					|| rhs.getIsConstant() && !((Boolean)rhs.evaluate(sym)).booleanValue()) {
				return nodeFactory.createBooleanNode(Boolean.FALSE, token);
			}

			// Wanneer slechts de linker zijde of de rechter zijde constant is en deze naar 'true' evalueert, evalueert de
			// huidige expressie naar de niet constante expressie.
			if (lhs.getIsConstant() && ((Boolean)lhs.evaluate(sym)).booleanValue()) {
				return rhs;
			} else if (rhs.getIsConstant() && ((Boolean)rhs.evaluate(sym)).booleanValue()) {
				return lhs;
			}
		} catch (EvaluationException e) {
			throw new AnalyserException(e.getMessage(), token);
		}
		// Geef de huidige instantie terug.
		return this;
	}

	@Override
	public Class<?> getType() {
		return Boolean.class;
	}

	@Override
	public boolean getIsConstant() {
		return false;
	}

	@Override
	public <R, X extends VisitingException> R accept(NodeVisitor<R, X> visitor) throws X {
		return visitor.visit(this);
	}

	public AbstractNode getLhs() {
		return lhs;
	}

	public void setLhs(AbstractNode lhs) {
		this.lhs = lhs;
	}

	public AbstractNode getRhs() {
		return rhs;
	}

	public void setRhs(AbstractNode rhs) {
		this.rhs = rhs;
	}
}
