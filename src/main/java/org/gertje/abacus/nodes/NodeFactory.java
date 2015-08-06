package org.gertje.abacus.nodes;

import org.gertje.abacus.token.Token;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Date;
import java.util.List;

/**
 * Deze interface definieert methodes voor alle nodes die de parser aan kan maken.
 */
public interface NodeFactory {

	AddNode createAddNode(Node lhs, Node rhs, Token token);
	AndNode createAndNode(Node lhs, Node rhs, Token token);
	AssignmentNode createAssignmentNode(Node lhs, Node rhs, Token token);
	BooleanNode createBooleanNode(Boolean value, Token token);
	DateNode createDateNode(Date value, Token token);
	DecimalNode createDecimalNode(BigDecimal value, Token token);
	DivideNode createDivideNode(Node lhs, Node rhs, Token token);
	EqNode createEqNode(Node lhs, Node rhs, Token token);
	FactorNode createFactorNode(Node argument, Token token);
	FunctionNode createFunctionNode(String identifier, List<Node> parameters, Token token);
	GeqNode createGeqNode(Node lhs, Node rhs, Token token);
	GtNode createGtNode(Node lhs, Node rhs, Token token);
	IfNode createIfNode(Node condition, Node ifbody, Node elsebody, Token token);
	IntegerNode createIntegerNode(BigInteger value, Token token);
	LeqNode createLeqNode(Node lhs, Node rhs, Token token);
	LtNode createLtNode(Node lhs, Node rhs, Token token);
	ModuloNode createModuloNode(Node lhs, Node rhs, Token token);
	MultiplyNode createMultiplyNode(Node lhs, Node rhs, Token token);
	NegativeNode createNegativeNode(Node argument, Token token);
	NeqNode createNeqNode(Node lhs, Node rhs, Token token);
	NotNode createNotNode(Node argument, Token token);
	NullNode createNullNode(Token token);
	OrNode createOrNode(Node lhs, Node rhs, Token token);
	PositiveNode createPositiveNode(Node argument, Token token);
	PowerNode createPowerNode(Node base, Node power, Token token);
	StatementListNode createStatementListNode(Token token);
	StringNode createStringNode(String value, Token token);
	SubstractNode createSubstractNode(Node lhs, Node rhs, Token token);
	VariableNode createVariableNode(String identifier, Token token);
}
