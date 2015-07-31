package org.gertje.abacus.nodevisitors;

import org.gertje.abacus.context.AbacusContext;
import org.gertje.abacus.nodes.AddNode;
import org.gertje.abacus.nodes.AndNode;
import org.gertje.abacus.nodes.AssignmentNode;
import org.gertje.abacus.nodes.BinaryOperationNode;
import org.gertje.abacus.nodes.BooleanNode;
import org.gertje.abacus.nodes.DateNode;
import org.gertje.abacus.nodes.DecimalNode;
import org.gertje.abacus.nodes.DivideNode;
import org.gertje.abacus.nodes.EqNode;
import org.gertje.abacus.nodes.FactorNode;
import org.gertje.abacus.nodes.FunctionNode;
import org.gertje.abacus.nodes.GeqNode;
import org.gertje.abacus.nodes.GtNode;
import org.gertje.abacus.nodes.IfNode;
import org.gertje.abacus.nodes.IntegerNode;
import org.gertje.abacus.nodes.LeqNode;
import org.gertje.abacus.nodes.LtNode;
import org.gertje.abacus.nodes.ModuloNode;
import org.gertje.abacus.nodes.MultiplyNode;
import org.gertje.abacus.nodes.NegativeNode;
import org.gertje.abacus.nodes.NeqNode;
import org.gertje.abacus.nodes.Node;
import org.gertje.abacus.nodes.NodeFactory;
import org.gertje.abacus.nodes.NotNode;
import org.gertje.abacus.nodes.NullNode;
import org.gertje.abacus.nodes.OrNode;
import org.gertje.abacus.nodes.PositiveNode;
import org.gertje.abacus.nodes.PowerNode;
import org.gertje.abacus.nodes.StatementListNode;
import org.gertje.abacus.nodes.StringNode;
import org.gertje.abacus.nodes.SubstractNode;
import org.gertje.abacus.nodes.VariableNode;
import org.gertje.abacus.token.Token;
import org.gertje.abacus.types.Type;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Date;

public class Simplifier extends AbstractNodeVisitor<Node, SimplificationException> {

	/**
	 * De context.
	 */
	private final AbacusContext abacusContext;

	/**
	 * De nodefactory die we gebruiken om nodes aan te maken.
	 */
	private NodeFactory nodeFactory;

	/**
	 * De evaluator die we gebruiken om nodes te vereenvoudigen waar mogelijk.
	 */
	private Evaluator evaluator;

	/**
	 * Constructor.
	 */
	public Simplifier(AbacusContext abacusContext, NodeFactory nodeFactory) {
		this.nodeFactory = nodeFactory;
		this.abacusContext = abacusContext;

		// Maak een evaluator aan om de nodes te vereenvoudigen.
		evaluator = new Evaluator(abacusContext);
	}

	public Node simplify(Node node) throws SimplificationException {
		return node.accept(this);
	}

	@Override
	public Node visit(AddNode node) throws SimplificationException {
		return simplifyBinaryOperation(node);
	}

