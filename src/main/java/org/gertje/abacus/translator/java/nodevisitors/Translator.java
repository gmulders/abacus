package org.gertje.abacus.translator.java.nodevisitors;

import org.gertje.abacus.context.AbacusContext;
import org.gertje.abacus.nodes.AbstractComparisonNode;
import org.gertje.abacus.nodes.AddNode;
import org.gertje.abacus.nodes.AndNode;
import org.gertje.abacus.nodes.ArrayNode;
import org.gertje.abacus.nodes.AssignmentNode;
import org.gertje.abacus.nodes.BinaryOperationNode;
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
import org.gertje.abacus.nodes.NodeType;
import org.gertje.abacus.nodes.NotNode;
import org.gertje.abacus.nodes.NullNode;
import org.gertje.abacus.nodes.OrNode;
import org.gertje.abacus.nodes.PositiveNode;
import org.gertje.abacus.nodes.PowerNode;
import org.gertje.abacus.nodes.RootNode;
import org.gertje.abacus.nodes.StatementListNode;
import org.gertje.abacus.nodes.StringNode;
import org.gertje.abacus.nodes.SubtractNode;
import org.gertje.abacus.nodes.SumNode;
import org.gertje.abacus.nodes.VariableNode;
import org.gertje.abacus.nodevisitors.DefaultVisitor;
import org.gertje.abacus.nodevisitors.EvaluationException;
import org.gertje.abacus.nodevisitors.NodeVisitor;
import org.gertje.abacus.symboltable.SymbolTable;
import org.gertje.abacus.translator.java.util.JavaEscaper;
import org.gertje.abacus.types.Type;
import org.gertje.abacus.util.JavaTypeHelper;

