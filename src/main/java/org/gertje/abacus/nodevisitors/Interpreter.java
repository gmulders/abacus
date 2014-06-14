package org.gertje.abacus.nodevisitors;

import org.gertje.abacus.nodes.AbstractNode;
import org.gertje.abacus.nodes.AddNode;
import org.gertje.abacus.nodes.AndNode;
import org.gertje.abacus.nodes.AssignmentNode;
import org.gertje.abacus.nodes.BooleanNode;
import org.gertje.abacus.nodes.DateNode;
import org.gertje.abacus.nodes.DivideNode;
import org.gertje.abacus.nodes.EqNode;
import org.gertje.abacus.nodes.FactorNode;
import org.gertje.abacus.nodes.FloatNode;
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
import org.gertje.abacus.nodes.NotNode;
import org.gertje.abacus.nodes.NullNode;
import org.gertje.abacus.nodes.OrNode;
import org.gertje.abacus.nodes.PositiveNode;
import org.gertje.abacus.nodes.PowerNode;
import org.gertje.abacus.nodes.StatementListNode;
import org.gertje.abacus.nodes.StringNode;
import org.gertje.abacus.nodes.SubstractNode;
import org.gertje.abacus.nodes.VariableNode;
import org.gertje.abacus.symboltable.NoSuchFunctionException;
import org.gertje.abacus.symboltable.NoSuchVariableException;
import org.gertje.abacus.symboltable.SymbolTable;
import org.gertje.abacus.util.EvaluationHelper;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

public class Interpreter extends AbstractNodeVisitor<Object, InterpreterException> {

	/**
	 * De symboltable met de variabelen en de functies.
	 */
	private SymbolTable symbolTable;

	/**
	 * Constructor.
	 */
	public Interpreter(SymbolTable symbolTable) {
		this.symbolTable = symbolTable;
	}

	public Object interpret(AbstractNode node) throws InterpreterException {
		return node.accept(this);
	}

	@Override
	public Object visit(AddNode node) throws InterpreterException {
		AbstractNode lhs = node.getLhs();
		AbstractNode rhs = node.getRhs();

		Object left = lhs.accept(this);
		Object right = rhs.accept(this);

		// Wanneer niet beide zijden van het type 'String' of 'Number' zijn moeten we een exceptie gooien.
		if (!(lhs.getType().equals(String.class) && rhs.getType().equals(String.class))
				&& !(isNumber(lhs.getType()) && isNumber(rhs.getType()))) {
			throw new InterpreterException(
					"Expected two parameters of type 'Number' or type 'String' to ADD-expression.",
					node);
		}

		return EvaluationHelper.add(left, right);
	}

	@Override
	public Object visit(AndNode node) throws InterpreterException {
		AbstractNode lhs = node.getLhs();
		AbstractNode rhs = node.getRhs();

		Object left = lhs.accept(this);

		// Links moet van het type boolean zijn.
		if (!lhs.getType().equals(Boolean.class)) {
			throw new InterpreterException("Expected two boolean parameters to AND-expression.", node);
		}

		// Wanneer links leeg is, is het resultaat van de operatie leeg.
		if (left == null) {
			return null;
		}

		// Wanneer links false is, is het resultaat van de operatie false.
		if (!((Boolean)left).booleanValue()) {
			return Boolean.FALSE;
		}

		Object right = rhs.accept(this);

		// Rechts moet van het type boolean zijn.
		if (!rhs.getType().equals(Boolean.class)) {
			throw new InterpreterException("Expected two boolean parameters to AND-expression.", node);
		}

		// Het resultaat van de operatie is nu gewoon de rechterkant (links is al true).
		return right;
	}

