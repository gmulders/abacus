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

	AddNode createAddNode(ExpressionNode lhs, ExpressionNode rhs, Token token);
	AndNode createAndNode(ExpressionNode lhs, ExpressionNode rhs, Token token);
	AssignmentNode createAssignmentNode(ExpressionNode lhs, ExpressionNode rhs, Token token);
	BooleanNode createBooleanNode(Boolean value, Token token);
	DateNode createDateNode(Date value, Token token);
	DecimalNode createDecimalNode(BigDecimal value, Token token);
	DivideNode createDivideNode(ExpressionNode lhs, ExpressionNode rhs, Token token);
	EqNode createEqNode(ExpressionNode lhs, ExpressionNode rhs, Token token);
	FactorNode createFactorNode(ExpressionNode argument, Token token);
	FunctionNode createFunctionNode(String identifier, List<ExpressionNode> parameters, Token token);
	GeqNode createGeqNode(ExpressionNode lhs, ExpressionNode rhs, Token token);
	GtNode createGtNode(ExpressionNode lhs, ExpressionNode rhs, Token token);
	IfNode createIfNode(ExpressionNode condition, ExpressionNode ifbody, ExpressionNode elsebody, Token token);
	IntegerNode createIntegerNode(BigInteger value, Token token);
	LeqNode createLeqNode(ExpressionNode lhs, ExpressionNode rhs, Token token);
	LtNode createLtNode(ExpressionNode lhs, ExpressionNode rhs, Token token);
	ModuloNode createModuloNode(ExpressionNode lhs, ExpressionNode rhs, Token token);
	MultiplyNode createMultiplyNode(ExpressionNode lhs, ExpressionNode rhs, Token token);
	NegativeNode createNegativeNode(ExpressionNode argument, Token token);
	NeqNode createNeqNode(ExpressionNode lhs, ExpressionNode rhs, Token token);
	NotNode createNotNode(ExpressionNode argument, Token token);
	NullNode createNullNode(Token token);
	OrNode createOrNode(ExpressionNode lhs, ExpressionNode rhs, Token token);
	PositiveNode createPositiveNode(ExpressionNode argument, Token token);
	PowerNode createPowerNode(ExpressionNode base, ExpressionNode power, Token token);
	StatementListNode createStatementListNode(Token token);
	StringNode createStringNode(String value, Token token);
	SubstractNode createSubstractNode(ExpressionNode lhs, ExpressionNode rhs, Token token);
	VariableNode createVariableNode(String identifier, Token token);
}