	@Override
	public Node visit(AndNode node) throws SimplificationException {
		Node lhs = node.getLhs();
		Node rhs = node.getRhs();

		// Vereenvoudig de nodes indien mogelijk.
		lhs = lhs.accept(this); node.setLhs(lhs);
		rhs = rhs.accept(this); node.setRhs(rhs);

		// Wanneer beide zijden constant zijn kunnen we de node vereenvoudigen.
		if (lhs.getIsConstant() && rhs.getIsConstant()) {
			return nodeFactory.createBooleanNode((Boolean)evaluateConstantNode(node), node.getToken());
		}

		// Wanneer links constant is (dan is rechts dat niet), bepaal dan de waarde van links.
		if (lhs.getIsConstant()) {
			Boolean left = (Boolean)evaluateConstantNode(lhs);
			// Wanneer links null is, vervang dan de linkerkant door een BooleanNode met waarde null.
			if (left == null) {
				node.setLhs(nodeFactory.createBooleanNode(null, lhs.getToken()));
				return node;
			}

			// Wanneer links naar false evalueert, evalueert de hele expressie naar false en kunnen we de node
			// vereenvoudigen.
			if (!left.booleanValue()) {
				return nodeFactory.createBooleanNode(Boolean.FALSE, node.getToken());
			}

			// Wanneer we hier komen, is links true. Daarom evalueert de expressie naar de waarde van de rechter
			// kant.
			return rhs;
		}

		// Wanneer rechts constant is (dan is links dat niet), bepaal dan de waarde van rechts.
		if (rhs.getIsConstant()) {
			Boolean right = (Boolean)evaluateConstantNode(rhs);
			// Wanneer rechts null is, vervang dan de rechterkant door een BooleanNode met waarde null.
			if (right == null) {
				node.setRhs(nodeFactory.createBooleanNode(null, rhs.getToken()));
				return node;
			}

			// Wanneer rechts naar false evalueert, evalueert de hele expressie naar false en kunnen we de node
			// vereenvoudigen.
			if (!right.booleanValue()) {
				return nodeFactory.createBooleanNode(Boolean.FALSE, node.getToken());
			}

			// Wanneer we hier komen, is rechts true. Daarom evalueert de expressie naar de waarde van de linker
			// kant.
			return lhs;
		}


		return node;
	}

	@Override
	public Node visit(AssignmentNode node) throws SimplificationException {
		Node lhs = node.getLhs();
		Node rhs = node.getRhs();

		// Vereenvoudig de nodes indien mogelijk.
		lhs = lhs.accept(this); node.setLhs(lhs);
		rhs = rhs.accept(this); node.setRhs(rhs);

		// We kunnen deze node niet verder vereenvoudigen, geef de node terug.
		return node;
	}

	@Override
	public Node visit(BooleanNode node) throws SimplificationException {
		return node;
	}

	@Override
	public Node visit(DateNode node) throws SimplificationException {
		return node;
	}

	@Override
	public Node visit(DecimalNode node) throws SimplificationException {
		return node;
	}

	@Override
	public Node visit(DivideNode node) throws SimplificationException {
		return simplifyBinaryOperation(node);
	}

	@Override
	public Node visit(EqNode node) throws SimplificationException {
		return simplifyBinaryOperation(node);
	}

	@Override
	public Node visit(FactorNode node) throws SimplificationException {
		Node argument = node.getArgument();

		// Vereenvoudig de nodes indien mogelijk.
		argument = argument.accept(this); node.setArgument(argument);

		return argument;
	}

	@Override
	public Node visit(FunctionNode node) throws SimplificationException {
		return node;
	}

	@Override
	public Node visit(GeqNode node) throws SimplificationException {
		return simplifyBinaryOperation(node);
	}

	@Override
	public Node visit(GtNode node) throws SimplificationException {
		return simplifyBinaryOperation(node);
	}

	@Override
	public Node visit(IfNode node) throws SimplificationException {
		Node condition = node.getCondition();
		Node ifBody = node.getIfBody();
		Node elseBody = node.getElseBody();

		// Vereenvoudig de nodes indien mogelijk.
		condition = condition.accept(this); node.setCondition(condition);
		ifBody = ifBody.accept(this); node.setIfBody(ifBody);
		elseBody = elseBody.accept(this); node.setElseBody(elseBody);

		// Wanneer we conditie niet constant is kunnen we niets vereenvoudigen. Geef de node terug.
		if (!condition.getIsConstant()) {
			return node;
		}

		Boolean conditionValue = (Boolean)evaluateConstantNode(condition);
		if (conditionValue == null) {
			return createNodeForTypeAndValue(node.getType(), null, node.getToken());
		}

		if (conditionValue.booleanValue()) {
			return determineResultNodeForIf(ifBody, node.getType());
		} else {
			return determineResultNodeForIf(elseBody, node.getType());
		}
	}

	@Override
	public Node visit(IntegerNode node) throws SimplificationException {
		return node;
	}

	@Override
	public Node visit(LeqNode node) throws SimplificationException {
		return simplifyBinaryOperation(node);
	}

	@Override
	public Node visit(LtNode node) throws SimplificationException {
		return simplifyBinaryOperation(node);
	}

