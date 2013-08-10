package org.gertje.abacus.nodevisitors;

import org.gertje.abacus.nodes.*;
import org.gertje.abacus.symboltable.SymbolTable;

import java.math.BigDecimal;
import java.math.BigInteger;

public class Simplifier extends AbstractNodeVisitor<AbstractNode, SimplificationException> {

	/**
	 * De symboltable met de variabelen en de functies.
	 */
	private SymbolTable symbolTable;

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
	public Simplifier(SymbolTable symbolTable, NodeFactory nodeFactory) {
		this.symbolTable = symbolTable;
		this.nodeFactory = nodeFactory;

		// Maak een evaluator aan om de nodes te vereenvoudigen.
		evaluator = new Evaluator(symbolTable);
	}

	public AbstractNode simplify(AbstractNode node) throws SimplificationException {
		return node.accept(this);
	}

	@Override
	public AbstractNode visit(AddNode node) throws SimplificationException {
		AbstractNode lhs = node.getLhs();
		AbstractNode rhs = node.getRhs();

		// Vereenvoudig de nodes indien mogelijk.
		lhs = lhs.accept(this); node.setLhs(lhs);
		rhs = rhs.accept(this); node.setRhs(rhs);

		// Wanneer een van beide zijden niet constant is kunnen we de node niet verder vereenvoudigen. Geef de huidige
		// instantie terug.
		if (!lhs.getIsConstant() || !rhs.getIsConstant()) {
			return node;
		}

		// Wanneer we hier komen zijn beide zijden constant. Vereenvoudig de node.
		Object result;
		try {
			result = evaluator.evaluate(node);
		} catch (EvaluationException e) {
			throw new SimplificationException(e.getMessage(), node);
		}

		if (node.getType().equals(BigDecimal.class)) {
			return nodeFactory.createFloatNode((BigDecimal) result, node.getToken());
		} else if (node.getType().equals(BigInteger.class)) {
			return nodeFactory.createIntegerNode((BigInteger) result, node.getToken());
		} else {
			return nodeFactory.createStringNode((String) result, node.getToken());
		}
	}

	@Override
	public AbstractNode visit(AndNode node) throws SimplificationException {
		AbstractNode lhs = node.getLhs();
		AbstractNode rhs = node.getRhs();

		// Vereenvoudig de nodes indien mogelijk.
		lhs = lhs.accept(this); node.setLhs(lhs);
		rhs = rhs.accept(this); node.setRhs(rhs);

		try {
			// Wanneer beide zijden constant zijn kunnen we de node vereenvoudigen.
			if (lhs.getIsConstant() && rhs.getIsConstant()) {
				return nodeFactory.createBooleanNode((Boolean)evaluator.evaluate(node), node.getToken());
			}

			// Wanneer slechts de linker zijde of de rechter zijde constant is en deze naar 'false' evalueert, evalueert
			// de hele expressie naar false en kunnen we de node vereenvoudigen.
			if (lhs.getIsConstant() && !((Boolean)evaluator.evaluate(lhs)).booleanValue()
					|| rhs.getIsConstant() && !((Boolean)evaluator.evaluate(rhs)).booleanValue()) {
				return nodeFactory.createBooleanNode(Boolean.FALSE, node.getToken());
			}

			// Wanneer slechts de linker zijde of de rechter zijde constant is en deze naar 'true' evalueert, evalueert
			// de huidige expressie naar de niet constante expressie.
			if (lhs.getIsConstant() && ((Boolean)evaluator.evaluate(lhs)).booleanValue()) {
				return rhs;
			} else if (rhs.getIsConstant() && ((Boolean)evaluator.evaluate(rhs)).booleanValue()) {
				return lhs;
			}
		} catch (EvaluationException e) {
			throw new SimplificationException(e.getMessage(), node);
		}

		return node;
	}

	@Override
	public AbstractNode visit(AssignmentNode node) throws SimplificationException {
		AbstractNode lhs = node.getLhs();
		AbstractNode rhs = node.getRhs();

		// Vereenvoudig de nodes indien mogelijk.
		lhs = lhs.accept(this); node.setLhs(lhs);
		rhs = rhs.accept(this); node.setRhs(rhs);

		// We kunnen deze node niet verder vereenvoudigen, geef de node terug.
		return node;
	}

	@Override
	public AbstractNode visit(BooleanNode node) throws SimplificationException {
		return node;
	}

	@Override
	public AbstractNode visit(DateNode node) throws SimplificationException {
		return node;
	}

	@Override
	public AbstractNode visit(DivideNode node) throws SimplificationException {
		return term(node);
	}

	@Override
	public AbstractNode visit(EqNode node) throws SimplificationException {
		return comparison(node);
	}

	@Override
	public AbstractNode visit(FactorNode node) throws SimplificationException {
		AbstractNode argument = node.getArgument();

		// Vereenvoudig de nodes indien mogelijk.
		argument = argument.accept(this); node.setArgument(argument);

		return argument;
	}

	@Override
	public AbstractNode visit(FloatNode node) throws SimplificationException {
		return node;
	}

