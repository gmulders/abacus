package org.gertje.abacus.nodevisitors;

import org.gertje.abacus.context.AbacusContext;
import org.gertje.abacus.lexer.AbacusLexer;
import org.gertje.abacus.lexer.Lexer;
import org.gertje.abacus.nodes.AbacusNodeFactory;
import org.gertje.abacus.nodes.AbstractExpressionNode;
import org.gertje.abacus.nodes.AddNode;
import org.gertje.abacus.nodes.AndNode;
import org.gertje.abacus.nodes.AssignmentNode;
import org.gertje.abacus.nodes.BooleanNode;
import org.gertje.abacus.nodes.DateNode;
import org.gertje.abacus.nodes.DecimalNode;
import org.gertje.abacus.nodes.DivideNode;
import org.gertje.abacus.nodes.EqNode;
import org.gertje.abacus.nodes.ExpressionNode;
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
import org.gertje.abacus.nodes.NotNode;
import org.gertje.abacus.nodes.NullNode;
import org.gertje.abacus.nodes.OrNode;
import org.gertje.abacus.nodes.PositiveNode;
import org.gertje.abacus.nodes.PowerNode;
import org.gertje.abacus.nodes.StatementListNode;
import org.gertje.abacus.nodes.StringNode;
import org.gertje.abacus.nodes.SubstractNode;
import org.gertje.abacus.nodes.VariableNode;
import org.gertje.abacus.parser.Parser;
import org.gertje.abacus.parser.ParserException;
import org.gertje.abacus.symboltable.IllegalTypeException;
import org.gertje.abacus.symboltable.NoSuchFunctionException;
import org.gertje.abacus.symboltable.NoSuchVariableException;
import org.gertje.abacus.symboltable.SymbolTable;
import org.gertje.abacus.types.Type;
import org.gertje.abacus.util.CastHelper;
import org.gertje.abacus.util.EvaluationHelper;
import org.gertje.abacus.util.SemanticsHelper;

import java.util.ArrayList;
import java.util.List;

public class Interpreter extends AbstractExpressionNodeVisitor<Object, InterpreterException> {

	/**
	 * De context waarbinnen de interpreter werkt.
	 */
	private AbacusContext abacusContext;

	/**
	 * De symboltable met de variabelen en de functies.
	 */
	private SymbolTable symbolTable;

	/**
	 * Het return type van de expressie.
	 */
	private Type returnType;

	/**
	 * Constructor.
	 */
	public Interpreter(AbacusContext abacusContext) {
		this.abacusContext = abacusContext;
		symbolTable = abacusContext.getSymbolTable();
	}

	/**
	 * Interpreteert de expressie uit de node.
	 * @param node De node met de AST die de expressie voorstelt.
	 * @return Het resultaat van het interpreteren van de expressie.
	 * @throws InterpreterException Wanneer er een fout optreedt tijdens het interpreteren van de expressie.
	 */
	public Object interpret(ExpressionNode node) throws InterpreterException {
		Object value = node.accept(this);
		returnType = node.getType();

		return value;
	}

	/**
	 * Interpreteert de expressie uit de String. Hiervoor bouwt de methode eerst een AST op uit de String.
	 * @param expression De expressie die geinterpreteert moet worden.
	 * @return Het resultaat van het interpreteren van de expressie.
	 * @throws ParserException Wanneer een fout optreedt tijdens het compileren van de expressie.
	 * @throws InterpreterException Wanneer er een fout optreedt tijdens het interpreteren van de expressie.
	 */
	public Object interpret(String expression) throws ParserException, InterpreterException {
		// Maak een lexer om de expressie te lexen.
		Lexer lexer = new AbacusLexer(expression);
		// Maak een parser die de expressie parst.
		Parser parser = new Parser(lexer, new AbacusNodeFactory());
		// Bouw de AST op.
		AbstractExpressionNode tree = parser.parse();

		// Interpreteer de AST.
		return interpret(tree);
	}

