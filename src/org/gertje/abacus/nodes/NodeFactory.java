package org.gertje.abacus.nodes;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;

import org.gertje.abacus.Token;

public class NodeFactory implements NodeFactoryInterface {

	@Override
	public AddNode createAddNode(AbstractNode lhs, AbstractNode rhs, Token token) {
		return new AddNode(lhs, rhs, token, this);
	}

	@Override
	public AndNode createAndNode(AbstractNode lhs, AbstractNode rhs, Token token) {
		return new AndNode(lhs, rhs, token, this);
	}

	@Override
	public AssignmentNode createAssignmentNode(VariableNode lhs, AbstractNode rhs, Token token) {
		return new AssignmentNode(lhs, rhs, token, this);
	}

	@Override
	public BooleanNode createBooleanNode(Boolean value, Token token) {
		return new BooleanNode(value, token, this);
	}

	@Override
	public DateNode createDateNode(Date value, Token token) {
		return new DateNode(value, token, this);
	}

	@Override
	public DivideNode createDivideNode(AbstractNode lhs, AbstractNode rhs, Token token) {
		return new DivideNode(lhs, rhs, token, this);
	}

	@Override
	public EqNode createEqNode(AbstractNode lhs, AbstractNode rhs, Token token) {
		return new EqNode(lhs, rhs, token, this);
	}

	@Override
	public FactorNode createFactorNode(AbstractNode argument, Token token) {
		return new FactorNode(argument, token, this);
	}

	@Override
	public FunctionNode createFunctionNode(String identifier, List<AbstractNode> parameters, Token token) {
		return new FunctionNode(identifier, parameters, token, this);
	}

	@Override
	public GeqNode createGeqNode(AbstractNode lhs, AbstractNode rhs, Token token) {
		return new GeqNode(lhs, rhs, token, this);
	}

	@Override
	public GtNode createGtNode(AbstractNode lhs, AbstractNode rhs, Token token) {
		return new GtNode(lhs, rhs, token, this);
	}

	@Override
	public IfNode createIfNode(AbstractNode condition, AbstractNode ifbody, AbstractNode elsebody, Token token) {
		return new IfNode(condition, ifbody, elsebody, token, this);
	}

	@Override
	public LeqNode createLeqNode(AbstractNode lhs, AbstractNode rhs, Token token) {
		return new LeqNode(lhs, rhs, token, this);
	}

	@Override
	public LtNode createLtNode(AbstractNode lhs, AbstractNode rhs, Token token) {
		return new LtNode(lhs, rhs, token, this);
	}

	@Override
	public ModuloNode createModuloNode(AbstractNode lhs, AbstractNode rhs, Token token) {
		return new ModuloNode(lhs, rhs, token, this);
	}

	@Override
	public MultiplyNode createMultiplyNode(AbstractNode lhs, AbstractNode rhs, Token token) {
		return new MultiplyNode(lhs, rhs, token, this);
	}
	
	@Override
	public NegativeNode createNegativeNode(AbstractNode argument, Token token) {
		return new NegativeNode(argument, token, this);
	}

	@Override
	public NeqNode createNeqNode(AbstractNode lhs, AbstractNode rhs, Token token) {
		return new NeqNode(lhs, rhs, token, this);
	}

	@Override
	public NotNode createNotNode(AbstractNode argument, Token token) {
		return new NotNode(argument, token, this);
	}

	@Override
	public NumberNode createNumberNode(BigDecimal value, Token token) {
		return new NumberNode(value, token, this);
	}

	@Override
	public OrNode createOrNode(AbstractNode lhs, AbstractNode rhs, Token token) {
		return new OrNode(lhs, rhs, token, this);
	}

	@Override
	public PositiveNode createPositiveNode(AbstractNode argument, Token token) {
		return new PositiveNode(argument, token, this);
	}

	@Override
	public PowerNode createPowerNode(AbstractNode base, AbstractNode power, Token token) {
		return new PowerNode(base, power, token, this);
	}

	@Override
	public StatementListNode createStatementListNode(Token token) {
		return new StatementListNode(token, this);
	}

	@Override
	public StringNode createStringNode(String value, Token token) {
		return new StringNode(value, token, this);
	}

	@Override
	public SubstractNode createSubstractNode(AbstractNode lhs, AbstractNode rhs, Token token) {
		return new SubstractNode(lhs, rhs, token, this);
	}

	@Override
	public VariableNode createVariableNode(String identifier, Token token) {
		return new VariableNode(identifier, token, this);
	}

}
