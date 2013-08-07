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

public class Interpreter extends AbstractNodeVisitor<Object, InterpreterException> {

	/**
	 * De symboltable met de variabelen en de functies.
	 */
	private SymbolTable symbolTable;

	/**
	 * Constructor.
	 */
	public Interpreter(SymbolTable symbolTable) {
		this.symbolTable = symbolTable;
	}

	public Object evaluate(AbstractNode node) throws InterpreterException {
		return node.accept(this);
	}

	@Override
	public Object visit(AddNode node) throws InterpreterException {
		return null;
	}

	@Override
	public Object visit(AndNode node) throws InterpreterException {
		return null;
	}

	@Override
	public Object visit(AssignmentNode node) throws InterpreterException {
		return null;
	}

	@Override
	public Object visit(BooleanNode node) throws InterpreterException {
		return null;
	}

	@Override
	public Object visit(DateNode node) throws InterpreterException {
		return null;
	}

	@Override
	public Object visit(DivideNode node) throws InterpreterException {
		return null;
	}

	@Override
	public Object visit(EqNode node) throws InterpreterException {
		return null;
	}

	@Override
	public Object visit(FactorNode node) throws InterpreterException {
		return null;
	}

	@Override
	public Object visit(FloatNode node) throws InterpreterException {
		return null;
	}

	@Override
	public Object visit(FunctionNode node) throws InterpreterException {
		return null;
	}

	@Override
	public Object visit(GeqNode node) throws InterpreterException {
		return null;
	}

	@Override
	public Object visit(GtNode node) throws InterpreterException {
		return null;
	}

	@Override
	public Object visit(IfNode node) throws InterpreterException {
		return null;
	}

	@Override
	public Object visit(IntegerNode node) throws InterpreterException {
		return null;
	}

	@Override
	public Object visit(LeqNode node) throws InterpreterException {
		return null;
	}

	@Override
	public Object visit(LtNode node) throws InterpreterException {
		return null;
	}

	@Override
	public Object visit(ModuloNode node) throws InterpreterException {
		return null;
	}

	@Override
	public Object visit(MultiplyNode node) throws InterpreterException {
		return null;
	}

	@Override
	public Object visit(NegativeNode node) throws InterpreterException {
		return null;
	}

	@Override
	public Object visit(NeqNode node) throws InterpreterException {
		return null;
	}

	@Override
	public Object visit(NotNode node) throws InterpreterException {
		return null;
	}

	@Override
	public Object visit(NullNode node) throws InterpreterException {
		return null;
	}

	@Override
	public Object visit(OrNode node) throws InterpreterException {
		return null;
	}

	@Override
	public Object visit(PositiveNode node) throws InterpreterException {
		return null;
	}

	@Override
	public Object visit(PowerNode node) throws InterpreterException {
		return null;
	}

	@Override
	public Object visit(StatementListNode node) throws InterpreterException {
		return null;
	}

	@Override
	public Object visit(StringNode node) throws InterpreterException {
		return null;
	}

	@Override
	public Object visit(SubstractNode node) throws InterpreterException {
		return null;
	}

	@Override
	public Object visit(VariableNode node) throws InterpreterException {
		return null;
	}
}
