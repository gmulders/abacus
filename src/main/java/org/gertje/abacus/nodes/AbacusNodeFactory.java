package org.gertje.abacus.nodes;

import org.gertje.abacus.token.Token;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Date;
import java.util.List;

/**
 * Default implementation of {@link NodeFactory}.
 */
public class AbacusNodeFactory implements NodeFactory {

	@Override
	public AddNode createAddNode(Node lhs, Node rhs, Token token) {
		return new AddNode(lhs, rhs, token);
	}

	@Override
	public AndNode createAndNode(Node lhs, Node rhs, Token token) {
		return new AndNode(lhs, rhs, token);
	}

	@Override
	public AssignmentNode createAssignmentNode(Node lhs, Node rhs, Token token) {
		return new AssignmentNode(lhs, rhs, token);
	}

	@Override
	public BooleanNode createBooleanNode(Boolean value, Token token) {
		return new BooleanNode(value, token);
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
	public DivideNode createDivideNode(Node lhs, Node rhs, Token token) {
		return new DivideNode(lhs, rhs, token);
	}

	@Override
	public EqNode createEqNode(Node lhs, Node rhs, Token token) {
		return new EqNode(lhs, rhs, token);
	}

	@Override
	public FactorNode createFactorNode(Node argument, Token token) {
		return new FactorNode(argument, token);
	}

	@Override
	public FunctionNode createFunctionNode(String identifier, List<Node> parameters, Token token) {
		return new FunctionNode(identifier, parameters, token);
	}

	@Override
	public GeqNode createGeqNode(Node lhs, Node rhs, Token token) {
		return new GeqNode(lhs, rhs, token);
	}

	@Override
	public GtNode createGtNode(Node lhs, Node rhs, Token token) {
		return new GtNode(lhs, rhs, token);
	}

	@Override
	public IfNode createIfNode(Node condition, Node ifbody, Node elsebody, Token token) {
		return new IfNode(condition, ifbody, elsebody, token);
	}

	@Override
	public IntegerNode createIntegerNode(BigInteger value, Token token) {
		return new IntegerNode(value, token);
	}

	@Override
	public LeqNode createLeqNode(Node lhs, Node rhs, Token token) {
		return new LeqNode(lhs, rhs, token);
	}

	@Override
	public LtNode createLtNode(Node lhs, Node rhs, Token token) {
		return new LtNode(lhs, rhs, token);
	}

	@Override
	public ModuloNode createModuloNode(Node lhs, Node rhs, Token token) {
		return new ModuloNode(lhs, rhs, token);
	}

	@Override
	public MultiplyNode createMultiplyNode(Node lhs, Node rhs, Token token) {
		return new MultiplyNode(lhs, rhs, token);
	}
	
	@Override
	public NegativeNode createNegativeNode(Node argument, Token token) {
		return new NegativeNode(argument, token);
	}

	@Override
	public NeqNode createNeqNode(Node lhs, Node rhs, Token token) {
		return new NeqNode(lhs, rhs, token);
	}

	@Override
	public NotNode createNotNode(Node argument, Token token) {
		return new NotNode(argument, token);
	}

	@Override
	public NullNode createNullNode(Token token) {
		return new NullNode(token);
	}

	@Override
	public OrNode createOrNode(Node lhs, Node rhs, Token token) {
		return new OrNode(lhs, rhs, token);
	}

	@Override
	public PositiveNode createPositiveNode(Node argument, Token token) {
		return new PositiveNode(argument, token);
	}

	@Override
	public PowerNode createPowerNode(Node base, Node power, Token token) {
		return new PowerNode(base, power, token);
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
	public SubstractNode createSubstractNode(Node lhs, Node rhs, Token token) {
		return new SubstractNode(lhs, rhs, token);
	}

	@Override
	public VariableNode createVariableNode(String identifier, Token token) {
		return new VariableNode(identifier, token);
	}
}