	@Override
	public Node visit(ModuloNode node) throws SimplificationException {
		return simplifyBinaryOperation(node);
	}

	@Override
	public Node visit(MultiplyNode node) throws SimplificationException {
		return simplifyBinaryOperation(node);
	}

	@Override
	public Node visit(NegativeNode node) throws SimplificationException {
		Node argument = node.getArgument();

		// Vereenvoudig de nodes indien mogelijk.
		argument = argument.accept(this); node.setArgument(argument);

		// Wanneer het argument niet constant is kunnen we de node niet vereenvoudigen.
		if (!argument.getIsConstant()) {
			return node;
		}

		// Het argument is constant, evalueer het.
		Number argumentValue = (Number)evaluateConstantNode(node);

		return createNodeForTypeAndValue(argument.getType(), argumentValue, node.getToken());
	}

	@Override
	public Node visit(NeqNode node) throws SimplificationException {
		return simplifyBinaryOperation(node);
	}

	@Override
	public Node visit(NotNode node) throws SimplificationException {
		Node argument = node.getArgument();

		// Vereenvoudig de nodes indien mogelijk.
		argument = argument.accept(this); node.setArgument(argument);

		// Wanneer het argument niet constant is kunnen we de node niet vereenvoudigen.
		if (!argument.getIsConstant()) {
			return node;
		}

		// Het argument is constant, evalueer het en geef een BooleanNode terug.
		return nodeFactory.createBooleanNode((Boolean) evaluateConstantNode(node), node.getToken());
	}

	@Override
	public Node visit(NullNode node) throws SimplificationException {
		return node;
	}

	@Override
	public Node visit(OrNode node) throws SimplificationException {
		Node lhs = node.getLhs();
		Node rhs = node.getRhs();

		// Vereenvoudig de nodes indien mogelijk.
		lhs = lhs.accept(this); node.setLhs(lhs);
		rhs = rhs.accept(this); node.setRhs(rhs);

		// Wanneer beide zijden constant zijn kunnen we de node vereenvoudigen.
		if (lhs.getIsConstant() && rhs.getIsConstant()) {
			return nodeFactory.createBooleanNode((Boolean)evaluateConstantNode(node), node.getToken());
		}

		// Wanneer links constant is (dan is rechts dat niet), bepaal dan de waarde van links.
		if (lhs.getIsConstant()) {
			Boolean left = (Boolean)evaluateConstantNode(lhs);
			// Wanneer links null is, vervang dan de linkerkant door een BooleanNode met waarde null.
			if (left == null) {
				node.setLhs(nodeFactory.createBooleanNode(null, lhs.getToken()));
				return node;
			}

			// Wanneer links naar true evalueert, evalueert de hele expressie naar true en kunnen we de node
			// vereenvoudigen.
			if (left.booleanValue()) {
				return nodeFactory.createBooleanNode(Boolean.TRUE, node.getToken());
			}

			// Wanneer we hier komen, is links false. Daarom evalueert de expressie naar de waarde van de rechter
			// kant.
			return rhs;
		}

		// Wanneer rechts constant is (dan is links dat niet), bepaal dan de waarde van rechts.
		if (rhs.getIsConstant()) {
			Boolean right = (Boolean)evaluateConstantNode(rhs);
			// Wanneer rechts null is, vervang dan de rechterkant door een BooleanNode met waarde null.
			if (right == null) {
				node.setRhs(nodeFactory.createBooleanNode(null, rhs.getToken()));
				return node;
			}

			// Wanneer rechts naar true evalueert, evalueert de hele expressie naar true en kunnen we de node
			// vereenvoudigen.
			if (right.booleanValue()) {
				return nodeFactory.createBooleanNode(Boolean.TRUE, node.getToken());
			}

			// Wanneer we hier komen, is rechts false. Daarom evalueert de expressie naar de waarde van de linker
			// kant.
			return lhs;
		}

		// Geef de huidige instantie terug.
		return node;
	}

	@Override
	public Node visit(PositiveNode node) throws SimplificationException {
		Node argument = node.getArgument();

		// Vereenvoudig de nodes indien mogelijk.
		argument = argument.accept(this); node.setArgument(argument);

		return argument;
	}

