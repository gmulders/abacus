package org.gertje.abacus.nodevisitors;

import org.gertje.abacus.nodes.AddNode;
import org.gertje.abacus.nodes.AndNode;
import org.gertje.abacus.nodes.AssignmentNode;
import org.gertje.abacus.nodes.BinaryOperationNode;
import org.gertje.abacus.nodes.BooleanNode;
import org.gertje.abacus.nodes.ConcatStringNode;
import org.gertje.abacus.nodes.DateNode;
import org.gertje.abacus.nodes.DecimalNode;
import org.gertje.abacus.nodes.DivideNode;
import org.gertje.abacus.nodes.EqNode;
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
import org.gertje.abacus.nodes.SumNode;
import org.gertje.abacus.nodes.VariableNode;

/**
 * A translator to translate a {@link Node} to a GraphViz definition.
 */
public class GraphVizTranslator implements NodeVisitor<Void, VisitingException> {

	/**
	 * String builder that contains the node definitions.
	 */
	private StringBuilder nodeDefinitions = new StringBuilder();

	/**
	 * The references.
	 */
	private StringBuilder references = new StringBuilder();

	/**
	 * Translates the {@link Node} to a GraphViz definition.
	 * @param node The node to translate.
	 * @return The translation.
	 * @throws VisitingException
	 */
	public String translate(Node node) throws VisitingException {
		node.accept(this);
		return "digraph {\nrankdir=TB;\n" + nodeDefinitions.toString() + references.toString()
				+ "overlap=false;\nfontsize=12;\n}\n";
	}

	@Override
	public Void visit(AddNode node) throws VisitingException {
		addBinaryOperationNode(node, "+");
		return null;
	}

	@Override
	public Void visit(AndNode node) throws VisitingException {
		addBinaryOperationNode(node, "&&");
		return null;
	}

	@Override
	public Void visit(AssignmentNode node) throws VisitingException {
		addNodeDefinition(node, "=");
		addReference(node, node.getLhs());
		addReference(node, node.getRhs());
		return null;
	}

	@Override
	public Void visit(BooleanNode node) throws VisitingException {
		addNodeDefinition(node, "" + node.getValue());
		return null;
	}

	@Override
	public Void visit(ConcatStringNode node) throws VisitingException {
		addBinaryOperationNode(node, "+");
		return null;
	}

	@Override
	public Void visit(DateNode node) throws VisitingException {
		addNodeDefinition(node, "" + node.getValue());
		return null;
	}

	@Override
	public Void visit(DivideNode node) throws VisitingException {
		addBinaryOperationNode(node, "/");
		return null;
	}

	@Override
	public Void visit(EqNode node) throws VisitingException {
		addBinaryOperationNode(node, "==");
		return null;
	}

	@Override
	public Void visit(FactorNode node) throws VisitingException {
		addNodeDefinition(node, "()");
		addReference(node, node.getArgument());
		return null;
	}

	@Override
	public Void visit(DecimalNode node) throws VisitingException {
		addNodeDefinition(node, "" + node.getValue());
		return null;
	}

	@Override
	public Void visit(FunctionNode node) throws VisitingException {
		addNodeDefinition(node, "function call " + node.getIdentifier());
		for (Node subNode : node.getParameters()) {
			addReference(node, subNode);
		}
		return null;
	}

	@Override
	public Void visit(GeqNode node) throws VisitingException {
		addBinaryOperationNode(node, ">=");
		return null;
	}

	@Override
	public Void visit(GtNode node) throws VisitingException {
		addBinaryOperationNode(node, ">");
		return null;
	}

	@Override
	public Void visit(IfNode node) throws VisitingException {
		addNodeDefinition(node, "? :");
		addReference(node, node.getCondition());
		addReference(node, node.getIfBody());
		addReference(node, node.getElseBody());
		return null;
	}

	@Override
	public Void visit(IntegerNode node) throws VisitingException {
		addNodeDefinition(node, "" + node.getValue());
		return null;
	}

