package org.gertje.abacus.nodevisitors;

import org.gertje.abacus.nodes.ArrayNode;
import org.gertje.abacus.nodes.AssignmentNode;
import org.gertje.abacus.nodes.VariableNode;
import org.gertje.abacus.symboltable.Variable;

import java.util.HashSet;
import java.util.Set;

public class VariableVisitor extends DefaultVisitor<Void, VisitingException> {

	private boolean isForRead = true;

	private Set<Variable> readVariables = new HashSet<>();

	private Set<Variable> storeVariables = new HashSet<>();

	public Void visit(ArrayNode node) throws VisitingException {
		isForRead = true;
		node.getArray().accept(this);
		node.getIndex().accept(this);
		return null;
	}

	@Override
	public Void visit(AssignmentNode node) throws VisitingException {
		isForRead = false;
		node.getLhs().accept(this);
		isForRead = true;
		node.getRhs().accept(this);
		return null;
	}

	@Override
	public Void visit(VariableNode node) throws VisitingException {
		Variable variable = new Variable(node.getIdentifier(), node.getType());
		if (isForRead) {
			readVariables.add(variable);
		} else {
			storeVariables.add(variable);
		}
		return null;
	}

	public Set<Variable> getReadVariables() {
		return readVariables;
	}

	public Set<Variable> getStoreVariables() {
		return storeVariables;
	}
}