	@Override
	public Object visit(AddNode node) throws InterpreterException {
		ExpressionNode lhs = node.getLhs();
		ExpressionNode rhs = node.getRhs();

		Object left = lhs.accept(this);
		Object right = rhs.accept(this);

		// Wanneer niet beide zijden van het type 'String' of 'Number' zijn moeten we een exceptie gooien.
		if (!(Type.isStringOrUnknown(lhs.getType()) && Type.isStringOrUnknown(rhs.getType()))
				&& !(Type.isNumberOrUnknown(lhs.getType()) && Type.isNumberOrUnknown(rhs.getType()))) {
			throw new InterpreterException(SemanticsHelper.ADD_ILLEGAL_OPERAND_TYPES, node);
		}

		return EvaluationHelper.add(left, lhs.getType(), right, rhs.getType());
	}

	@Override
	public Object visit(AndNode node) throws InterpreterException {
		ExpressionNode lhs = node.getLhs();
		ExpressionNode rhs = node.getRhs();

		Object left = lhs.accept(this);

		// Links moet van het type boolean of onbekend zijn.
		if (!Type.isBooleanOrUnknown(lhs.getType())) {
			throw new InterpreterException(SemanticsHelper.AND_ILLEGAL_OPERAND_TYPES, node);
		}

		// Wanneer links false is, is het resultaat van de operatie false.
		if (left != null && !((Boolean)left).booleanValue()) {
			return Boolean.FALSE;
		}

		Object right = rhs.accept(this);

		// Rechts moet van het type boolean of onbekend zijn.
		if (!Type.isBooleanOrUnknown(rhs.getType())) {
			throw new InterpreterException(SemanticsHelper.AND_ILLEGAL_OPERAND_TYPES, node);
		}

		// Wanneer rechts false is, is het resultaat van de operatie false.
		if (right != null && !((Boolean)right).booleanValue()) {
			return Boolean.FALSE;
		}

		// Geen van beide zijden is false, wanneer tenminste 1 van beide zijden null is, is het resultaat van de
		// operatie null.
		if (left == null || right == null) {
			return null;
		}

		// Geen van beide zijden is false of null, dus het resultaat van de operatie is true.
		return Boolean.TRUE;
	}

