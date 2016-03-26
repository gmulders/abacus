package org.gertje.abacus.nodevisitors;

import org.gertje.abacus.context.AbacusContext;
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
import org.gertje.abacus.nodes.NotNode;
import org.gertje.abacus.nodes.NullNode;
import org.gertje.abacus.nodes.OrNode;
import org.gertje.abacus.nodes.PositiveNode;
import org.gertje.abacus.nodes.PowerNode;
import org.gertje.abacus.nodes.StatementListNode;
import org.gertje.abacus.nodes.StringNode;
import org.gertje.abacus.nodes.SubstractNode;
import org.gertje.abacus.nodes.VariableNode;
import org.gertje.abacus.symboltable.IllegalTypeException;
import org.gertje.abacus.symboltable.NoSuchFunctionException;
import org.gertje.abacus.symboltable.NoSuchVariableException;
import org.gertje.abacus.symboltable.SymbolTable;
import org.gertje.abacus.types.Type;
import org.gertje.abacus.util.CastHelper;
import org.gertje.abacus.util.EvaluationHelper;

import java.util.ArrayList;
import java.util.List;

public class ExpressionEvaluator extends AbstractExpressionNodeVisitor<Object, EvaluationException> {

	/**
	 * De context waarbinnen de interpreter werkt.
	 */
	private final AbacusContext abacusContext;

	/**
	 * De symboltable met de variabelen en de functies.
	 */
	private SymbolTable symbolTable;

	/**
	 * Constructor.
	 */
	public ExpressionEvaluator(AbacusContext abacusContext) {
		this.abacusContext = abacusContext;
		this.symbolTable = abacusContext.getSymbolTable();
	}

	public Object evaluate(ExpressionNode node) throws EvaluationException {
		return node.accept(this);
	}

	@Override
	public Object visit(AddNode node) throws EvaluationException {
		ExpressionNode lhs = node.getLhs();
		ExpressionNode rhs = node.getRhs();

		Object left = lhs.accept(this);
		Object right = rhs.accept(this);

		return EvaluationHelper.add(left, lhs.getType(), right, rhs.getType());
	}