	@Override
	public Void visit(LeqNode node) throws VisitingException {
		addBinaryOperationNode(node, "<=");
		return null;
	}

	@Override
	public Void visit(LtNode node) throws VisitingException {
		addBinaryOperationNode(node, "<");
		return null;
	}

	@Override
	public Void visit(ModuloNode node) throws VisitingException {
		addBinaryOperationNode(node, "%");
		return null;
	}

	@Override
	public Void visit(MultiplyNode node) throws VisitingException {
		addBinaryOperationNode(node, "*");
		return null;
	}

	@Override
	public Void visit(NegativeNode node) throws VisitingException {
		addNodeDefinition(node, "-");
		addReference(node, node.getArgument());
		return null;
	}

	@Override
	public Void visit(NeqNode node) throws VisitingException {
		addBinaryOperationNode(node, "!=");
		return null;
	}

	@Override
	public Void visit(NotNode node) throws VisitingException {
		addNodeDefinition(node, "!");
		addReference(node, node.getArgument());
		return null;
	}

	@Override
	public Void visit(NullNode node) throws VisitingException {
		addNodeDefinition(node, "null");
		return null;
	}

	@Override
	public Void visit(OrNode node) throws VisitingException {
		addBinaryOperationNode(node, "||");
		return null;
	}

	@Override
	public Void visit(PositiveNode node) throws VisitingException {
		addNodeDefinition(node, "+");
		addReference(node, node.getArgument());
		return null;
	}

	@Override
	public Void visit(PowerNode node) throws VisitingException {
		addBinaryOperationNode(node, "^");
		return null;
	}

	@Override
	public Void visit(RootNode node) throws VisitingException {
		addNodeDefinition(node, "root");
		addReference(node, node.getStatementListNode());
		return null;
	}

	@Override
	public Void visit(StatementListNode node) throws VisitingException {
		addNodeDefinition(node, "list");
		for (Node subNode : node) {
			addReference(node, subNode);
		}
		return null;
	}

	@Override
	public Void visit(StringNode node) throws VisitingException {
		addNodeDefinition(node, "'" + node.getValue() + "'");
		return null;
	}

	@Override
	public Void visit(SubstractNode node) throws VisitingException {
		addBinaryOperationNode(node, "-");
		return null;
	}

	@Override
	public Void visit(SumNode node) throws VisitingException {
		addBinaryOperationNode(node, "+");
		return null;
	}

	@Override
	public Void visit(VariableNode node) throws VisitingException {
		addNodeDefinition(node, "var " + node.getIdentifier() + " " + node.getType());
		return null;
	}

	/**
	 * Adds the given node to the list of definitions.
	 * @param node The node to add to the list.
	 * @param label The label to give the node.
	 */
	private void addNodeDefinition(Node node, String label) {
		nodeDefinitions.append(determineNodeIdentifier(node)).append(" [label=\"").append(label)
				.append("\", shape=box];\n");
	}

	/**
	 * Adds a reference from {@code fromNode} to {@code toNode}.
	 * @param fromNode The node to reference from.
	 * @param toNode The node to reference to.
	 * @throws VisitingException
	 */
	private void addReference(Node fromNode, Node toNode) throws VisitingException {
		references.append(determineNodeIdentifier(fromNode)).append(" -> ").append(determineNodeIdentifier(toNode))
				.append(";\n");
		toNode.accept(this);
	}

	/**
	 * Adds a binary operation node and its references.
	 * @param node The node to add to the list.
	 * @param label The label to give the node.
	 * @throws VisitingException
	 */
	private void addBinaryOperationNode(BinaryOperationNode node, String label) throws VisitingException {
		addNodeDefinition(node, label);
		addReference(node, node.getLhs());
		addReference(node, node.getRhs());
	}

	/**
	 * Determines the label for the given node.
	 * @param node The node.
	 * @return the label.
	 */
	private String determineNodeIdentifier(Node node) {
		return node.getClass().getSimpleName() + "_" + Integer.toHexString(node.hashCode());
	}
}
