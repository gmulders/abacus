package org.gertje.abacus.nodevisitors;

import org.gertje.abacus.nodes.AddNode;
import org.gertje.abacus.nodes.AndNode;
import org.gertje.abacus.nodes.AssignmentNode;
import org.gertje.abacus.nodes.BinaryOperationNode;
import org.gertje.abacus.nodes.BooleanNode;
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
import org.gertje.abacus.nodes.SubstractNode;
import org.gertje.abacus.nodes.VariableNode;

import java.util.Iterator;

/**
 * Prints the expression.
 */
public class PrettyPrinter implements NodeVisitor<String, VisitingException> {

	/**
	 * Prints the given AST.
	 * @param node The AST to print.
	 * @return A String representing the node.
	 * @throws VisitingException
	 */
	public static String print(Node node) throws VisitingException {
		return node.accept(new PrettyPrinter());
	}

	@Override
	public String visit(AddNode node) throws VisitingException {
		return createScriptForBinaryOperationNode(node, "+");
	}

	@Override
	public String visit(AndNode node) throws VisitingException {
		return createScriptForBinaryOperationNode(node, "&&");
	}

	@Override
	public String visit(AssignmentNode node) throws VisitingException {
		return node.getLhs().accept(this) + " = " + node.getRhs().accept(this);
	}

	@Override
	public String visit(BooleanNode node) throws VisitingException {
		return node.getValue().toString();
	}

	@Override
	public String visit(DateNode node) throws VisitingException {
		return null;
	}

	@Override
	public String visit(DivideNode node) throws VisitingException {
		return createScriptForBinaryOperationNode(node, "/");
	}

	@Override
	public String visit(EqNode node) throws VisitingException {
		return createScriptForBinaryOperationNode(node, "==");
	}

	@Override
	public String visit(FactorNode node) throws VisitingException {
		return "(" + node.getArgument().accept(this) + ")";
	}

	@Override
	public String visit(DecimalNode node) throws VisitingException {
		return node.getValue().toString();
	}

	@Override
	public String visit(FunctionNode node) throws VisitingException {
		StringBuilder parameters = new StringBuilder();

		Iterator<ExpressionNode> it = node.getParameters().iterator();
		while (it.hasNext()) {
			parameters.append(it.next().accept(this));
			if (it.hasNext()) {
				parameters.append(", ");
			}
		}
		return node.getIdentifier() + "(" + parameters.toString() + ")";
	}

	@Override
	public String visit(GeqNode node) throws VisitingException {
		return createScriptForBinaryOperationNode(node, ">=");
	}

	@Override
	public String visit(GtNode node) throws VisitingException {
		return createScriptForBinaryOperationNode(node, ">");
	}

	@Override
	public String visit(IfNode node) throws VisitingException {
		return node.getCondition().accept(this) + " ? " + node.getIfBody().accept(this) + " : " 
				+ node.getElseBody().accept(this);
	}

	@Override
	public String visit(IntegerNode node) throws VisitingException {
		return node.getValue().toString();
	}

	@Override
	public String visit(LeqNode node) throws VisitingException {
		return createScriptForBinaryOperationNode(node, "<=");
	}

	@Override
	public String visit(LtNode node) throws VisitingException {
		return createScriptForBinaryOperationNode(node, "<");
	}

	@Override
	public String visit(ModuloNode node) throws VisitingException {
		return createScriptForBinaryOperationNode(node, "%");
	}

	@Override
	public String visit(MultiplyNode node) throws VisitingException {
		return createScriptForBinaryOperationNode(node, "*");
	}

	@Override
	public String visit(NegativeNode node) throws VisitingException {
		return "-" + node.getArgument().accept(this);
	}

	@Override
	public String visit(NeqNode node) throws VisitingException {
		return createScriptForBinaryOperationNode(node, "!=");
	}

	@Override
	public String visit(NotNode node) throws VisitingException {
		return "!" + node.getArgument().accept(this);
	}

	@Override
	public String visit(NullNode node) throws VisitingException {
		return "null";
	}

	@Override
	public String visit(OrNode node) throws VisitingException {
		return createScriptForBinaryOperationNode(node, "||");
	}

	@Override
	public String visit(PositiveNode node) throws VisitingException {
		return "+" + node.getArgument().accept(this);
	}

	@Override
	public String visit(PowerNode node) throws VisitingException {
		return createScriptForBinaryOperationNode(node, "^");
	}

	@Override
	public String visit(RootNode node) throws VisitingException {
		return node.getStatementListNode().accept(this);
	}

	@Override
	public String visit(StatementListNode node) throws VisitingException {
		StringBuilder statementList = new StringBuilder();

		for (Node child : node) {
			statementList.append(child.accept(this)).append("; ");
		}
		
		return statementList.toString();
	}

	@Override
	public String visit(StringNode node) throws VisitingException {
		return '"' + node.getValue() + '"';
	}

	@Override
	public String visit(SubstractNode node) throws VisitingException {
		return createScriptForBinaryOperationNode(node, "-");
	}

	@Override
	public String visit(VariableNode node) throws VisitingException {
		return node.getIdentifier();
	}

	/**
	 * Creates the script for a {@link BinaryOperationNode}.
	 * @param node The node.
	 * @param operator The String representation of the operator.
	 * @throws VisitingException
	 */
	protected String createScriptForBinaryOperationNode(BinaryOperationNode node, String operator)
			throws VisitingException {
		return node.getLhs().accept(this) + " " + operator + " " + node.getRhs().accept(this);
	}

}