	@Override
	public Node visit(PowerNode node) throws SimplificationException {
		return simplifyBinaryOperation(node);
	}

	@Override
	public Node visit(StatementListNode node) throws SimplificationException {
		// Loop over de lijst heen om alle nodes in de lijst te analyseren.
		for (int i = 0; i < node.size(); i++) {
			// LET OP! De nodeList kan alleen objecten bevatten van het type T of een type dat T extends. Dit betekent
			// dat we ervoor moeten zorgen dat wanneer we een node van het type T analyseren ook weer een node van dit
			// type terug krijgen.
			node.set(i, node.get(i).accept(this));
		}

		// Geef altijd de huidige node terug.
		return node;
	}

	@Override
	public Node visit(StringNode node) throws SimplificationException {
		return node;
	}

	@Override
	public Node visit(SubstractNode node) throws SimplificationException {
		return simplifyBinaryOperation(node);
	}

	@Override
	public Node visit(VariableNode node) throws SimplificationException {
		return node;
	}

	protected Node simplifyBinaryOperation(BinaryOperationNode node) throws SimplificationException {
		Node lhs = node.getLhs();
		Node rhs = node.getRhs();

		// Vereenvoudig de nodes indien mogelijk.
		lhs = lhs.accept(this); node.setLhs(lhs);
		rhs = rhs.accept(this); node.setRhs(rhs);

		// Wanneer beide zijden constant zijn kunnen we de node vereenvoudigen.
		if (lhs.getIsConstant() && rhs.getIsConstant()) {
			// Wanneer we hier komen zijn beide zijden constant. Vereenvoudig de node.
			Object value = evaluateConstantNode(node);

			return createNodeForTypeAndValue(node.getType(), value, node.getToken());
		}

		// Wanneer we hier komen is tenminste een van beide zijden niet constant.

		// Wanneer een van de zijden constant is EN null, geven we een node met de waarde null terug.
		if ((lhs.getIsConstant() && evaluateConstantNode(lhs) == null)
				|| (rhs.getIsConstant() && evaluateConstantNode(rhs) == null)) {

			return createNodeForTypeAndValue(node.getType(), null, node.getToken());
		}

		// Wanneer we hier komen kunnen we de node niet verder vereenvoudigen. Geef de node terug.
		return node;
	}

	/**
	 * Creates a node for the given type with the given value.
	 * @param type The type of the node.
	 * @param value The value of the node.
	 * @param token The token of the node.
	 * @return The new node.
	 */
	protected Node createNodeForTypeAndValue(Type type, Object value, Token token) {
		if (type == null) {
			return nodeFactory.createNullNode(token);
		}

		switch (type) {
			case STRING: return nodeFactory.createStringNode((String) value, token);
			case INTEGER: return nodeFactory.createIntegerNode((BigInteger)value, token);
			case DECIMAL: return nodeFactory.createDecimalNode((BigDecimal)value, token);
			case BOOLEAN: return nodeFactory.createBooleanNode((Boolean)value, token);
			case DATE: return nodeFactory.createDateNode((Date)value, token);
		}

		throw new IllegalStateException("Unknown type: " + type.toString());
	}

	/**
	 * Determines the result node for an if.
	 * @param node The node with the result body of an if.
	 * @param type The type of the if.
	 * @return the result node for an if
	 */
	protected Node determineResultNodeForIf(Node node, Type type) {
		if (node instanceof NullNode && type != null) {
			return createNodeForTypeAndValue(type, null, node.getToken());
		}

		if (node instanceof IntegerNode && type == Type.DECIMAL) {
			BigDecimal value = new BigDecimal(((IntegerNode) node).getValue(), abacusContext.getMathContext());
			return createNodeForTypeAndValue(type, value, node.getToken());
		}

		return node;
	}

	/**
	 *
	 * @return
	 */
	protected Object evaluateConstantNode(Node node) throws SimplificationException {
		try {
			return evaluator.evaluate(node);
		} catch (EvaluationException e) {
			throw new SimplificationException(e.getMessage(), node);
		}
	}
}
