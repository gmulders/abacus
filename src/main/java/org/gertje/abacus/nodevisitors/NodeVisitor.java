package org.gertje.abacus.nodevisitors;

import org.gertje.abacus.nodes.AddNode;
import org.gertje.abacus.nodes.AndNode;
import org.gertje.abacus.nodes.AssignmentNode;
import org.gertje.abacus.nodes.BooleanNode;
import org.gertje.abacus.nodes.ConcatStringNode;
import org.gertje.abacus.nodes.DateNode;
import org.gertje.abacus.nodes.DivideNode;
import org.gertje.abacus.nodes.EqNode;
import org.gertje.abacus.nodes.FactorNode;
import org.gertje.abacus.nodes.DecimalNode;
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
import org.gertje.abacus.nodes.RootNode;
import org.gertje.abacus.nodes.StatementListNode;
import org.gertje.abacus.nodes.StringNode;
import org.gertje.abacus.nodes.SubtractNode;
import org.gertje.abacus.nodes.SumNode;
import org.gertje.abacus.nodes.VariableNode;

public interface NodeVisitor<R, X extends VisitingException> {
	R visit(AddNode node) throws X;
	R visit(AndNode node) throws X;
	R visit(AssignmentNode node) throws X;
	R visit(BooleanNode node) throws X;
	R visit(ConcatStringNode node) throws X;
	R visit(DateNode node) throws X;
	R visit(DecimalNode node) throws X;
	R visit(DivideNode node) throws X;
	R visit(EqNode node) throws X;
	R visit(FactorNode node) throws X;
	R visit(FunctionNode node) throws X;
	R visit(GeqNode node) throws X;
	R visit(GtNode node) throws X;
	R visit(IfNode node) throws X;
	R visit(IntegerNode node) throws X;
	R visit(LeqNode node) throws X;
	R visit(LtNode node) throws X;
	R visit(ModuloNode node) throws X;
	R visit(MultiplyNode node) throws X;
	R visit(NegativeNode node) throws X;
	R visit(NeqNode node) throws X;
	R visit(NotNode node) throws X;
	R visit(NullNode node) throws X;
	R visit(OrNode node) throws X;
	R visit(PositiveNode node) throws X;
	R visit(PowerNode node) throws X;
	R visit(RootNode node) throws X;
	R visit(StatementListNode node) throws X;
	R visit(StringNode node) throws X;
	R visit(SubtractNode node) throws X;
	R visit(SumNode node) throws X;
	R visit(VariableNode node) throws X;
}
