package org.gertje.abacus.nodes;

/**
 * Node that represents a binary operation.
 */
public interface BinaryOperationNode extends ExpressionNode {
	ExpressionNode getLhs();
	void setLhs(ExpressionNode node);
	ExpressionNode getRhs();
	void setRhs(ExpressionNode node);
}
