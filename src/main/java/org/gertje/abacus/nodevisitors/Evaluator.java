package org.gertje.abacus.nodevisitors;

import org.gertje.abacus.context.AbacusContext;
import org.gertje.abacus.nodes.ExpressionNode;
import org.gertje.abacus.nodes.Node;
import org.gertje.abacus.nodes.NodeType;
import org.gertje.abacus.nodes.RootNode;
import org.gertje.abacus.nodes.StatementListNode;

import java.util.Iterator;

/**
 * Evaluator for an AST.
 */
public class Evaluator extends AbstractStatementNodeVisitor<Void, EvaluationException> {

	/**
	 * Evaluator for expressions.
	 */
	private ExpressionEvaluator expressionEvaluator;

	/**
	 * The result of the evaluation.
	 */
	private Object result;


	public Evaluator(AbacusContext abacusContext) {
		expressionEvaluator = new ExpressionEvaluator(abacusContext);
	}

	public Object evaluate(Node node) throws EvaluationException {
		if (node.getNodeType() == NodeType.EXPRESSION) {
			return expressionEvaluator.evaluate((ExpressionNode) node);
		}

		node.accept(this);

		return result;
	}

	@Override
	public Void visit(RootNode node) throws EvaluationException {
		return node.getStatementListNode().accept(this);
	}

	@Override
	public Void visit(StatementListNode node) throws EvaluationException {
		Iterator<Node> it = node.iterator();
		while (it.hasNext()) {
			Node subNode = it.next();
			Object subResult = null;

			// If the node is an expression, use the expression evaluator.
			if (subNode.getNodeType() == NodeType.EXPRESSION) {
				subResult = expressionEvaluator.evaluate((ExpressionNode) subNode);
			} else {
				subNode.accept(this);
			}

			if (!it.hasNext()) {
				result = subResult;
			}
		}

		return null;
	}

	public Object getResult() {
		return result;
	}
}
