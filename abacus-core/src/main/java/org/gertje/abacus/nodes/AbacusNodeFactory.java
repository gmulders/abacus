package org.gertje.abacus.nodes;

import org.gertje.abacus.token.Token;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;

/**
 * Default implementation of {@link NodeFactory}.
 */
public class AbacusNodeFactory implements NodeFactory {

	@Override
	public AddNode createAddNode(ExpressionNode lhs, ExpressionNode rhs, Token token) {
		return new AddNode(lhs, rhs, token);
	}

	@Override
	public AndNode createAndNode(ExpressionNode lhs, ExpressionNode rhs, Token token) {
		return new AndNode(lhs, rhs, token);
	}

	@Override
	public ArrayNode createArrayNode(ExpressionNode array, ExpressionNode index, Token token) {
		return new ArrayNode(array, index, token);
	}

	@Override
	public AssignmentNode createAssignmentNode(ExpressionNode lhs, ExpressionNode rhs, Token token) {
		return new AssignmentNode(lhs, rhs, token);
	}

	@Override
	public BooleanNode createBooleanNode(Boolean value, Token token) {
		return new BooleanNode(value, token);
	}

	@Override
	public ConcatStringNode createConcatStringNode(ExpressionNode lhs, ExpressionNode rhs, Token token) {
		return new ConcatStringNode(lhs, rhs, token);
	}

	@Override
	public DateNode createDateNode(Date value, Token token) {
		return new DateNode(value, token);
	}

	@Override
	public DecimalNode createDecimalNode(BigDecimal value, Token token) {
		return new DecimalNode(value, token);
	}

	@Override
	public DivideNode createDivideNode(ExpressionNode lhs, ExpressionNode rhs, Token token) {
		return new DivideNode(lhs, rhs, token);
	}

	@Override
	public EqNode createEqNode(ExpressionNode lhs, ExpressionNode rhs, Token token) {
		return new EqNode(lhs, rhs, token);
	}

	@Override
	public FactorNode createFactorNode(ExpressionNode argument, Token token) {
		return new FactorNode(argument, token);
	}

	@Override
	public FunctionNode createFunctionNode(String identifier, List<ExpressionNode> parameters, Token token) {
		return new FunctionNode(identifier, parameters, token);
	}

	@Override
	public GeqNode createGeqNode(ExpressionNode lhs, ExpressionNode rhs, Token token) {
		return new GeqNode(lhs, rhs, token);
	}

	@Override
	public GtNode createGtNode(ExpressionNode lhs, ExpressionNode rhs, Token token) {
		return new GtNode(lhs, rhs, token);
	}

	@Override
	public IfNode createIfNode(ExpressionNode condition, ExpressionNode ifbody, ExpressionNode elsebody, Token token) {
		return new IfNode(condition, ifbody, elsebody, token);
	}

	@Override
	public IntegerNode createIntegerNode(Long value, Token token) {
		return new IntegerNode(value, token);
	}

	@Override
	public LeqNode createLeqNode(ExpressionNode lhs, ExpressionNode rhs, Token token) {
		return new LeqNode(lhs, rhs, token);
	}

	@Override
	public LtNode createLtNode(ExpressionNode lhs, ExpressionNode rhs, Token token) {
		return new LtNode(lhs, rhs, token);
	}

	@Override
	public ModuloNode createModuloNode(ExpressionNode lhs, ExpressionNode rhs, Token token) {
		return new ModuloNode(lhs, rhs, token);
	}

	@Override
	public MultiplyNode createMultiplyNode(ExpressionNode lhs, ExpressionNode rhs, Token token) {
		return new MultiplyNode(lhs, rhs, token);
	}
	
	@Override
	public NegativeNode createNegativeNode(ExpressionNode argument, Token token) {
		return new NegativeNode(argument, token);
	}

	@Override
	public NeqNode createNeqNode(ExpressionNode lhs, ExpressionNode rhs, Token token) {
		return new NeqNode(lhs, rhs, token);
	}

	@Override
	public NotNode createNotNode(ExpressionNode argument, Token token) {
		return new NotNode(argument, token);
	}

	@Override
	public NullNode createNullNode(Token token) {
		return new NullNode(token);
	}

	@Override
	public OrNode createOrNode(ExpressionNode lhs, ExpressionNode rhs, Token token) {
		return new OrNode(lhs, rhs, token);
	}

	@Override
	public PositiveNode createPositiveNode(ExpressionNode argument, Token token) {
		return new PositiveNode(argument, token);
	}

	@Override
	public PowerNode createPowerNode(ExpressionNode base, ExpressionNode power, Token token) {
		return new PowerNode(base, power, token);
	}

	@Override
	public RootNode createRootNode(StatementListNode node, Token token) {
		return new RootNode(node, token);
	}

	@Override
	public StatementListNode createStatementListNode(Token token) {
		return new StatementListNode(token);
	}

	@Override
	public StringNode createStringNode(String value, Token token) {
		return new StringNode(value, token);
	}

	@Override
	public SubtractNode createSubtractNode(ExpressionNode lhs, ExpressionNode rhs, Token token) {
		return new SubtractNode(lhs, rhs, token);
	}

	@Override
	public SumNode createSumNode(ExpressionNode lhs, ExpressionNode rhs, Token token) {
		return new SumNode(lhs, rhs, token);
	}

	@Override
	public VariableNode createVariableNode(String identifier, Token token) {
		return new VariableNode(identifier, token);
	}
}