	@Override
	public Object visit(AssignmentNode node) throws InterpreterException {
		AbstractNode lhs = node.getLhs();
		AbstractNode rhs = node.getRhs();

		// Evalueer de rechterkant van de toekenning.
		Object result = rhs.accept(this);

		// Als de linkerkant geen VariabeleNode is EN de linkerkant is geen AssignmentNode met aan de rechterkant een
		// VariableNode, dan gooien we een exceptie.
		if (!(lhs instanceof VariableNode)
				&& !((lhs instanceof AssignmentNode) && (((AssignmentNode)lhs).getRhs() instanceof VariableNode))) {
			throw new InterpreterException("Left side of assignment should be a variable or an assignment.",
					node);
		}

		// Zet het resultaat in de symboltable.
		// Wanneer de linkerkant een variabele is kunnen we het direct in de variabele zetten, anders moeten we eerst
		// de variabele uit de rechterkant halen.
		if (lhs instanceof VariableNode) {
			symbolTable.setVariableValue(((VariableNode) lhs).getIdentifier(), result);
		} else {
			symbolTable.setVariableValue(((VariableNode) ((AssignmentNode)lhs).getRhs()).getIdentifier(), result);
		}

		// Geef het resultaat terug.
		return result;
	}

	@Override
	public Object visit(BooleanNode node) throws InterpreterException {
		return node.getValue();
	}

	@Override
	public Object visit(DateNode node) throws InterpreterException {
		return node.getValue();
	}

	@Override
	public Object visit(DivideNode node) throws InterpreterException {
		AbstractNode lhs = node.getLhs();
		AbstractNode rhs = node.getRhs();

		Object left = lhs.accept(this);
		Object right = rhs.accept(this);

		// Beide zijden moeten van het type 'number' zijn.
		if (!isNumber(lhs.getType()) || !isNumber(rhs.getType())) {
			throw new InterpreterException("Expected two parameters of type 'number' to divide-expression.",
					node);
		}

		return EvaluationHelper.divide((Number)left, (Number)right);
	}

	@Override
	public Object visit(EqNode node) throws InterpreterException {
		AbstractNode lhs = node.getLhs();
		AbstractNode rhs = node.getRhs();

		Object left = lhs.accept(this);
		Object right = rhs.accept(this);

		// Een == vergelijking kan booleans, getallen, strings en datums vergelijken.
		List<Class<?>> allowedTypes = new ArrayList<Class<?>>();
		allowedTypes.add(Boolean.class);
		allowedTypes.add(BigDecimal.class);
		allowedTypes.add(String.class);
		allowedTypes.add(Date.class);

		// Controleer of de types voorkomen in de lijst.
		if (!checkComparisonTypes(lhs.getType(), rhs.getType(), allowedTypes)) {
			throw new InterpreterException("Expected two parameters of the same type to comparison-expression.",
					node);
		}

		return EvaluationHelper.eq(left, right);
	}

	@Override
	public Object visit(FactorNode node) throws InterpreterException {
		return node.getArgument().accept(this);
	}

	@Override
	public Object visit(FloatNode node) throws InterpreterException {
		return node.getValue();
	}

	@Override
	public Object visit(FunctionNode node) throws InterpreterException {
		List<AbstractNode> parameters = node.getParameters();
		String identifier = node.getIdentifier();

		// Maak een lijst met alle resultaten van de evaluatie van de parameters.
		List<Object> paramResults = new ArrayList<Object>();

		// Maak een lijst met alle types van de parameters.
		List<Class<?>> paramTypes = new ArrayList<Class<?>>();

		// Loop over alle nodes heen en vul de lijsten met de geevaluuerde waarde en het type.
		for (AbstractNode parameter : parameters) {
			paramResults.add(parameter.accept(this));
			paramTypes.add(parameter.getType());
		}

		// Controleer of de variabele bestaat. Als deze niet bestaat gooien we een exceptie.
		if (!symbolTable.getExistsFunction(identifier, paramTypes)) {
			throw new InterpreterException("Function '" + identifier + "' does not exist.", node);
		}

		try {
			// Haal het type van de variabele op en zet deze op de node.
			// TODO: Moet dit wel hier gebeuren? Zie ook TODO hieronder.
			node.setReturnType(symbolTable.getFunctionReturnType(identifier, parameters));

			// Geef de return waarde terug.
			return symbolTable.getFunctionReturnValue(identifier, paramResults, paramTypes);
		} catch (NoSuchFunctionException e) {
			throw new InterpreterException(e.getMessage(), node);
		}
	}

