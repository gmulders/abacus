package org.gertje.abacus.nodes;

import org.gertje.abacus.token.Token;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Date;
import java.util.List;

public class AbacusNodeFactory implements NodeFactory {

	@Override
	public AddNode createAddNode(AbstractNode lhs, AbstractNode rhs, Token token) {
		return new AddNode(lhs, rhs, token);
	}

	@Override
	public AndNode createAndNode(AbstractNode lhs, AbstractNode rhs, Token token) {
		return new AndNode(lhs, rhs, token);
	}

	@Override
	public AssignmentNode createAssignmentNode(AbstractNode lhs, AbstractNode rhs, Token token) {
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
	public DivideNode createDivideNode(AbstractNode lhs, AbstractNode rhs, Token token) {
		return new DivideNode(lhs, rhs, token);
	}

	@Override
	public EqNode createEqNode(AbstractNode lhs, AbstractNode rhs, Token token) {
		return new EqNode(lhs, rhs, token);
	}

	@Override
	public FactorNode createFactorNode(AbstractNode argument, Token token) {
		return new FactorNode(argument, token);
	}

	@Override
	public FunctionNode createFunctionNode(String identifier, List<AbstractNode> parameters, Token token) {
		return new FunctionNode(identifier, parameters, token);
	}

	@Override
	public GeqNode createGeqNode(AbstractNode lhs, AbstractNode rhs, Token token) {
		return new GeqNode(lhs, rhs, token);
	}

	@Override
	public GtNode createGtNode(AbstractNode lhs, AbstractNode rhs, Token token) {
		return new GtNode(lhs, rhs, token);
	}

	@Override
	public IfNode createIfNode(AbstractNode condition, AbstractNode ifbody, AbstractNode elsebody, Token token) {
		return new IfNode(condition, ifbody, elsebody, token);
	}

	@Override
	public LeqNode createLeqNode(AbstractNode lhs, AbstractNode rhs, Token token) {
		return new LeqNode(lhs, rhs, token);
	}

	@Override
	public LtNode createLtNode(AbstractNode lhs, AbstractNode rhs, Token token) {
		return new LtNode(lhs, rhs, token);
	}

	@Override
	public ModuloNode createModuloNode(AbstractNode lhs, AbstractNode rhs, Token token) {
		return new ModuloNode(lhs, rhs, token);
	}

	@Override
	public MultiplyNode createMultiplyNode(AbstractNode lhs, AbstractNode rhs, Token token) {
		return new MultiplyNode(lhs, rhs, token);
	}
	
	@Override
	public NegativeNode createNegativeNode(AbstractNode argument, Token token) {
		return new NegativeNode(argument, token);
	}

	@Override
	public NeqNode createNeqNode(AbstractNode lhs, AbstractNode rhs, Token token) {
		return new NeqNode(lhs, rhs, token);
	}

	@Override
	public NotNode createNotNode(AbstractNode argument, Token token) {
		return new NotNode(argument, token);
	}

	@Override
	public FloatNode createFloatNode(BigDecimal value, Token token) {
		return new FloatNode(value, token);
	}

	@Override
	public IntegerNode createIntegerNode(BigInteger value, Token token) {
		return new IntegerNode(value, token);
	}

	@Override
	public OrNode createOrNode(AbstractNode lhs, AbstractNode rhs, Token token) {
		return new OrNode(lhs, rhs, token);
	}

	@Override
	public PositiveNode createPositiveNode(AbstractNode argument, Token token) {
		return new PositiveNode(argument, token);
	}

	@Override
	public PowerNode createPowerNode(AbstractNode base, AbstractNode power, Token token) {
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
	public SubstractNode createSubstractNode(AbstractNode lhs, AbstractNode rhs, Token token) {
		return new SubstractNode(lhs, rhs, token);
	}

	@Override
	public VariableNode createVariableNode(String identifier, Token token) {
		return new VariableNode(identifier, token);
	}

	@Override
	public NullNode createNullNode(Token token) {
		return new NullNode(token);
	}

}