	@Override
	public Object visit(AndNode node) throws EvaluationException {
		ExpressionNode lhs = node.getLhs();
		ExpressionNode rhs = node.getRhs();

		Boolean left = (Boolean) lhs.accept(this);

		// Wanneer links false is, is het resultaat van de operatie false.
		if (left != null && !left.booleanValue()) {
			return Boolean.FALSE;
		}

		Boolean right = (Boolean) rhs.accept(this);

		// Wanneer rechts false is, is het resultaat van de operatie false.
		if (right != null && !right.booleanValue()) {
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
	public Object visit(AssignmentNode node) throws EvaluationException {
		ExpressionNode lhs = node.getLhs();
		ExpressionNode rhs = node.getRhs();

		// Evalueer de rechterkant van de toekenning.
		Object result = rhs.accept(this);

		VariableNode variableNode;

		// Zet het resultaat in de symboltable.
		// Wanneer de linkerkant een variabele is kunnen we het direct in de variabele zetten, anders moeten we eerst
		// de variabele uit de rechterkant halen.
		if (lhs instanceof VariableNode) {
			variableNode = (VariableNode)lhs;
		} else {
			variableNode = (VariableNode)((AssignmentNode)lhs).getRhs();
		}

		try {
			symbolTable.setVariableValue(variableNode.getIdentifier(), rhs.getType(), result);
		} catch (IllegalTypeException e) {
			throw new EvaluationException("Could not set the variable value.", node, e);
		}

		// Geef het resultaat terug.
		return CastHelper.castValue(result, rhs.getType(), variableNode.getType());
	}

	@Override
	public Object visit(BooleanNode node) throws EvaluationException {
		return node.getValue();
	}

	@Override
	public Object visit(DateNode node) throws EvaluationException {
		return node.getValue();
	}

	@Override
	public Object visit(DivideNode node) throws EvaluationException {
		ExpressionNode lhs = node.getLhs();
		ExpressionNode rhs = node.getRhs();

		Number left = (Number) lhs.accept(this);
		Number right = (Number) rhs.accept(this);

		return EvaluationHelper.divide(left, lhs.getType(), right, rhs.getType(), abacusContext.getMathContext());
	}

	@Override
	public Object visit(EqNode node) throws EvaluationException {
		ExpressionNode lhs = node.getLhs();
		ExpressionNode rhs = node.getRhs();

		Object left = lhs.accept(this);
		Object right = rhs.accept(this);

		return EvaluationHelper.eq(left, lhs.getType(), right, rhs.getType());
	}

	@Override
	public Object visit(FactorNode node) throws EvaluationException {
		return node.getArgument().accept(this);
	}

	@Override
	public Object visit(DecimalNode node) throws EvaluationException {
		return node.getValue();
	}

	@Override
	public Object visit(FunctionNode node) throws EvaluationException {
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
		
		try {
			return symbolTable.getFunctionReturnValue(identifier, paramResults, paramTypes);
		} catch (NoSuchFunctionException e) {
			throw new EvaluationException(e.getMessage(), node);
		}
	}

	@Override
	public Object visit(GeqNode node) throws EvaluationException {
		ExpressionNode lhs = node.getLhs();
		ExpressionNode rhs = node.getRhs();

		Object left = lhs.accept(this);
		Object right = rhs.accept(this);

		return EvaluationHelper.geq(left, lhs.getType(), right, rhs.getType());
	}

	@Override
	public Object visit(GtNode node) throws EvaluationException {
		ExpressionNode lhs = node.getLhs();
 		ExpressionNode rhs = node.getRhs();

		Object left = lhs.accept(this);
		Object right = rhs.accept(this);

		return EvaluationHelper.gt(left, lhs.getType(), right, rhs.getType());
	}

	@Override
	public Object visit(IfNode node) throws EvaluationException {
		ExpressionNode condition = node.getCondition();
		ExpressionNode ifBody = node.getIfBody();
		ExpressionNode elseBody = node.getElseBody();

		// Evauleer de conditie.
		Boolean cond = (Boolean)condition.accept(this);

		// Wanneer de conditie leeg is kunnen we ook geen uitspraak doen over het resultaat, geef dus null terug.
		if (cond == null) {
			return null;
		}

		// Wanneer de conditie waar is geven we de geevalueerde if body terug, anders geven we de else body terug.
		if (Boolean.TRUE.equals(cond)) {
			return cast(ifBody.accept(this), ifBody.getType(), node.getType());
		}

		// We moeten de else body geevaluateueerd teruggeven.
		return cast(elseBody.accept(this), elseBody.getType(), node.getType());
	}

	@Override
	public Object visit(IntegerNode node) throws EvaluationException {
		return node.getValue();
	}

	@Override
	public Object visit(LeqNode node) throws EvaluationException {
		ExpressionNode lhs = node.getLhs();
 		ExpressionNode rhs = node.getRhs();

		Object left = lhs.accept(this);
		Object right = rhs.accept(this);

		return EvaluationHelper.leq(left, lhs.getType(), right, rhs.getType());
	}

	@Override
	public Object visit(LtNode node) throws EvaluationException {
		ExpressionNode lhs = node.getLhs();
		ExpressionNode rhs = node.getRhs();

		Object left = lhs.accept(this);
		Object right = rhs.accept(this);

		return EvaluationHelper.lt(left, lhs.getType(), right, rhs.getType());
	}

	@Override
	public Object visit(ModuloNode node) throws EvaluationException {
		ExpressionNode lhs = node.getLhs();
		ExpressionNode rhs = node.getRhs();

		Number left = (Number) lhs.accept(this);
		Number right = (Number) rhs.accept(this);

		return EvaluationHelper.modulo(left, lhs.getType(), right, rhs.getType());
	}

	@Override
	public Object visit(MultiplyNode node) throws EvaluationException {
		ExpressionNode lhs = node.getLhs();
		ExpressionNode rhs = node.getRhs();

		Number left = (Number) lhs.accept(this);
		Number right = (Number) rhs.accept(this);

		return EvaluationHelper.multiply(left, lhs.getType(), right, rhs.getType());
	}

	@Override
	public Object visit(NegativeNode node) throws EvaluationException {
		ExpressionNode argument = node.getArgument();

		// Bepaal het getal dat we negatief gaan maken.
		Number number = (Number)argument.accept(this);

		return EvaluationHelper.negative(number, argument.getType());
	}

	@Override
	public Object visit(NeqNode node) throws EvaluationException {
		ExpressionNode lhs = node.getLhs();
		ExpressionNode rhs = node.getRhs();

		Object left = lhs.accept(this);
		Object right = rhs.accept(this);

		return EvaluationHelper.neq(left, lhs.getType(), right, rhs.getType());
	}

	@Override
	public Object visit(NotNode node) throws EvaluationException {
		ExpressionNode argument = node.getArgument();

		// Bepaal de waarde van de boolean.
		Boolean bool = (Boolean)argument.accept(this);

		return EvaluationHelper.not(bool);
	}

	@Override
	public Object visit(NullNode node) throws EvaluationException {
		return null;
	}

	@Override
	public Object visit(OrNode node) throws EvaluationException {
		ExpressionNode lhs = node.getLhs();
		ExpressionNode rhs = node.getRhs();

		Boolean left = (Boolean) lhs.accept(this);

		// Wanneer links true is, is het resultaat van de operatie true.
		if (left != null && left.booleanValue()) {
			return Boolean.TRUE;
		}

		Boolean right = (Boolean) rhs.accept(this);

		// Wanneer rechts true is, is het resultaat van de operatie true.
		if (right != null && right.booleanValue()) {
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
	public Object visit(PositiveNode node) throws EvaluationException {
		ExpressionNode argument = node.getArgument();
		return argument.accept(this);
	}

	@Override
	public Object visit(PowerNode node) throws EvaluationException {
		ExpressionNode base = node.getBase();
		ExpressionNode power = node.getPower();

		Number baseValue = (Number)base.accept(this);
		Number powerValue = (Number)power.accept(this);

		return EvaluationHelper.power(baseValue, base.getType(), powerValue, power.getType());
	}

	@Override
	public Object visit(StatementListNode node) throws EvaluationException {
		// Evalueer alle AbstractNodes en geef het resultaat van de laatste node terug.
		Object result = null;
		for (ExpressionNode subNode : node) {
			result = subNode.accept(this);
		}

		return result;
	}

	@Override
	public Object visit(StringNode node) throws EvaluationException {
		return node.getValue();
	}

	@Override
	public Object visit(SubstractNode node) throws EvaluationException {
		ExpressionNode lhs = node.getLhs();
		ExpressionNode rhs = node.getRhs();

		Number left = (Number) lhs.accept(this);
		Number right = (Number) rhs.accept(this);

		return EvaluationHelper.substract(left, lhs.getType(), right, rhs.getType());
	}

	@Override
	public Object visit(VariableNode node) throws EvaluationException {
		String identifier = node.getIdentifier();
		try {
			return symbolTable.getVariableValue(identifier);
		} catch (NoSuchVariableException e) {
			throw new EvaluationException(e.getMessage(), node);
		}
	}

	/**
	 * Casts the given object from one type to another.
	 * @param object The object to cast.
	 * @param fromType The original type of the expression.
	 * @param toType The resulting type of the expression.
	 * @return The given expression casted to the desired type.
	 */
	private Object cast(Object object, Type fromType, Type toType) {
		if (fromType == toType || fromType == null || toType == null) {
			return object;
		}

		// Cast the expression from integer to decimal.
		if (fromType == Type.INTEGER && toType == Type.DECIMAL) {
			return new java.math.BigDecimal(object.toString(), abacusContext.getMathContext());
		}

		// We only need to cast from integer to decimal.
		throw new IllegalStateException("An unexpected type cast was needed.");
	}
}