	@Override
	public Object visit(AssignmentNode node) throws InterpreterException {
		ExpressionNode lhs = node.getLhs();
		ExpressionNode rhs = node.getRhs();

		// Evalueer de rechterkant van de toekenning.
		Object result = rhs.accept(this);

		// Als de linkerkant geen VariabeleNode is EN de linkerkant is geen AssignmentNode met aan de rechterkant een
		// VariableNode, dan gooien we een exceptie.
		if (!(lhs instanceof VariableNode)
				&& !((lhs instanceof AssignmentNode) && (((AssignmentNode)lhs).getRhs() instanceof VariableNode))) {
			throw new InterpreterException(SemanticsHelper.ASSIGNMENT_ILLEGAL_LEFT_OPERAND, node);
		}

		VariableNode variableNode;
		// Zet het resultaat in de symboltable.
		// Wanneer de linkerkant een variabele is kunnen we het direct in de variabele zetten, anders moeten we eerst
		// de variabele uit de rechterkant halen.
		if (lhs instanceof VariableNode) {
			variableNode = (VariableNode)lhs;
		} else {
			variableNode = (VariableNode)((AssignmentNode)lhs).getRhs();
		}

		// Visit the variable node, but ignore the return value. This way me make sure the type of the variable is
		// correctly set on the node.
		variableNode.accept(this);

		try {
			symbolTable.setVariableValue(variableNode.getIdentifier(), rhs.getType(), result);
		} catch (IllegalTypeException e) {
			throw new InterpreterException("Could not set the variable value.", node, e);
		}

		// Geef het resultaat terug.
		return CastHelper.castValue(result, rhs.getType(), variableNode.getType());
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
	public Object visit(DecimalNode node) throws InterpreterException {
		return node.getValue();
	}

	@Override
	public Object visit(DivideNode node) throws InterpreterException {
		ExpressionNode lhs = node.getLhs();
		ExpressionNode rhs = node.getRhs();

		Object left = lhs.accept(this);
		Object right = rhs.accept(this);

		// Beide zijden moeten van het type 'number', of een onbekend type zijn.
		if (!Type.isNumberOrUnknown(lhs.getType()) || !Type.isNumberOrUnknown(rhs.getType())) {
			throw new InterpreterException(SemanticsHelper.DIVIDE_ILLEGAL_OPERAND_TYPES, node);
		}

		return EvaluationHelper.divide((Number)left, lhs.getType(), (Number)right, rhs.getType(), abacusContext.getMathContext());
	}

	@Override
	public Object visit(EqNode node) throws InterpreterException {
		ExpressionNode lhs = node.getLhs();
		ExpressionNode rhs = node.getRhs();

		Object left = lhs.accept(this);
		Object right = rhs.accept(this);

		// Controleer of de types voorkomen in de lijst.
		if (!SemanticsHelper.checkComparisonTypes(lhs.getType(), rhs.getType(), SemanticsHelper.ALLOWED_TYPES_EQ_NEQ)) {
			throw new InterpreterException(SemanticsHelper.EQ_ILLEGAL_OPERAND_TYPES, node);
		}

		return EvaluationHelper.eq(left, lhs.getType(), right, rhs.getType());
	}

	@Override
	public Object visit(FactorNode node) throws InterpreterException {
		return node.getArgument().accept(this);
	}

	@Override
	public Object visit(FunctionNode node) throws InterpreterException {
		List<ExpressionNode> parameters = node.getParameters();
		String identifier = node.getIdentifier();

		// Maak een lijst met alle resultaten van de evaluatie van de parameters.
		List<Object> paramResults = new ArrayList<>();

		// Maak een lijst met alle types van de parameters.
		List<Type> paramTypes = new ArrayList<>();

		// Loop over alle nodes heen en vul de lijsten met de geevaluuerde waarde en het type.
		for (ExpressionNode parameter : parameters) {
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
		ExpressionNode lhs = node.getLhs();
		ExpressionNode rhs = node.getRhs();

		Object left = lhs.accept(this);
		Object right = rhs.accept(this);

		// Controleer of de types voorkomen in de lijst.
		if (!SemanticsHelper.checkComparisonTypes(lhs.getType(), rhs.getType(),
				SemanticsHelper.ALLOWED_TYPES_GEQ_GT_LEQ_LT)) {
			throw new InterpreterException(SemanticsHelper.GEQ_ILLEGAL_OPERAND_TYPES, node);
		}

		return EvaluationHelper.geq(left, lhs.getType(), right, rhs.getType());
	}

	@Override
	public Object visit(GtNode node) throws InterpreterException {
		ExpressionNode lhs = node.getLhs();
		ExpressionNode rhs = node.getRhs();

		Object left = lhs.accept(this);
		Object right = rhs.accept(this);

		// Controleer of de types voorkomen in de lijst.
		if (!SemanticsHelper.checkComparisonTypes(lhs.getType(), rhs.getType(),
				SemanticsHelper.ALLOWED_TYPES_GEQ_GT_LEQ_LT)) {
			throw new InterpreterException(SemanticsHelper.GT_ILLEGAL_OPERAND_TYPES, node);
		}

		return EvaluationHelper.gt(left, lhs.getType(), right, rhs.getType());
	}

	@Override
	public Object visit(IfNode node) throws InterpreterException {
		// Deze methode wijkt iets af van de standaard werking. We controleren namelijk niet of de types van de twee
		// bodies gelijk is.
		ExpressionNode condition = node.getCondition();
		ExpressionNode ifBody = node.getIfBody();
		ExpressionNode elseBody = node.getElseBody();

		// Evauleer de conditie.
		Object cond = condition.accept(this);

		// De waarde van de conditie moet van het type 'boolean' of van een onbekend type zijn.
		if (!Type.isBooleanOrUnknown(condition.getType())) {
			throw new InterpreterException(SemanticsHelper.IF_ILLEGAL_CONDITION_TYPE, node);
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
		ExpressionNode lhs = node.getLhs();
		ExpressionNode rhs = node.getRhs();

		Object left = lhs.accept(this);
		Object right = rhs.accept(this);

		// Controleer of de types voorkomen in de lijst.
		if (!SemanticsHelper.checkComparisonTypes(lhs.getType(), rhs.getType(),
				SemanticsHelper.ALLOWED_TYPES_GEQ_GT_LEQ_LT)) {
			throw new InterpreterException(SemanticsHelper.LEQ_ILLEGAL_OPERAND_TYPES, node);
		}

		return EvaluationHelper.leq(left, lhs.getType(), right, rhs.getType());
	}

	@Override
	public Object visit(LtNode node) throws InterpreterException {
		ExpressionNode lhs = node.getLhs();
		ExpressionNode rhs = node.getRhs();

		Object left = lhs.accept(this);
		Object right = rhs.accept(this);

		// Controleer of de types voorkomen in de lijst.
		if (!SemanticsHelper.checkComparisonTypes(lhs.getType(), rhs.getType(),
				SemanticsHelper.ALLOWED_TYPES_GEQ_GT_LEQ_LT)) {
			throw new InterpreterException(SemanticsHelper.LT_ILLEGAL_OPERAND_TYPES, node);
		}

		return EvaluationHelper.lt(left, lhs.getType(), right, rhs.getType());
	}

	@Override
	public Object visit(ModuloNode node) throws InterpreterException {
		ExpressionNode lhs = node.getLhs();
		ExpressionNode rhs = node.getRhs();

		Object left = lhs.accept(this);
		Object right = rhs.accept(this);

		// Beide zijden moeten van het type 'number', of een onbekend type zijn.
		if (!Type.isNumberOrUnknown(lhs.getType()) || !Type.isNumberOrUnknown(rhs.getType())) {
			throw new InterpreterException(SemanticsHelper.MODULO_ILLEGAL_OPERAND_TYPES, node);
		}

		return EvaluationHelper.modulo((Number)left, lhs.getType(), (Number)right, rhs.getType());
	}

	@Override
	public Object visit(MultiplyNode node) throws InterpreterException {
		ExpressionNode lhs = node.getLhs();
		ExpressionNode rhs = node.getRhs();

		Object left = lhs.accept(this);
		Object right = rhs.accept(this);

		// Beide zijden moeten van het type 'number', of een onbekend type zijn.
		if (!Type.isNumberOrUnknown(lhs.getType()) || !Type.isNumberOrUnknown(rhs.getType())) {
			throw new InterpreterException(SemanticsHelper.MULTIPLY_ILLEGAL_OPERAND_TYPES, node);
		}

		return EvaluationHelper.multiply((Number)left, lhs.getType(), (Number)right, rhs.getType());
	}

	@Override
	public Object visit(NegativeNode node) throws InterpreterException {
		ExpressionNode argument = node.getArgument();

		// Bepaal het getal dat we negatief gaan maken.
		Object number = argument.accept(this);

		// Het argument moet een getal of onbekend zijn.
		if (!Type.isNumberOrUnknown(argument.getType())) {
			throw new InterpreterException(SemanticsHelper.NEGATIVE_ILLEGAL_OPERAND_TYPE, node);
		}

		return EvaluationHelper.negative((Number)number, argument.getType());
	}

	@Override
	public Object visit(NeqNode node) throws InterpreterException {
		ExpressionNode lhs = node.getLhs();
		ExpressionNode rhs = node.getRhs();

		Object left = lhs.accept(this);
		Object right = rhs.accept(this);

		// Controleer of de types voorkomen in de lijst.
		if (!SemanticsHelper.checkComparisonTypes(lhs.getType(), rhs.getType(), SemanticsHelper.ALLOWED_TYPES_EQ_NEQ)) {
			throw new InterpreterException(SemanticsHelper.NEQ_ILLEGAL_OPERAND_TYPES, node);
		}

		return EvaluationHelper.neq(left, lhs.getType(), right, rhs.getType());
	}

	@Override
	public Object visit(NotNode node) throws InterpreterException {
		ExpressionNode argument = node.getArgument();

		// Bepaal de waarde van de boolean.
		Object bool = argument.accept(this);

		// Het argument moet een boolean zijn.
		if (!Type.isBooleanOrUnknown(argument.getType())) {
			throw new InterpreterException(SemanticsHelper.NOT_ILLEGAL_OPERAND_TYPE, node);
		}

		return EvaluationHelper.not((Boolean)bool);
	}

	@Override
	public Object visit(NullNode node) throws InterpreterException {
		return null;
	}

	@Override
	public Object visit(OrNode node) throws InterpreterException {
		ExpressionNode lhs = node.getLhs();
		ExpressionNode rhs = node.getRhs();

		Object left = lhs.accept(this);

		// Links moet van het type boolean of onbekend zijn.
		if (!Type.isBooleanOrUnknown(lhs.getType())) {
			throw new InterpreterException(SemanticsHelper.OR_ILLEGAL_OPERAND_TYPES, node);
		}

		// Wanneer links true is, is het resultaat van de operatie true.
		if (left != null && ((Boolean)left).booleanValue()) {
			return Boolean.TRUE;
		}

		Object right = rhs.accept(this);

		// Rechts moet van het type boolean of onbekend zijn.
		if (!Type.isBooleanOrUnknown(rhs.getType())) {
			throw new InterpreterException(SemanticsHelper.OR_ILLEGAL_OPERAND_TYPES, node);
		}

		// Wanneer rechts true is, is het resultaat van de operatie true.
		if (right != null && ((Boolean)right).booleanValue()) {
			return Boolean.TRUE;
		}

		// Geen van beide zijden is true, wanneer tenminste 1 van beide zijden null is, is het resultaat van de
		// operatie null.
		if (left == null || right == null) {
			return null;
		}

		// Geen van beide zijden is true of null, dus het resultaat van de operatie is false.
		return Boolean.FALSE;
	}

	@Override
	public Object visit(PositiveNode node) throws InterpreterException {
		ExpressionNode argument = node.getArgument();
		Object pos = argument.accept(this);

		// Het argument moet een 'number' of onbekend zijn.
		if (!Type.isNumberOrUnknown(argument.getType())) {
			throw new InterpreterException(SemanticsHelper.POSITIVE_ILLEGAL_OPERAND_TYPE, node);
		}

		return pos;
	}

	@Override
	public Object visit(PowerNode node) throws InterpreterException {
		ExpressionNode base = node.getBase();
		ExpressionNode power = node.getPower();

		Object baseValue = base.accept(this);
		Object powerValue = power.accept(this);

		// Beide zijden moeten van het type 'number' of onbekend zijn.
		if (!Type.isNumberOrUnknown(base.getType()) || !Type.isNumberOrUnknown(power.getType())) {
			throw new InterpreterException(SemanticsHelper.POWER_ILLEGAL_OPERAND_TYPES, node);
		}

		return EvaluationHelper.power((Number)baseValue, base.getType(), (Number)powerValue, power.getType());
	}

	@Override
	public Object visit(StatementListNode node) throws InterpreterException {
		// Evalueer alle AbstractNodes en geef het resultaat van de laatste node terug.
		Object result = null;
		for (Node subNode : node) {
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
		ExpressionNode lhs = node.getLhs();
		ExpressionNode rhs = node.getRhs();

		Object left = lhs.accept(this);
		Object right = rhs.accept(this);

		// Beide zijden moeten van het type 'number' of onbekend zijn.
		if (!Type.isNumberOrUnknown(rhs.getType()) || !Type.isNumberOrUnknown(lhs.getType())) {
			throw new InterpreterException(SemanticsHelper.SUBSTRACT_ILLEGAL_OPERAND_TYPES, node);
		}

		return EvaluationHelper.substract((Number)left, lhs.getType(), (Number)right, rhs.getType());
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

	public Type getReturnType() {
		return returnType;
	}
}
