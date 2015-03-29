package org.gertje.abacus.nodes;

/**
 * Stelt een node voor een binaire operatie voor.
 */
public interface BinaryOperationNode extends Node {
	AbstractNode getLhs();
	AbstractNode getRhs();
}
