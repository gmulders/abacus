package org.gertje.abacus.nodes;

import java.math.BigDecimal;

import org.gertje.abacus.Token;
import org.gertje.abacus.nodevisitors.NodeVisitor;
import org.gertje.abacus.nodevisitors.VisitingException;
import org.gertje.abacus.symboltable.SymbolTableInterface;

public class FloatNode extends AbstractNode {

	private BigDecimal value;

	/**
	 * Constructor
	 */
	public FloatNode(BigDecimal value, Token token, NodeFactoryInterface nodeFactory) {
		super(1, token, nodeFactory);

		this.value = value;
	}

	@Override
	public BigDecimal evaluate(SymbolTableInterface sym) {
		return value;
	}

	@Override
	public AbstractNode analyse(SymbolTableInterface sym) {
		// Deze node kunnen we niet eenvoudiger maken. Geef de huidige instantie terug.	
		return this;
	}

	@Override
	public Class<?> getType() {
		return BigDecimal.class;
	}

	@Override
	public boolean getIsConstant() {
		return true;
	}

	@Override
	public <R, X extends VisitingException> R accept(NodeVisitor<R, X> visitor) throws X {
		return visitor.visit(this);
	}

	public BigDecimal getValue() {
		return value;
	}

	public void setValue(BigDecimal value) {
		this.value = value;
	}
}
