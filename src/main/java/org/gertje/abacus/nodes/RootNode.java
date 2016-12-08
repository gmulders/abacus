package org.gertje.abacus.nodes;

import org.gertje.abacus.nodevisitors.NodeVisitor;
import org.gertje.abacus.nodevisitors.VisitingException;
import org.gertje.abacus.token.Token;
import org.gertje.abacus.types.Type;

/**
 * The root node of the AST.
 */
public class RootNode extends AbstractNode {

	/**
	 * A list of statements.
	 */
	private StatementListNode statementListNode;

	public RootNode(StatementListNode statementListNode, Token token) {
		super(token);
		this.statementListNode = statementListNode;
	}

	public Type getType() {
		return statementListNode.getType();
	}

	@Override
	public <R, X extends VisitingException> R accept(NodeVisitor<R, X> visitor) throws X {
		return visitor.visit(this);
	}

	public StatementListNode getStatementListNode() {
		return statementListNode;
	}

	public void setStatementListNode(StatementListNode statementListNode) {
		this.statementListNode = statementListNode;
	}
}