	@Override
	public AbstractNode visit(FunctionNode node) throws SimplificationException {
		return node;
	}

	@Override
	public AbstractNode visit(GeqNode node) throws SimplificationException {
		return comparison(node);
	}

	@Override
	public AbstractNode visit(GtNode node) throws SimplificationException {
		return comparison(node);
	}

	@Override
	public AbstractNode visit(IfNode node) throws SimplificationException {
		AbstractNode condition = node.getCondition();
		AbstractNode ifBody = node.getIfBody();
		AbstractNode elseBody = node.getElseBody();

		// Vereenvoudig de nodes indien mogelijk.
		condition = condition.accept(this); node.setCondition(condition);
		ifBody = ifBody.accept(this); node.setIfBody(ifBody);
		elseBody = elseBody.accept(this); node.setElseBody(elseBody);

		// Wanneer we conditie niet constant is kunnen we niets vereenvoudigen. Geef de node terug.
		if (!condition.getIsConstant()) {
			return node;
		}

		try {
			if (Boolean.TRUE.equals(evaluator.evaluate(condition))) {
				return ifBody;
			} else {
				return elseBody;
			}
		} catch (EvaluationException e) {
			throw new SimplificationException(e.getMessage(), node);
		}
	}

	@Override
	public AbstractNode visit(IntegerNode node) throws SimplificationException {
		return node;
	}

	@Override
	public AbstractNode visit(LeqNode node) throws SimplificationException {
		return comparison(node);
	}

	@Override
	public AbstractNode visit(LtNode node) throws SimplificationException {
		return comparison(node);
	}

	@Override
	public AbstractNode visit(ModuloNode node) throws SimplificationException {
		return term(node);
	}

	@Override
	public AbstractNode visit(MultiplyNode node) throws SimplificationException {
		return term(node);
	}

	@Override
	public AbstractNode visit(NegativeNode node) throws SimplificationException {
		AbstractNode argument = node.getArgument();

		// Vereenvoudig de nodes indien mogelijk.
		argument = argument.accept(this); node.setArgument(argument);

		// Wanneer het argument niet constant is kunnen we de node niet vereenvoudigen.
		if (!argument.getIsConstant()) {
			return node;
		}

		// Het argument is constant, evalueer het.
		Number argumentValue;
		try {
			argumentValue = (Number) evaluator.evaluate(node);
		} catch (EvaluationException e) {
			throw new SimplificationException(e.getMessage(), node);
		}

		// Wanneer het argument een float is maken we een FloatNode aan.
   		if (argument.getType().equals(BigDecimal.class)) {
			return nodeFactory.createFloatNode((BigDecimal) argumentValue, node.getToken());
		}

		// Het argument is geen float, dus maken we een IntegerNode aan.
		return nodeFactory.createIntegerNode((BigInteger) argumentValue, node.getToken());
	}

	@Override
	public AbstractNode visit(NeqNode node) throws SimplificationException {
		return comparison(node);
	}

	@Override
	public AbstractNode visit(NotNode node) throws SimplificationException {
		AbstractNode argument = node.getArgument();

		// Vereenvoudig de nodes indien mogelijk.
		argument = argument.accept(this); node.setArgument(argument);

		// Wanneer het argument niet constant is kunnen we de node niet vereenvoudigen.
		if (!argument.getIsConstant()) {
			return node;
		}

		// Het argument is constant, evalueer het en geef een BooleanNode terug.
		try {
			return nodeFactory.createBooleanNode((Boolean) evaluator.evaluate(node), node.getToken());
		} catch (EvaluationException e) {
			throw new SimplificationException(e.getMessage(), node);
		}
	}

	@Override
	public AbstractNode visit(NullNode node) throws SimplificationException {
		return node;
	}

	@Override
	public AbstractNode visit(OrNode node) throws SimplificationException {
		AbstractNode lhs = node.getLhs();
		AbstractNode rhs = node.getRhs();

		// Vereenvoudig de nodes indien mogelijk.
		lhs = lhs.accept(this); node.setLhs(lhs);
		rhs = rhs.accept(this); node.setRhs(rhs);

		try {
			// Wanneer beide zijden constant zijn kunnen we de node vereenvoudigen.
			if (lhs.getIsConstant() && rhs.getIsConstant()) {
				return nodeFactory.createBooleanNode((Boolean)evaluator.evaluate(node), node.getToken());
			}

			// Wanneer slechts de linker zijde of de rechter zijde constant is en deze naar 'true' evalueert, evalueert de
			// hele expressie naar true en kunnen we de node vereenvoudigen.
			if (lhs.getIsConstant() && ((Boolean)evaluator.evaluate(lhs)).booleanValue()
					|| rhs.getIsConstant() && ((Boolean)evaluator.evaluate(rhs)).booleanValue()) {
				return nodeFactory.createBooleanNode(Boolean.TRUE, node.getToken());
			}

			// Wanneer slechts de linker zijde of de rechter zijde constant is en deze naar 'false' evalueert, evalueert de
			// huidige expressie naar de niet constante expressie.
			if (lhs.getIsConstant() && !((Boolean)evaluator.evaluate(lhs)).booleanValue()) {
				return rhs;
			} else if (rhs.getIsConstant() && !((Boolean)evaluator.evaluate(rhs)).booleanValue()) {
				return lhs;
			}
		} catch (EvaluationException e) {
			throw new SimplificationException(e.getMessage(), node);
		}

		// Geef de huidige instantie terug.
		return node;
	}

