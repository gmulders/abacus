package org.gertje.abacus.nodevisitors;

import org.gertje.abacus.nodes.AbstractNode;
import org.gertje.abacus.nodes.AddNode;
import org.gertje.abacus.nodes.AndNode;
import org.gertje.abacus.nodes.AssignmentNode;
import org.gertje.abacus.nodes.BooleanNode;
import org.gertje.abacus.nodes.DateNode;
import org.gertje.abacus.nodes.DivideNode;
import org.gertje.abacus.nodes.EqNode;
import org.gertje.abacus.nodes.FactorNode;
import org.gertje.abacus.nodes.FloatNode;
import org.gertje.abacus.nodes.FunctionNode;
import org.gertje.abacus.nodes.GeqNode;
import org.gertje.abacus.nodes.GtNode;
import org.gertje.abacus.nodes.IfNode;
import org.gertje.abacus.nodes.IntegerNode;
import org.gertje.abacus.nodes.LeqNode;
import org.gertje.abacus.nodes.LtNode;
import org.gertje.abacus.nodes.ModuloNode;
import org.gertje.abacus.nodes.MultiplyNode;
import org.gertje.abacus.nodes.NegativeNode;
import org.gertje.abacus.nodes.NeqNode;
import org.gertje.abacus.nodes.NotNode;
import org.gertje.abacus.nodes.NullNode;
import org.gertje.abacus.nodes.OrNode;
import org.gertje.abacus.nodes.PositiveNode;
import org.gertje.abacus.nodes.PowerNode;
import org.gertje.abacus.nodes.StatementListNode;
import org.gertje.abacus.nodes.StringNode;
import org.gertje.abacus.nodes.SubstractNode;
import org.gertje.abacus.nodes.VariableNode;
import org.gertje.abacus.symboltable.SymbolTable;

public class Simplifier extends AbstractNodeVisitor<AbstractNode, SimplificationException> {

	/**
	 * De symboltable met de variabelen en de functies.
	 */
	private SymbolTable symbolTable;

	/**
	 * Constructor.
	 */
	public Simplifier(SymbolTable symbolTable) {
		this.symbolTable = symbolTable;
	}

	public AbstractNode simplify(AbstractNode node) throws SimplificationException {
		return node.accept(this);
	}

	@Override
	public AbstractNode visit(AddNode node) throws SimplificationException {
		return null;
	}

	@Override
	public AbstractNode visit(AndNode node) throws SimplificationException {
		return null;
	}

	@Override
	public AbstractNode visit(AssignmentNode node) throws SimplificationException {
		return null;
	}

	@Override
	public AbstractNode visit(BooleanNode node) throws SimplificationException {
		return null;
	}

	@Override
	public AbstractNode visit(DateNode node) throws SimplificationException {
		return null;
	}

	@Override
	public AbstractNode visit(DivideNode node) throws SimplificationException {
		return null;
	}

	@Override
	public AbstractNode visit(EqNode node) throws SimplificationException {
		return null;
	}

	@Override
	public AbstractNode visit(FactorNode node) throws SimplificationException {
		return null;
	}

	@Override
	public AbstractNode visit(FloatNode node) throws SimplificationException {
		return null;
	}

	@Override
	public AbstractNode visit(FunctionNode node) throws SimplificationException {
		return null;
	}

	@Override
	public AbstractNode visit(GeqNode node) throws SimplificationException {
		return null;
	}

	@Override
	public AbstractNode visit(GtNode node) throws SimplificationException {
		return null;
	}

	@Override
	public AbstractNode visit(IfNode node) throws SimplificationException {
		return null;
	}

	@Override
	public AbstractNode visit(IntegerNode node) throws SimplificationException {
		return null;
	}

	@Override
	public AbstractNode visit(LeqNode node) throws SimplificationException {
		return null;
	}

	@Override
	public AbstractNode visit(LtNode node) throws SimplificationException {
		return null;
	}

	@Override
	public AbstractNode visit(ModuloNode node) throws SimplificationException {
		return null;
	}

	@Override
	public AbstractNode visit(MultiplyNode node) throws SimplificationException {
		return null;
	}

	@Override
	public AbstractNode visit(NegativeNode node) throws SimplificationException {
		return null;
	}

	@Override
	public AbstractNode visit(NeqNode node) throws SimplificationException {
		return null;
	}

	@Override
	public AbstractNode visit(NotNode node) throws SimplificationException {
		return null;
	}

	@Override
	public AbstractNode visit(NullNode node) throws SimplificationException {
		return null;
	}

	@Override
	public AbstractNode visit(OrNode node) throws SimplificationException {
		return null;
	}

	@Override
	public AbstractNode visit(PositiveNode node) throws SimplificationException {
		return null;
	}

	@Override
	public AbstractNode visit(PowerNode node) throws SimplificationException {
		return null;
	}

	@Override
	public AbstractNode visit(StatementListNode node) throws SimplificationException {
		return null;
	}

	@Override
	public AbstractNode visit(StringNode node) throws SimplificationException {
		return null;
	}

	@Override
	public AbstractNode visit(SubstractNode node) throws SimplificationException {
		return null;
	}

	@Override
	public AbstractNode visit(VariableNode node) throws SimplificationException {
		return null;
	}
}
