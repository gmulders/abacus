package org.gertje.abacus.nodes;

import org.gertje.abacus.token.Token;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Abstract node.
 */
public abstract class AbstractNode implements Node {

	/**
	 * The id for the next node that is created.
	 */
	private static AtomicLong NEXT_ID = new AtomicLong(0);

	/**
	 * The token from which the node was created.
	 */
	protected Token token;

	/**
	 * The id of the node.
	 */
	private long id = NEXT_ID.incrementAndGet();

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

	@Override
	public long getId() {
		return id;
	}
}
