package org.gertje.abacus.nodes;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;

import org.gertje.abacus.Token;

/**
 * Deze interface definieert methodes voor alle nodes die de lexer aan kan maken.
 */
public interface NodeFactoryInterface {

	public AddNode createAddNode(AbstractNode lhs, AbstractNode rhs, Token token);
	public AndNode createAndNode(AbstractNode lhs, AbstractNode rhs, Token token);
	public AssignmentNode createAssignmentNode(VariableNode lhs, AbstractNode rhs, Token token);
	public BooleanNode createBooleanNode(Boolean value, Token token);
	public DateNode createDateNode(Date value, Token token);
	public DivideNode createDivideNode(AbstractNode lhs, AbstractNode rhs, Token token);
	public EqNode createEqNode(AbstractNode lhs, AbstractNode rhs, Token token);
	public FactorNode createFactorNode(AbstractNode argument, Token token);
	public FunctionNode createFunctionNode(String identifier, List<AbstractNode> parameters, Token token);
	public GeqNode createGeqNode(AbstractNode lhs, AbstractNode rhs, Token token);
	public GtNode createGtNode(AbstractNode lhs, AbstractNode rhs, Token token);
	public IfNode createIfNode(AbstractNode condition, AbstractNode ifbody, AbstractNode elsebody, Token token);
	public LeqNode createLeqNode(AbstractNode lhs, AbstractNode rhs, Token token);
	public LtNode createLtNode(AbstractNode lhs, AbstractNode rhs, Token token);
	public ModuloNode createModuloNode(AbstractNode lhs, AbstractNode rhs, Token token);
	public MultiplyNode createMultiplyNode(AbstractNode lhs, AbstractNode rhs, Token token);
	public NegativeNode createNegativeNode(AbstractNode argument, Token token);
	public NeqNode createNeqNode(AbstractNode lhs, AbstractNode rhs, Token token);
	public NotNode createNotNode(AbstractNode argument, Token token);
	public NumberNode createNumberNode(BigDecimal value, Token token);
	public OrNode createOrNode(AbstractNode lhs, AbstractNode rhs, Token token);
	public PositiveNode createPositiveNode(AbstractNode argument, Token token);
	public PowerNode createPowerNode(AbstractNode base, AbstractNode power, Token token);
	public StatementListNode createStatementListNode(Token token);
	public StringNode createStringNode(String value, Token token);
	public SubstractNode createSubstractNode(AbstractNode lhs, AbstractNode rhs, Token token);
	public VariableNode createVariableNode(String identifier, Token token);
}