	@Override
	public AbstractNode visit(PositiveNode node) throws SimplificationException {
		AbstractNode argument = node.getArgument();

		// Vereenvoudig de nodes indien mogelijk.
		argument = argument.accept(this); node.setArgument(argument);

		return argument;
	}

	@Override
	public AbstractNode visit(PowerNode node) throws SimplificationException {
		AbstractNode base = node.getBase();
		AbstractNode power = node.getPower();

		// Vereenvoudig de nodes indien mogelijk.
		base.accept(this); node.setBase(base);
		power.accept(this); node.setPower(power);

		// Wanneer de basis of de macht niet constant is kunnen we de node niet verder vereenvoudigen. Geef de huidige
		// node terug.
		if (!base.getIsConstant() || !power.getIsConstant()) {
			return node;
		}

		// De basis en de macht zijn beide constant, we kunnen de node dus vereenvoudigen. Evalueer de expressie.
		try {
			return nodeFactory.createFloatNode((BigDecimal)evaluator.evaluate(node), node.getToken());
		} catch (EvaluationException e) {
			throw new SimplificationException(e.getMessage(), node);
		}
	}

	@Override
	public AbstractNode visit(StatementListNode node) throws SimplificationException {
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
	public AbstractNode visit(StringNode node) throws SimplificationException {
		return node;
	}

	@Override
	public AbstractNode visit(SubstractNode node) throws SimplificationException {
		AbstractNode lhs = node.getLhs();
		AbstractNode rhs = node.getRhs();

		// Vereenvoudig de nodes indien mogelijk.
		lhs = lhs.accept(this); node.setLhs(lhs);
		rhs = rhs.accept(this); node.setRhs(rhs);

		// Wanneer een van beide zijden niet constant is kunnen we de node niet verder vereenvoudigen. Geef de huidige
		// instantie terug.
		if (!lhs.getIsConstant() || !rhs.getIsConstant()) {
			return node;
		}

		// Wanneer we hier komen zijn beide zijden constant. Vereenvoudig de node.
		Object result;
		try {
			result = evaluator.evaluate(node);
		} catch (EvaluationException e) {
			throw new SimplificationException(e.getMessage(), node);
		}

		if (node.getType().equals(BigDecimal.class)) {
			return nodeFactory.createFloatNode((BigDecimal) result, node.getToken());
		} else {
			return nodeFactory.createIntegerNode((BigInteger) result, node.getToken());
		}
	}

	@Override
	public AbstractNode visit(VariableNode node) throws SimplificationException {
		return node;
	}

	protected AbstractNode term(AbstractTermNode node) throws SimplificationException {
		AbstractNode lhs = node.getLhs();
		AbstractNode rhs = node.getRhs();

		// Vereenvoudig de nodes indien mogelijk.
		lhs = lhs.accept(this); node.setLhs(lhs);
		rhs = rhs.accept(this); node.setRhs(rhs);

		// Wanneer links of rechts niet constant is kunnen we de node niet verder vereenvoudigen. Geef de huidige node
		// terug.
		if (!lhs.getIsConstant() || !rhs.getIsConstant()) {
			return node;
		}

		// Beide zijden zijn constant, we kunnen de node dus vereenvoudigen. Evalueer de expressie.
		Number value;
		try {
			value = (Number) evaluator.evaluate(node);
		} catch (EvaluationException e) {
			throw new SimplificationException(e.getMessage(), node);
		}
		
		// Wanneer de waarde een BigDecimal is, geven we een FloatNode terug.
		if (value instanceof BigDecimal) {
			return nodeFactory.createFloatNode((BigDecimal)value, node.getToken());
		}

		// De waarde is een BigInteger. Geef een IntegerNode terug.
		return nodeFactory.createIntegerNode((BigInteger)value, node.getToken());
	}

	protected AbstractNode comparison(AbstractComparisonNode node) throws SimplificationException {
		AbstractNode lhs = node.getLhs();
		AbstractNode rhs = node.getRhs();

		// Vereenvoudig de nodes indien mogelijk.
		lhs = lhs.accept(this); node.setLhs(lhs);
		rhs = rhs.accept(this); node.setRhs(rhs);

		// Wanneer links of rechts niet constant is kunnen we de node niet verder vereenvoudigen. Geef de huidige node
		// terug.
		if (!lhs.getIsConstant() || !rhs.getIsConstant()) {
			return node;
		}

		// Beide zijden zijn constant, we kunnen de node dus vereenvoudigen. Evalueer de expressie.
		try {
			return nodeFactory.createBooleanNode((Boolean)evaluator.evaluate(node), node.getToken());
		} catch (EvaluationException e) {
			throw new SimplificationException(e.getMessage(), node);
		}
	}
}