	@Override
	public Object visit(GeqNode node) throws InterpreterException {
		AbstractNode lhs = node.getLhs();
		AbstractNode rhs = node.getRhs();

		Object left = lhs.accept(this);
		Object right = rhs.accept(this);

		// Een >= vergelijking kan getallen, strings en datums vergelijken.
		List<Class<?>> allowedTypes = new ArrayList<Class<?>>();
		allowedTypes.add(BigDecimal.class);
		allowedTypes.add(String.class);
		allowedTypes.add(Date.class);

		// Controleer of de types voorkomen in de lijst.
		if (!checkComparisonTypes(lhs.getType(), rhs.getType(), allowedTypes)) {
			throw new InterpreterException("Expected two parameters of the same type to comparison-expression.",
					node);
		}

		return EvaluationHelper.geq(left, right);
	}

	@Override
	public Object visit(GtNode node) throws InterpreterException {
		AbstractNode lhs = node.getLhs();
		AbstractNode rhs = node.getRhs();

		Object left = lhs.accept(this);
		Object right = rhs.accept(this);

		// Een > vergelijking kan getallen, strings en datums vergelijken.
		List<Class<?>> allowedTypes = new ArrayList<Class<?>>();
		allowedTypes.add(BigDecimal.class);
		allowedTypes.add(String.class);
		allowedTypes.add(Date.class);

		// Controleer of de types voorkomen in de lijst.
		if (!checkComparisonTypes(lhs.getType(), rhs.getType(), allowedTypes)) {
			throw new InterpreterException("Expected two parameters of the same type to comparison-expression.",
					node);
		}

		return EvaluationHelper.gt(left, right);
	}

	@Override
	public Object visit(IfNode node) throws InterpreterException {
		// Deze methode wijkt iets af van de standaard werking. We controleren namelijk niet of de types van de twee
		// bodies gelijk is.
		AbstractNode condition = node.getCondition();
		AbstractNode ifBody = node.getIfBody();
		AbstractNode elseBody = node.getElseBody();

		// Evauleer de conditie.
		Object cond = condition.accept(this);

		// De waarde van de conditie moet van het type 'boolean' zijn.
		if (!condition.getType().equals(Boolean.class)) {
			throw new InterpreterException("Expected boolean parameter to IF-expression.", node);
		}

		// Wanneer de conditie leeg is, dan is het resultaat van de if-conditie ook leeg.
		if (cond == null) {
			return null;
		}

		Object returnValue;

		// Wanneer de conditie waar was evalueren we de ifbody.
		if (((Boolean)cond).booleanValue()) {
			returnValue = ifBody.accept(this);
			node.setType(ifBody.getType());
		} else {
			returnValue = elseBody.accept(this);
			node.setType(elseBody.getType());
		}

		// Geef de bepaalde waarde terug.
		return returnValue;
	}

	@Override
	public Object visit(IntegerNode node) throws InterpreterException {
		return node.getValue();
	}

	@Override
	public Object visit(LeqNode node) throws InterpreterException {
		AbstractNode lhs = node.getLhs();
		AbstractNode rhs = node.getRhs();

		Object left = lhs.accept(this);
		Object right = rhs.accept(this);

		// Een <= vergelijking kan getallen, strings en datums vergelijken.
		List<Class<?>> allowedTypes = new ArrayList<Class<?>>();
		allowedTypes.add(BigDecimal.class);
		allowedTypes.add(String.class);
		allowedTypes.add(Date.class);

		// Controleer of de types voorkomen in de lijst.
		if (!checkComparisonTypes(lhs.getType(), rhs.getType(), allowedTypes)) {
			throw new InterpreterException(
					"Expected two parameters of the same type to less-then-equals-expression.",
					node);
		}

		return EvaluationHelper.leq(left, right);
	}

	@Override
	public Object visit(LtNode node) throws InterpreterException {
		AbstractNode lhs = node.getLhs();
		AbstractNode rhs = node.getRhs();

		Object left = lhs.accept(this);
		Object right = rhs.accept(this);

		// Een > vergelijking kan getallen, strings en datums vergelijken.
		List<Class<?>> allowedTypes = new ArrayList<Class<?>>();
		allowedTypes.add(BigDecimal.class);
		allowedTypes.add(String.class);
		allowedTypes.add(Date.class);

		// Controleer of de types voorkomen in de lijst.
		if (!checkComparisonTypes(lhs.getType(), rhs.getType(), allowedTypes)) {
			throw new InterpreterException("Expected two parameters of the same type to less-then-expression.",
					node);
		}

		return EvaluationHelper.lt(left, right);
	}

