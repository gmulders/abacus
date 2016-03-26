package org.gertje.abacus.nodes;

import org.gertje.abacus.nodevisitors.NodeVisitor;
import org.gertje.abacus.nodevisitors.VisitingException;
import org.gertje.abacus.token.Token;

public interface Node {
	/**
	 * Registreert een visitor bij de node.
	 *
	 * De bedoeling is dat een node deze methode ook aanroept bij zijn child-nodes.
	 *
	 * @param visitor De visitor.
	 * @throws X
	 */
	<R, X extends VisitingException> R accept(NodeVisitor<R, X> visitor) throws X;

	/**
	 * Geeft het token terug.
	 * @return het token.
	 */
	Token getToken();
}
