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
import org.gertje.abacus.nodes.Node;
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

public abstract class DefaultVisitor<R, X extends VisitingException> implements NodeVisitor<R, X> {

	@Override
	public R visit(AddNode node) throws X {
		node.getLhs().accept(this);
		node.getRhs().accept(this);
		return null;
	}

	@Override
	public R visit(AndNode node) throws X {
		node.getLhs().accept(this);
		node.getRhs().accept(this);
		return null;
	}

	@Override
	public R visit(AssignmentNode node) throws X {
		node.getLhs().accept(this);
		node.getRhs().accept(this);
		return null;
	}

	@Override
	public R visit(BooleanNode node) throws X {
		return null;
	}

	@Override
	public R visit(ConcatStringNode node) throws X {
		node.getLhs().accept(this);
		node.getRhs().accept(this);
		return null;
	}

	@Override
	public R visit(DateNode node) throws X {
		return null;
	}

	@Override
	public R visit(DecimalNode node) throws X {
		return null;
	}

	@Override
	public R visit(DivideNode node) throws X {
		node.getLhs().accept(this);
		node.getRhs().accept(this);
		return null;
	}

	@Override
	public R visit(EqNode node) throws X {
		node.getLhs().accept(this);
		node.getRhs().accept(this);
		return null;
	}

	@Override
	public R visit(FactorNode node) throws X {
		node.getArgument().accept(this);
		return null;
	}

	@Override
	public R visit(FunctionNode node) throws X {
		for (ExpressionNode parameter : node.getParameters()) {
			parameter.accept(this);
		}
		return null;
	}

	@Override
	public R visit(GeqNode node) throws X {
		node.getLhs().accept(this);
		node.getRhs().accept(this);
		return null;
	}

	@Override
	public R visit(GtNode node) throws X {
		node.getLhs().accept(this);
		node.getRhs().accept(this);
		return null;
	}

	@Override
	public R visit(IfNode node) throws X {
		node.getCondition().accept(this);
		node.getIfBody().accept(this);
		node.getElseBody().accept(this);
		return null;
	}

	@Override
	public R visit(IntegerNode node) throws X {
		return null;
	}

	@Override
	public R visit(LeqNode node) throws X {
		node.getLhs().accept(this);
		node.getRhs().accept(this);
		return null;
	}

	@Override
	public R visit(LtNode node) throws X {
		node.getLhs().accept(this);
		node.getRhs().accept(this);
		return null;
	}

	@Override
	public R visit(ModuloNode node) throws X {
		node.getLhs().accept(this);
		node.getRhs().accept(this);
		return null;
	}

	@Override
	public R visit(MultiplyNode node) throws X {
		node.getLhs().accept(this);
		node.getRhs().accept(this);
		return null;
	}

	@Override
	public R visit(NegativeNode node) throws X {
		node.getArgument().accept(this);
		return null;
	}

	@Override
	public R visit(NeqNode node) throws X {
		node.getLhs().accept(this);
		node.getRhs().accept(this);
		return null;
	}

	@Override
	public R visit(NotNode node) throws X {
		node.getArgument().accept(this);
		return null;
	}

	@Override
	public R visit(NullNode node) throws X {
		return null;
	}

	@Override
	public R visit(OrNode node) throws X {
		node.getLhs().accept(this);
		node.getRhs().accept(this);
		return null;
	}

	@Override
	public R visit(PositiveNode node) throws X {
		node.getArgument().accept(this);
		return null;
	}

	@Override
	public R visit(PowerNode node) throws X {
		node.getLhs().accept(this);
		node.getRhs().accept(this);
		return null;
	}

	@Override
	public R visit(RootNode node) throws X {
		node.getStatementListNode().accept(this);
		return null;
	}

	@Override
	public R visit(StatementListNode node) throws X {
		for (Node subNode : node) {
			subNode.accept(this);
		}
		return null;
	}

	@Override
	public R visit(StringNode node) throws X {
		return null;
	}

	@Override
	public R visit(SubtractNode node) throws X {
		node.getLhs().accept(this);
		node.getRhs().accept(this);
		return null;
	}

	@Override
	public R visit(SumNode node) throws X {
		node.getLhs().accept(this);
		node.getRhs().accept(this);
		return null;
	}

	@Override
	public R visit(VariableNode node) throws X {
		return null;
	}
}