import java.math.MathContext;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Translator implements NodeVisitor<Void, TranslationException> {

	/**
	 * The context for this evaluator.
	 */
	private final AbacusContext abacusContext;

	/**
	 * De symboltable met de variabelen en de functies.
	 */
	protected SymbolTable symbolTable;

	/**
	 * The translation result.
	 */
	private StringBuilder expression;

	/**
	 * The result of the evaluation.
	 */
	private String resultName;

	/**
	 * Constructor.
	 */
	public Translator(AbacusContext abacusContext) {
		this.abacusContext = abacusContext;
		this.symbolTable = abacusContext.getSymbolTable();

		expression = new StringBuilder();
	}

	public String translate(Node node) throws TranslationException {
		expression.append(determineMathContext());
		node.accept(this);

		if (node.getNodeType() == NodeType.EXPRESSION) {
			expression.append("return ").append(determineVariableName(node)).append(";\n");
		} else {
			expression.append("return ").append(resultName).append(";\n");
		}

		return expression.toString();
	}

	@Override
	public Void visit(AddNode node) throws TranslationException {
		throw new TranslationException(
				"The Add-node was visited, which means that the node was not properly simplified.", node);
	}

	@Override
	public Void visit(AndNode node) throws TranslationException {
		ExpressionNode lhs = node.getLhs();
		ExpressionNode rhs = node.getRhs();

		String name = determineVariableName(node);
		String left = determineVariableName(lhs);
		String right = determineVariableName(rhs);

		lhs.accept(this);

		appendDefinition(node);

		expression.append("if (").append(left).append(" != null && !").append(left).append(".booleanValue()) {")
						.append(name).append(" = Boolean.FALSE;")
					.append("} else {");

		rhs.accept(this);

		expression.append("if (").append(right).append(" != null && !").append(right).append(".booleanValue()) {")
						.append(name).append(" = Boolean.FALSE;")
					.append("} else if (").append(left).append(" == null || ").append(right).append(" == null) {")
						.append(name).append(" = null;")
					.append("} else {")
						.append(name).append(" = Boolean.TRUE;")
					.append("}")
				.append("}\n");

		return null;
	}

	@Override
	public Void visit(ArrayNode node) throws TranslationException {
		ExpressionNode array = node.getArray();
		ExpressionNode index = node.getIndex();

		array.accept(this);
		index.accept(this);

		String name = determineVariableName(node);
		String arrayName = determineVariableName(array);
		String indexName = determineVariableName(index);

		appendDefinition(node);

		expression.append(name).append(" = ")
				.append(arrayName).append(" == null || ")
				.append(indexName).append(" == null || ")
				.append(indexName).append(".intValue() >= ").append(arrayName).append(".length ||")
				.append(indexName).append(".intValue() < 0 ? null : ")
				.append(arrayName).append("[").append(indexName).append(".intValue()];\n");

		return null;
	}

	@Override
	public Void visit(AssignmentNode node) throws TranslationException {
		ExpressionNode lhs = node.getLhs();
		ExpressionNode rhs = node.getRhs();

		String name = determineVariableName(node);
		String left = determineVariableName(lhs);

		ValueAssigner valueAssigner = new ValueAssigner();
		valueAssigner.assign(lhs, rhs, node.getType());

		appendDefinition(node);
		appendAssignment(name, node.getType(), left, lhs.getType());

		return null;
	}

	@Override
	public Void visit(BooleanNode node) throws TranslationException {
		String value = "null";
		if (node.getValue() != null) {
			value = node.getValue() ? "Boolean.TRUE" : "Boolean.FALSE";
		}
		appendDefinition(node);
		expression.append(determineVariableName(node)).append(" = ").append(value).append(";\n");
		return null;
	}

	@Override
	public Void visit(ConcatStringNode node) throws TranslationException {
		ExpressionNode lhs = node.getLhs();
		ExpressionNode rhs = node.getRhs();

		lhs.accept(this);
		rhs.accept(this);

		String name = determineVariableName(node);
		String left = determineVariableName(lhs);
		String right = determineVariableName(rhs);

		appendDefinition(node);

		expression.append(name).append(" = ");
		expression.append(left).append("==null || ").append(right).append("==null ? null : ");
		expression.append(left).append(" + ").append(right).append(";\n");
		return null;
	}

	@Override
	public Void visit(DateNode node) throws TranslationException {
		String value = "null";
		if (node.getValue() != null) {
			value = "new java.sql.Date(" + node.getValue().getTime() + "L)";
		}
		appendDefinition(node);
		expression.append(determineVariableName(node)).append(" = ").append(value).append(";\n");
		return null;
	}

	@Override
	public Void visit(DecimalNode node) throws TranslationException {
		String value = "null";
		if (node.getValue() != null) {
			value = "new java.math.BigDecimal(\"" + node.getValue().toPlainString() + "\")";
		}
		appendDefinition(node);
		expression.append(determineVariableName(node)).append(" = ").append(value).append(";\n");
		return null;
	}

	@Override
	public Void visit(DivideNode node) throws TranslationException {
		ExpressionNode lhs = node.getLhs();
		ExpressionNode rhs = node.getRhs();

		lhs.accept(this);
		rhs.accept(this);

		appendBinaryArithmeticOperationNode(node, "divide");

		return null;
	}

	@Override
	public Void visit(EqNode node) throws TranslationException {
		ExpressionNode lhs = node.getLhs();
		ExpressionNode rhs = node.getRhs();

		lhs.accept(this);
		rhs.accept(this);

		String name = determineVariableName(node);
		String left = determineVariableName(lhs);
		String right = determineVariableName(rhs);

		appendDefinition(node);

		expression.append(name).append(" = ");

		if (lhs instanceof NullNode) {
			expression.append(right).append("==null;\n");
			return null;
		} else if (rhs instanceof NullNode) {
			expression.append(left).append("==null;\n");
			return null;
		}

		expression.append(left).append("==null && ").append(right).append("==null ? true : ");
		expression.append(left).append("==null || ").append(right).append("==null ? false : ");
		appendComparison(lhs, rhs, "==");
		return null;
	}

	@Override
	public Void visit(FactorNode node) throws TranslationException {
		node.getArgument().accept(this);
		return null;
	}

	@Override
	public Void visit(FunctionNode node) throws TranslationException {
		// TODO: Afmaken.
		List<ExpressionNode> parameters = node.getParameters();
		String identifier = node.getIdentifier();

		String name = determineVariableName(node);

		// Create a list with all parameter names.
		List<String> paramNames = new ArrayList<>();

		// Create a list with all parameter types.
		List<Type> paramTypes = new ArrayList<>();

		// Loop over all parameters.
		for (ExpressionNode parameter : parameters) {
			parameter.accept(this);
			paramNames.add(determineVariableName(parameter));
			paramTypes.add(parameter.getType());
		}

		appendDefinition(node);

		// Maak de functie-aanroep.
		expression.append(name).append(" = function_").append(identifier).append("(");
		Iterator<String> it = paramNames.iterator();
		while (it.hasNext()) {
			expression.append(it.next());
			if (it.hasNext()) {
				expression.append(", ");
			}
		}
		expression.append(");\n");

		// Controleer of de variabele bestaat. Als deze niet bestaat gooien we een exceptie.
		if (!symbolTable.getExistsFunction(identifier, paramTypes)) {
			throw new TranslationException("Function '" + identifier + "' does not exist.", node);
		}

		return null;
	}

	@Override
	public Void visit(GeqNode node) throws TranslationException {
		appendComparison(node, ">=");
		return null;
	}

	@Override
	public Void visit(GtNode node) throws TranslationException {
		appendComparison(node, ">");
		return null;
	}

	@Override
	public Void visit(IfNode node) throws TranslationException {
		ExpressionNode condition = node.getCondition();
		ExpressionNode ifBody = node.getIfBody();
		ExpressionNode elseBody = node.getElseBody();

		String name = determineVariableName(node);
		String cond = determineVariableName(condition);
		String ifb = determineVariableName(ifBody);
		String elseb = determineVariableName(elseBody);

		condition.accept(this);

		appendDefinition(node);

		expression.append("if (").append(cond).append(" == null) {\n")
						.append(name).append(" = null;\n")
					.append("} else if (").append(cond).append(") {\n");

		ifBody.accept(this);
		appendAssignment(name, node.getType(), ifb, ifBody.getType());

		expression.append("} else {\n");

		elseBody.accept(this);
		appendAssignment(name, node.getType(), elseb, elseBody.getType());

		expression.append("}\n");
		return null;
	}

	@Override
	public Void visit(IntegerNode node) throws TranslationException {
		String value = "null";
		if (node.getValue() != null) {
			value = "Long.valueOf(\"" + node.getValue().toString() + "\")";
		}
		appendDefinition(node);
		expression.append(determineVariableName(node)).append(" = ").append(value).append(";\n");
		return null;
	}

	@Override
	public Void visit(LeqNode node) throws TranslationException {
		appendComparison(node, "<=");
		return null;
	}

	@Override
	public Void visit(LtNode node) throws TranslationException {
		appendComparison(node, "<");
		return null;
	}

	@Override
	public Void visit(ModuloNode node) throws TranslationException {
		ExpressionNode lhs = node.getLhs();
		ExpressionNode rhs = node.getRhs();

		lhs.accept(this);
		rhs.accept(this);

		appendBinaryArithmeticOperationNode(node, "modulo");

		return null;
	}

	@Override
	public Void visit(MultiplyNode node) throws TranslationException {
		ExpressionNode lhs = node.getLhs();
		ExpressionNode rhs = node.getRhs();

		lhs.accept(this);
		rhs.accept(this);

		appendBinaryArithmeticOperationNode(node, "multiply");

		return null;
	}

	@Override
	public Void visit(NegativeNode node) throws TranslationException {
		ExpressionNode argument = node.getArgument();

		argument.accept(this);

		String name = determineVariableName(node);
		String arg = determineVariableName(argument);

		appendDefinition(node);

		expression.append(name).append(" = ");
		expression.append(arg).append("==null ? null : ").append("ArithmeticOperation.negate(").append(arg).append(");\n");

		return null;
	}

	@Override
	public Void visit(NeqNode node) throws TranslationException {
		ExpressionNode lhs = node.getLhs();
		ExpressionNode rhs = node.getRhs();

		lhs.accept(this);
		rhs.accept(this);

		String name = determineVariableName(node);
		String left = determineVariableName(lhs);
		String right = determineVariableName(rhs);

		appendDefinition(node);

		expression.append(name).append(" = ");

		if (lhs instanceof NullNode) {
			expression.append(right).append("!=null;\n");
			return null;
		} else if (rhs instanceof NullNode) {
			expression.append(left).append("!=null;\n");
			return null;
		}

		expression.append(left).append("==null && ").append(right).append("==null ? false : ");
		expression.append(left).append("==null || ").append(right).append("==null ? true : ");
		appendComparison(lhs, rhs, "!=");
		return null;
	}

	@Override
	public Void visit(NotNode node) throws TranslationException {
		ExpressionNode argument = node.getArgument();

		argument.accept(this);

		String name = determineVariableName(node);
		String arg = determineVariableName(argument);

		appendDefinition(node);

		expression.append(name).append(" = ");
		expression.append(arg).append("==null ? null : !").append(arg).append(";\n");

		return null;
	}

	@Override
	public Void visit(NullNode node) throws TranslationException {
		appendDefinition(node);
		expression.append(determineVariableName(node)).append(" = null;\n");
		return null;
	}

	@Override
	public Void visit(OrNode node) throws TranslationException {
		ExpressionNode lhs = node.getLhs();
		ExpressionNode rhs = node.getRhs();

		String name = determineVariableName(node);
		String left = determineVariableName(lhs);
		String right = determineVariableName(rhs);

		lhs.accept(this);

		appendDefinition(node);

		expression.append("if (").append(left).append(" != null && ").append(left).append(".booleanValue()) {")
						.append(name).append(" = Boolean.TRUE;")
					.append("} else {");

		rhs.accept(this);

		expression.append("if (").append(right).append(" != null && ").append(right).append(".booleanValue()) {")
						.append(name).append(" = Boolean.TRUE;")
					.append("} else if (").append(left).append(" == null || ").append(right).append(" == null) {")
						.append(name).append(" = null;")
					.append("} else {")
						.append(name).append(" = Boolean.FALSE;")
					.append("}")
				.append("}\n");

		return null;
	}

	@Override
	public Void visit(PositiveNode node) throws TranslationException {
		ExpressionNode argument = node.getArgument();
		argument.accept(this);

		String name = determineVariableName(node);
		String arg = determineVariableName(argument);

		appendDefinition(node);
		expression.append(name).append(" = ").append(arg).append(";\n");
		return null;
	}

	@Override
	public Void visit(PowerNode node) throws TranslationException {
		ExpressionNode lhs = node.getLhs();
		ExpressionNode rhs = node.getRhs();

		lhs.accept(this);
		rhs.accept(this);

		String name = determineVariableName(node);
		String left = determineVariableName(lhs);
		String right = determineVariableName(rhs);

		appendDefinition(node);

		expression.append(name).append(" = ")
				.append(left).append("==null || ").append(right).append("==null ? null : ")
				.append("ArithmeticOperation.power(").append(left).append(", ").append(right);

		if (Type.equals(node.getType(), Type.DECIMAL)) {
			expression.append(", mathContext");
		}

		expression.append(");\n");

		return null;
	}

	@Override
	public Void visit(RootNode node) throws TranslationException {
		node.getStatementListNode().accept(this);
		return null;
	}

	@Override
	public Void visit(StatementListNode node) throws TranslationException {
		Iterator<Node> it = node.iterator();
		while (it.hasNext()) {
			Node subNode = it.next();

			subNode.accept(this);

			if (subNode.getNodeType() == NodeType.EXPRESSION && !it.hasNext()) {
				resultName = determineVariableName(subNode);
			}
		}

		return null;
	}

	@Override
	public Void visit(StringNode node) throws TranslationException {
		String value = "null";
		if (node.getValue() != null) {
			value = '"' + JavaEscaper.escapeJava(node.getValue()) + '"';
		}
		appendDefinition(node);
		expression.append(determineVariableName(node)).append(" = ").append(value).append(";\n");
		return null;
	}

	@Override
	public Void visit(SubtractNode node) throws TranslationException {
		ExpressionNode lhs = node.getLhs();
		ExpressionNode rhs = node.getRhs();

		lhs.accept(this);
		rhs.accept(this);

		appendBinaryArithmeticOperationNode(node, "subtract");

		return null;
	}

	@Override
	public Void visit(SumNode node) throws TranslationException {
		ExpressionNode lhs = node.getLhs();
		ExpressionNode rhs = node.getRhs();

		lhs.accept(this);
		rhs.accept(this);

		appendBinaryArithmeticOperationNode(node, "sum");

		return null;
	}

	@Override
	public Void visit(VariableNode node) throws TranslationException {
		appendDefinition(node);

		String identifier = node.getIdentifier();
		String name = determineVariableName(node);

		expression.append("try {\n	")
					.append(name).append(" = (").append(determineJavaType(node.getType()))
					.append(") abacusContext.getSymbolTable().getVariableValue(\"").append(identifier).append("\");\n")
				.append("} catch (org.gertje.abacus.symboltable.NoSuchVariableException e) {\n")
					.append("	throw new JavaExecutionException(\"Variable '").append(identifier)
						.append("' does not exist.\", ").append(node.getToken().getLineNumber())
						.append(" , ").append(node.getToken().getColumnNumber()).append(");\n")
				.append("}\n");
		return null;
	}

	/**
	 * Assigns a value to a variable or to an index.
	 */
	private class ValueAssigner extends DefaultVisitor<Void, TranslationException> {

		/**
		 * The value to assign.
		 */
		private ExpressionNode value;

		/**
		 * The type of the assignment.
		 */
		private Type type;

		public ValueAssigner() {
			// Don't visit the child nodes.
			visitChildNodes = false;
		}

		/**
		 * Assigns the value to the correct variable or array-index.
		 * @param node The node that determines where to assign the value to.
		 * @param value The value to assign.
		 * @param type The type of the assignment.
		 * @throws TranslationException
		 */
		public void assign(ExpressionNode node, ExpressionNode value, Type type) throws TranslationException {
			this.value = value;
			this.type = type;

			// Determine the value to assign.
			value.accept(Translator.this);

			appendDefinition(node);

			node.accept(this);
		}

		@Override
		public Void visit(ArrayNode node) throws TranslationException {
			String name = determineVariableName(node);
			String arrayName = determineVariableName(node.getArray());
			String indexName = determineVariableName(node.getIndex());
			String valueName = determineVariableName(value);

			// Get the array.
			node.getArray().accept(Translator.this);
			// Determine the index.
			node.getIndex().accept(Translator.this);

			expression.append("if (").append(arrayName).append(" == null || ").append(indexName).append(" == null || ")
					.append(indexName).append(".intValue() >= ").append(arrayName).append(".length || ")
					.append(indexName).append(".intValue() < 0) {\n")
					.append("\t").append(name).append(" = null;\n")
					.append("} else {\n")
					.append("\t").append(name).append(" = ").append(arrayName).append("[").append(indexName)
					.append(".intValue()] = ").append(castValue(valueName, value.getType(), node.getType())).append(";\n")
					.append("}\n");

			return null;
		}

		@Override
		public Void visit(VariableNode node) throws TranslationException {
			String name = determineVariableName(node);

			// Determine the name of the value.
			String valueName = determineVariableName(value);
			// Determine the identifier of the String.
			String identifier = node.getIdentifier();
			// Assign the value to the variable in the symbol table.
			expression.append("abacusContext.getSymbolTable().setVariableValue(\"").append(identifier).append("\", ")
					.append(castValue(valueName, value.getType(), node.getType())).append(");\n");

			appendAssignment(name, node.getType(), valueName, value.getType());

			return null;
		}
	}

	/**
	 * Appends an assignment to the expression.
	 * @param name The name of the variable to assign to.
	 * @param nodeType The type of the variable to assign to.
	 * @param value The value to assign to the variable.
	 * @param valueType The type of the value to assign to the variable.
	 */
	private void appendAssignment(String name, Type nodeType, String value, Type valueType) {
		expression.append(name).append(" = ").append(castValue(value, valueType, nodeType)).append(";\n");
	}

	/**
	 * Casts the given value from the given from-type, to the given to-type.
	 * @param value The name of the variable holding the value.
	 * @param fromType The type to cast from.
	 * @param toType The type to cast to.
	 */
	private String castValue(String value, Type fromType, Type toType) {
		if (Type.equals(fromType, Type.INTEGER) && Type.equals(toType, Type.DECIMAL)) {
			return "java.math.BigDecimal.valueOf(" + value + ")";
		}

		if (Type.equals(fromType, Type.DECIMAL) && Type.equals(toType, Type.INTEGER)) {
			return value + ".longValue()";
		}

		if (!Type.equals(fromType, toType)) {
			return "(" + determineJavaType(toType) + ")" + value;
		}

		return value;
	}

	/**
	 * Appends a comparison to the {@link StringBuilder}.
	 * @param node The node to build the comparison for.
	 * @param comparator A string representing the Java comparison operator.
	 * @throws TranslationException
	 */
	private void appendComparison(AbstractComparisonNode node, String comparator) throws TranslationException {
		ExpressionNode lhs = node.getLhs();
		ExpressionNode rhs = node.getRhs();

		lhs.accept(this);
		rhs.accept(this);

		String name = determineVariableName(node);
		String left = determineVariableName(lhs);
		String right = determineVariableName(rhs);

		appendDefinition(node);

		expression.append(name).append(" = ");

		expression.append(left).append("==null || ").append(right).append("==null ? null : ");

		appendComparison(lhs, rhs, comparator);
	}

	/**
	 * Appends a comparison to the expression.
	 * @param lhs The left hand side of the expression.
	 * @param rhs The right hand side of the expression.
	 * @param comparator The comparator to use.
	 */
	private void appendComparison(ExpressionNode lhs, ExpressionNode rhs, String comparator) {
		String left = determineVariableName(lhs);
		String right = determineVariableName(rhs);

		if (Type.equals(lhs.getType(), Type.DECIMAL) && Type.equals(rhs.getType(), Type.INTEGER)) {
			expression.append(left).append(".compareTo(new java.math.BigDecimal(").append(right).append(")) ").append(comparator).append(" 0;\n");
		} else if (Type.equals(lhs.getType(), Type.INTEGER) && Type.equals(rhs.getType(), Type.DECIMAL)) {
			expression.append("new java.math.BigDecimal(").append(left).append(").compareTo(").append(right).append(") ").append(comparator).append(" 0;\n");
		} else {
			expression.append(left).append(".compareTo(").append(right).append(") ").append(comparator).append(" 0;\n");
		}
	}

	/**
	 * Appends a binary arithmetic operation to the expression.
	 * @param node The node to append the operation for.
	 * @param operation The name of the operation.
	 */
	private void appendBinaryArithmeticOperationNode(BinaryOperationNode node, String operation) {
		String name = determineVariableName(node);
		String left = determineVariableName(node.getLhs());
		String right = determineVariableName(node.getRhs());

		appendDefinition(node);

		expression.append(name).append(" = ")
				.append(left).append("==null || ").append(right).append("==null ? null : ")
				.append("ArithmeticOperation.").append(operation).append("(").append(left).append(", ").append(right);

		if (Type.equals(node.getType(), Type.DECIMAL)) {
			expression.append(", mathContext");
		}

		expression.append(");\n");
	}

	/**
	 * Appends the definition of the variable of the given node.
	 * @param node The node to create the variable for.
	 */
	private void appendDefinition(ExpressionNode node) {
		expression.append(determineJavaType(node.getType())).append(" ").append(determineVariableName(node))
				.append(";\n");
	}

	/**
	 * Determines the Java type from the {@link Type}.
	 * @param type The {@link Type} to translate into a Java type.
	 * @return The Java type.
	 */
	private static String determineJavaType(Type type) {
		// If the type is null we return the class of Object.
		if (type == null) {
			return Object.class.getName();
		}

		// If the type is a primitive type we return the Java type of the base type.
		if (!type.isArray()) {
			return JavaTypeHelper.determineJavaType(type.getBaseType()).getName();
		}

		String baseType = JavaTypeHelper.determineJavaType(type.getBaseType()).getName();

		return baseType + new String(new char[type.getDimensionality()]).replaceAll("\0", "[]");
	}

	/**
	 * Determines the name of the variable that represents this node.
	 * @param node The node.
	 * @return The name of the variable that represents this node.
	 */
	private static String determineVariableName(Node node) {
		return "n" + node.getId();
	}

	/**
	 * Determines the Java-string representation of the {@link MathContext}.
	 * @return The Java-string representation of the {@link MathContext}.
	 */
	private String determineMathContext() {
		MathContext mathContext = abacusContext.getMathContext();
		return "java.math.MathContext mathContext = new java.math.MathContext(" + mathContext.getPrecision()
				+ ", java.math.RoundingMode." + mathContext.getRoundingMode() + ");\n";
	}
}