	@Override
	public Object visit(ModuloNode node) throws InterpreterException {
		AbstractNode lhs = node.getLhs();
		AbstractNode rhs = node.getRhs();

		Object left = lhs.accept(this);
		Object right = rhs.accept(this);

		// Beide zijden moeten van het type 'number' zijn.
		if (!isNumber(lhs.getType()) || !isNumber(rhs.getType())) {
			throw new InterpreterException("Expected two parameters of type 'number' to modulo-expression.", node);
		}

		return EvaluationHelper.modulo((Number)left, (Number)right);
	}

	@Override
	public Object visit(MultiplyNode node) throws InterpreterException {
		AbstractNode lhs = node.getLhs();
		AbstractNode rhs = node.getRhs();

		Object left = lhs.accept(this);
		Object right = rhs.accept(this);

		// Beide zijden moeten van het type 'number' zijn.
		if (!isNumber(lhs.getType()) || !isNumber(rhs.getType())) {
			throw new InterpreterException("Expected two parameters of type 'number' to multiply-expression.",
					node);
		}

		return EvaluationHelper.multiply((Number)left, (Number)right);
	}

	@Override
	public Object visit(NegativeNode node) throws InterpreterException {
		AbstractNode argument = node.getArgument();

		// Bepaal het getal dat we negatief gaan maken.
		Object number = argument.accept(this);

		// Het argument moet een getal zijn.
		if (!isNumber(argument.getType())) {
			throw new InterpreterException("Expected a number expression in NegativeNode.", node);
		}

		return EvaluationHelper.negative((Number)number);
	}

	@Override
	public Object visit(NeqNode node) throws InterpreterException {
		AbstractNode lhs = node.getLhs();
		AbstractNode rhs = node.getRhs();

		Object left = lhs.accept(this);
		Object right = rhs.accept(this);

		// Een == vergelijking kan booleans, getallen, strings en datums vergelijken.
		List<Class<?>> allowedTypes = new ArrayList<Class<?>>();
		allowedTypes.add(Boolean.class);
		allowedTypes.add(BigDecimal.class);
		allowedTypes.add(String.class);
		allowedTypes.add(Date.class);

		// Controleer of de types voorkomen in de lijst.
		if (!checkComparisonTypes(lhs.getType(), rhs.getType(), allowedTypes)) {
			throw new InterpreterException("Expected two parameters of the same type to not-equals-expression.",
					node);
		}

		return EvaluationHelper.neq(left, right);
	}

	@Override
	public Object visit(NotNode node) throws InterpreterException {
		AbstractNode argument = node.getArgument();

		// Bepaal de waarde van de boolean.
		Object bool = argument.accept(this);

		// Het argument moet een boolean zijn.
		if (!argument.getType().equals(Boolean.class)) {
			throw new InterpreterException("Expected a boolean expression in NotNode.", node);
		}

		return EvaluationHelper.not((Boolean)bool);
	}

	@Override
	public Object visit(NullNode node) throws InterpreterException {
		return null;
	}

	@Override
	public Object visit(OrNode node) throws InterpreterException {
		AbstractNode lhs = node.getLhs();
		AbstractNode rhs = node.getRhs();

		Object left = lhs.accept(this);

		// Links moet van het type Boolean zijn.
		if (!lhs.getType().equals(Boolean.class)) {
			throw new InterpreterException("Expected two boolean parameters to OR-expression.", node);
		}

		// Wanneer de linkerkant leeg is, is het resultaat van deze expressie ook leeg.
		if (left == null) {
			return null;
		}

		// Wanneer de linkerkant true is, is de expressie ook true.
		if (((Boolean)left).booleanValue()) {
			return Boolean.TRUE;
		}

		Object right = rhs.accept(this);

		// Rechts moet van het type Boolean zijn.
		if (!rhs.getType().equals(Boolean.class)) {
			throw new InterpreterException("Expected two boolean parameters to OR-expression.", node);
		}

		// Het resultaat van deze expressie is nu gewoon de rechterkant.
		return right;
	}

