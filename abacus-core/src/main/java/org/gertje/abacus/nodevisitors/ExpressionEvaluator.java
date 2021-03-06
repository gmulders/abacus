package org.gertje.abacus.nodevisitors;

import org.gertje.abacus.context.AbacusContext;
import org.gertje.abacus.nodes.AddNode;
import org.gertje.abacus.nodes.AndNode;
import org.gertje.abacus.nodes.ArrayNode;
import org.gertje.abacus.nodes.AssignmentNode;
import org.gertje.abacus.nodes.BooleanNode;
import org.gertje.abacus.nodes.ConcatStringNode;
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
import org.gertje.abacus.nodes.StringNode;
import org.gertje.abacus.nodes.SubtractNode;
import org.gertje.abacus.nodes.SumNode;
import org.gertje.abacus.nodes.VariableNode;
import org.gertje.abacus.runtime.expression.BooleanOperation;
import org.gertje.abacus.runtime.expression.StringOperation;
import org.gertje.abacus.symboltable.NoSuchFunctionException;
import org.gertje.abacus.symboltable.NoSuchVariableException;
import org.gertje.abacus.symboltable.SymbolTable;
import org.gertje.abacus.types.Type;
import org.gertje.abacus.util.CastHelper;
import org.gertje.abacus.util.EvaluationHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Evaluator for an AST of {@link ExpressionNode}s.
 */
public class ExpressionEvaluator extends AbstractExpressionNodeVisitor<Object, EvaluationException> {

	/**
	 * The context for this evaluator.
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

		return EvaluationHelper.sum(left, lhs.getType(), right, rhs.getType(), abacusContext.getMathContext());
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
	public Object visit(ArrayNode node) throws EvaluationException {
		ExpressionNode array = node.getArray();
		ExpressionNode index = node.getIndex();

		// Evalueer de rechterkant van de toekenning.
		Object[] arrayValue = (Object[]) array.accept(this);
		Long indexValue = (Long) index.accept(this);

		if (arrayValue == null || indexValue == null || indexValue >= arrayValue.length || indexValue < 0) {
			return null;
		}

		return arrayValue[indexValue.intValue()];
	}

	@Override
	public Object visit(AssignmentNode node) throws EvaluationException {
		ExpressionNode lhs = node.getLhs();
		ExpressionNode rhs = node.getRhs();

		// Evalueer de rechterkant van de toekenning.
		Object result = rhs.accept(this);
		result = CastHelper.castValue(result, rhs.getType(), lhs.getType());

		ValueAssigner valueAssigner = new ValueAssigner();
		return valueAssigner.assign(lhs, result);
	}

	@Override
	public Object visit(BooleanNode node) throws EvaluationException {
		return node.getValue();
	}

	@Override
	public Object visit(ConcatStringNode node) throws EvaluationException {
		ExpressionNode lhs = node.getLhs();
		ExpressionNode rhs = node.getRhs();

		String left = (String)lhs.accept(this);
		String right = (String)rhs.accept(this);

		return StringOperation.concat(left, right);
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

		return EvaluationHelper.multiply(left, lhs.getType(), right, rhs.getType(), abacusContext.getMathContext());
	}

	@Override
	public Object visit(NegativeNode node) throws EvaluationException {
		ExpressionNode argument = node.getArgument();

		// Bepaal het getal dat we negatief gaan maken.
		Number number = (Number)argument.accept(this);

		return EvaluationHelper.negate(number, argument.getType());
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

		return BooleanOperation.not(bool);
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

		return EvaluationHelper.power(baseValue, base.getType(), powerValue, power.getType(), abacusContext.getMathContext());
	}

	@Override
	public Object visit(StringNode node) throws EvaluationException {
		return node.getValue();
	}

	@Override
	public Object visit(SubtractNode node) throws EvaluationException {
		ExpressionNode lhs = node.getLhs();
		ExpressionNode rhs = node.getRhs();

		Number left = (Number) lhs.accept(this);
		Number right = (Number) rhs.accept(this);

		return EvaluationHelper.subtract(left, lhs.getType(), right, rhs.getType(), abacusContext.getMathContext());
	}

	@Override
	public Object visit(SumNode node) throws EvaluationException {
		ExpressionNode lhs = node.getLhs();
		ExpressionNode rhs = node.getRhs();

		Number left = (Number) lhs.accept(this);
		Number right = (Number) rhs.accept(this);

		return EvaluationHelper.sum(left, lhs.getType(), right, rhs.getType(), abacusContext.getMathContext());
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
	 * Assigns a value to a variable or to an index.
	 */
	private class ValueAssigner extends DefaultVisitor<Object, EvaluationException> {

		/**
		 * The value to assign.
		 */
		private Object value;

		public ValueAssigner() {
			// Don't visit the child nodes.
			visitChildNodes = false;
		}

		/**
		 * Assigns the value to the correct variable or array-index.
		 * @param node The node that determines where to assign the value to.
		 * @param value The value to assign.
		 * @throws EvaluationException
		 */
		public Object assign(Node node, Object value) throws EvaluationException {
			this.value = value;
			return node.accept(this);
		}

		@Override
		public Object visit(ArrayNode node) throws EvaluationException {
			// Get the array.
			Object[] array = (Object[]) node.getArray().accept(ExpressionEvaluator.this);
			// Determine the index.
			Long index = (Long) node.getIndex().accept(ExpressionEvaluator.this);

			// If the value cannot be found, this node should evaluate to false.
			if (array == null || index == null || index >= array.length || index < 0) {
				return null;
			}

			// Assign and return the value.
			return array[index.intValue()] = value;
		}

		@Override
		public Object visit(VariableNode node) throws EvaluationException {
			try {
				symbolTable.setVariableValue(node.getIdentifier(), value);
			} catch (Exception e) {
				throw new EvaluationException("Could not set the variable value.", node, e);
			}

			return value;
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
		if (Type.equals(fromType, toType) || fromType == null || toType == null) {
			return object;
		}

		// Cast the expression from integer to decimal.
		if (Type.equals(fromType, Type.INTEGER) && Type.equals(toType, Type.DECIMAL)) {
			return new java.math.BigDecimal(object.toString(), abacusContext.getMathContext());
		}

		// We only need to cast from integer to decimal.
		throw new IllegalStateException("An unexpected type cast was needed.");
	}
}
