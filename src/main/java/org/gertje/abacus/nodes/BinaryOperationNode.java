package org.gertje.abacus.nodes;

/**
 * Node that represents a binary operation.
 */
public interface BinaryOperationNode extends Node {
	Node getLhs();
	void setLhs(Node node);
	Node getRhs();
	void setRhs(Node node);
}
