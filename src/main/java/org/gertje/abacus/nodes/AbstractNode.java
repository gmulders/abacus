package org.gertje.abacus.nodes;

import org.gertje.abacus.token.Token;

/**
 * Abstract node.
 */
public abstract class AbstractNode implements Node {

	/**
	 * The token from which the node was created.
	 */
	protected Token token;

	public AbstractNode(Token token) {
		this.token = token;
	}

	@Override
	public Token getToken() {
		return token;
	}

	@Override
	public NodeType getNodeType() {
		return NodeType.STATEMENT;
	}
}
