package org.gertje.abacus.nodevisitors;

import org.gertje.abacus.context.AbacusContext;
import org.gertje.abacus.nodes.ExpressionNode;
import org.gertje.abacus.nodes.Node;
import org.gertje.abacus.nodes.NodeFactory;
import org.gertje.abacus.nodes.NodeType;
import org.gertje.abacus.nodes.RootNode;
import org.gertje.abacus.nodes.StatementListNode;

/**
 * Simplifier for an AST.
 */
public class Simplifier extends AbstractStatementNodeVisitor<Node, SimplificationException> {

	/**
	 * Simplifies expressions.
	 */
	private ExpressionSimplifier expressionSimplifier;

	public Simplifier(AbacusContext abacusContext, NodeFactory nodeFactory) {
		expressionSimplifier = new ExpressionSimplifier(abacusContext, nodeFactory);
	}

	public Node simplify(Node node) throws SimplificationException {
		if (node.getNodeType() == NodeType.EXPRESSION) {
			return expressionSimplifier.simplify((ExpressionNode) node);
		}

		return node.accept(this);
	}

	@Override
	public Node visit(RootNode node) throws SimplificationException {
		return node.getStatementListNode().accept(this);
	}

	@Override
	public Node visit(StatementListNode node) throws SimplificationException {
		// Visit all sub nodes.
		for (int i = 0; i < node.size(); i++) {
			Node subNode = node.get(i);
			// If the node is an expression, use the expression simplifier.
			if (subNode.getNodeType() == NodeType.EXPRESSION) {
				subNode = expressionSimplifier.simplify((ExpressionNode) subNode);
			} else {
				subNode = subNode.accept(this);
			}
			node.set(i, subNode);
		}

		// Return the first item if there is only one item.
		if (node.size() == 1) {
			return node.get(0);
		}

		// Return the node.
		return node;
	}
}
