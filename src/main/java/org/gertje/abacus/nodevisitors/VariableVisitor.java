package org.gertje.abacus.nodevisitors;

import org.gertje.abacus.nodes.AssignmentNode;
import org.gertje.abacus.nodes.VariableNode;
import org.gertje.abacus.symboltable.Variable;

import java.util.HashSet;
import java.util.Set;

public class VariableVisitor extends DefaultVisitor<Void, VisitingException> {

	private Set<Variable> readVariables = new HashSet<>();

	private Set<Variable> storeVariables = new HashSet<>();

	@Override
	public Void visit(AssignmentNode node) throws VisitingException {
		VariableNode variableNode = (VariableNode)node.getLhs();
		storeVariables.add(new Variable(variableNode.getIdentifier(), variableNode.getType()));
		node.getRhs().accept(this);
		return null;
	}

	@Override
	public Void visit(VariableNode node) throws VisitingException {
		readVariables.add(new Variable(node.getIdentifier(), node.getType()));
		return null;
	}

	public Set<Variable> getReadVariables() {
		return readVariables;
	}

	public Set<Variable> getStoreVariables() {
		return storeVariables;
	}
}