	@Override
	public Object visit(PositiveNode node) throws InterpreterException {
		AbstractNode argument = node.getArgument();
		Object pos = argument.accept(this);

		// Het argument moet een float of een integer zijn.
		if (!argument.getType().equals(BigDecimal.class) && !argument.getType().equals(BigInteger.class)) {
			throw new InterpreterException("Expected a number expression in PositiveNode.", node);
		}

		return pos;
	}

	@Override
	public Object visit(PowerNode node) throws InterpreterException {
		AbstractNode base = node.getBase();
		AbstractNode power = node.getPower();

		Object baseValue = base.accept(this);
		Object powerValue = power.accept(this);

		// Beide zijden moeten van het type 'number' zijn.
		if (!isNumber(base.getType()) || !isNumber(power.getType())) {
			throw new InterpreterException("Expected two parameters of type 'number' to POWER-expression.", node);
		}

		return EvaluationHelper.power((Number)baseValue, (Number)powerValue);
	}

	@Override
	public Object visit(StatementListNode node) throws InterpreterException {
		// Evalueer alle AbstractNodes en geef het resultaat van de laatste node terug.
		Object result = null;
		for (AbstractNode subNode : node) {
			result = subNode.accept(this);
		}

		return result;
	}

	@Override
	public Object visit(StringNode node) throws InterpreterException {
		return node.getValue();
	}

	@Override
	public Object visit(SubstractNode node) throws InterpreterException {
		AbstractNode lhs = node.getLhs();
		AbstractNode rhs = node.getRhs();

		Object left = lhs.accept(this);
		Object right = rhs.accept(this);

		// Beide zijden moeten van het type 'number' zijn.
		if (!isNumber(rhs.getType()) || !isNumber(lhs.getType())) {
			throw new InterpreterException("Expected two parameters of number type to SUBSTRACT-expression.", node);
		}

		return EvaluationHelper.substract((Number)left, (Number)right);
	}

	@Override
	public Object visit(VariableNode node) throws InterpreterException {
		String identifier = node.getIdentifier();

		// Controleer of de variabele bestaat. Als deze niet bestaat gooien we een exceptie.
		if (!symbolTable.getExistsVariable(identifier)) {
			throw new InterpreterException("Variable '" + identifier + "' does not exist.", node);
		}

		try {
			// Haal het type van de variabele op.
			// TODO: Moet dit wel hier gebeuren? Zie ook TODO hierboven.
			node.setType(symbolTable.getVariableType(identifier));
			return symbolTable.getVariableValue(identifier);
		} catch (NoSuchVariableException e) {
			throw new InterpreterException(e.getMessage(), node);
		}
	}

	/**
	 * Bepaalt of het meegegeven type een nummer is.
	 * @param type Het type waarvan de methode bepaalt of het een nummer is.
	 * @return <code>true</code> wanneer het meegegeven type een nummer is, anders <code>false</code>.
	 */
	protected boolean isNumber(Class<?> type) {
		return BigDecimal.class.equals(type) || BigInteger.class.equals(type);
	}

	/**
	 * Controleert de typen van de lhs en de rhs, wanneer beiden niet van het zelfde type zijn of ze komen niet voor in
	 * de lijst met toegestane typen geeft de methode false terug.
	 * @return <code>true</code> wanneer de typen goed zijn, anders <code>false</code>.
	 */
	protected boolean checkComparisonTypes(Class<?> lhsType, Class<?> rhsType, List<Class<?>> allowedTypes) {
		// We casten de BigInteger's naar BigDecimal's, omdat dit makkelijk te vergelijken is.
		if (lhsType.equals(BigInteger.class)) {
			lhsType = BigDecimal.class;
		}
		// We casten de BigInteger's naar BigDecimal's, omdat dit makkelijk te vergelijken is.
		if (rhsType.equals(BigInteger.class)) {
			rhsType = BigDecimal.class;
		}

		for(Class<?> type : allowedTypes) {
			if (lhsType.equals(type) && rhsType.equals(type)) {
				return true;
			}
		}
		return false;
	}
}
