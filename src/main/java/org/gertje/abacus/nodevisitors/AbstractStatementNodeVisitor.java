package org.gertje.abacus.nodevisitors;

import org.gertje.abacus.nodes.AddNode;
import org.gertje.abacus.nodes.AndNode;
import org.gertje.abacus.nodes.AssignmentNode;
import org.gertje.abacus.nodes.BooleanNode;
import org.gertje.abacus.nodes.ConcatStringNode;
import org.gertje.abacus.nodes.DateNode;
import org.gertje.abacus.nodes.DecimalNode;
import org.gertje.abacus.nodes.DivideNode;
import org.gertje.abacus.nodes.EqNode;
import org.gertje.abacus.nodes.ExpressionNode;
import org.gertje.abacus.nodes.FactorNode;
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
import org.gertje.abacus.nodes.StringNode;
import org.gertje.abacus.nodes.SubstractNode;
import org.gertje.abacus.nodes.SumNode;
import org.gertje.abacus.nodes.VariableNode;

public abstract class AbstractStatementNodeVisitor<R, X extends VisitingException> implements NodeVisitor<R, X> {

	@Override
	public R visit(AddNode node) throws X {
		throw createIllegalStateException(node);
	}

	@Override
	public R visit(AndNode node) throws X {
		throw createIllegalStateException(node);
	}

	@Override
	public R visit(AssignmentNode node) throws X {
		throw createIllegalStateException(node);
	}

	@Override
	public R visit(BooleanNode node) throws X {
		throw createIllegalStateException(node);
	}

	@Override
	public R visit(ConcatStringNode node) throws X {
		throw createIllegalStateException(node);
	}

	@Override
	public R visit(DateNode node) throws X {
		throw createIllegalStateException(node);
	}

	@Override
	public R visit(DivideNode node) throws X {
		throw createIllegalStateException(node);
	}

	@Override
	public R visit(EqNode node) throws X {
		throw createIllegalStateException(node);
	}

	@Override
	public R visit(FactorNode node) throws X {
		throw createIllegalStateException(node);
	}

	@Override
	public R visit(DecimalNode node) throws X {
		throw createIllegalStateException(node);
	}

	@Override
	public R visit(FunctionNode node) throws X {
		throw createIllegalStateException(node);
	}

	@Override
	public R visit(GeqNode node) throws X {
		throw createIllegalStateException(node);
	}

	@Override
	public R visit(GtNode node) throws X {
		throw createIllegalStateException(node);
	}

	@Override
	public R visit(IfNode node) throws X {
		throw createIllegalStateException(node);
	}

	@Override
	public R visit(IntegerNode node) throws X {
		throw createIllegalStateException(node);
	}

	@Override
	public R visit(LeqNode node) throws X {
		throw createIllegalStateException(node);
	}

	@Override
	public R visit(LtNode node) throws X {
		throw createIllegalStateException(node);
	}

	@Override
	public R visit(ModuloNode node) throws X {
		throw createIllegalStateException(node);
	}

	@Override
	public R visit(MultiplyNode node) throws X {
		throw createIllegalStateException(node);
	}

	@Override
	public R visit(NegativeNode node) throws X {
		throw createIllegalStateException(node);
	}

	@Override
	public R visit(NeqNode node) throws X {
		throw createIllegalStateException(node);
	}

	@Override
	public R visit(NotNode node) throws X {
		throw createIllegalStateException(node);
	}

	@Override
	public R visit(NullNode node) throws X {
		throw createIllegalStateException(node);
	}

	@Override
	public R visit(OrNode node) throws X {
		throw createIllegalStateException(node);
	}

	@Override
	public R visit(PositiveNode node) throws X {
		throw createIllegalStateException(node);
	}

	@Override
	public R visit(PowerNode node) throws X {
		throw createIllegalStateException(node);
	}

	@Override
	public R visit(StringNode node) throws X {
		throw createIllegalStateException(node);
	}

	@Override
	public R visit(SubstractNode node) throws X {
		throw createIllegalStateException(node);
	}

	@Override
	public R visit(SumNode node) throws X {
		throw createIllegalStateException(node);
	}

	@Override
	public R visit(VariableNode node) throws X {
		throw createIllegalStateException(node);
	}

	/**
	 * Creates an illegal state exception with the name of the class of the node.
	 * @param node The node.
	 */
	private static IllegalStateException createIllegalStateException(ExpressionNode node) {
		return new IllegalStateException("Cannot visit '" + node.getClass().getSimpleName() + "' from statement.");
	}
}
