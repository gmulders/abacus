package org.gertje.abacus.translator.javascript.nodevisitors;

import org.gertje.abacus.context.AbacusContext;
import org.gertje.abacus.nodes.AbstractComparisonNode;
import org.gertje.abacus.nodes.AddNode;
import org.gertje.abacus.nodes.AndNode;
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
import org.gertje.abacus.nodevisitors.NodeVisitor;
import org.gertje.abacus.nodevisitors.VisitingException;
import org.gertje.abacus.symboltable.SymbolTable;
import org.gertje.abacus.types.Type;

import java.math.MathContext;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Translator to translate an AST to JavaScript.
 */
public class JavaScriptTranslator implements NodeVisitor<Void, VisitingException> {

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
	public JavaScriptTranslator(AbacusContext abacusContext) {
		this.abacusContext = abacusContext;
		this.symbolTable = abacusContext.getSymbolTable();

		expression = new StringBuilder();
	}

	public String translate(Node node) throws VisitingException {

		expression.append("(function(){\n");

		expression.append(determineMathContext());
		node.accept(this);

		if (node.getNodeType() == NodeType.EXPRESSION) {
			expression.append("return ").append(determineVariableName(node)).append(";\n");
		} else {
			expression.append("return ").append(resultName).append(";\n");
		}

		return expression.append("})();\n").toString();
	}
	
	@Override
	public Void visit(AddNode node) throws VisitingException {
		throw new VisitingException(
				"The Add-node was visited, which means that the node was not properly simplified.", node);
	}

	@Override
	public Void visit(AndNode node) throws VisitingException {
		ExpressionNode lhs = node.getLhs();
		ExpressionNode rhs = node.getRhs();

		String name = determineVariableName(node);
		String left = determineVariableName(lhs);
		String right = determineVariableName(rhs);

		lhs.accept(this);

		appendDefinition(node);

		expression.append("if (").append(left).append(" != null && !").append(left).append(") {")
						.append(name).append(" = false;")
					.append("} else {");

		rhs.accept(this);

		expression.append("if (").append(right).append(" != null && !").append(right).append(") {")
						.append(name).append(" = false;")
					.append("} else if (").append(left).append(" == null || ").append(right).append(" == null) {")
						.append(name).append(" = null;")
					.append("} else {")
						.append(name).append(" = true;")
					.append("}")
				.append("}\n");

		return null;
	}

	@Override
	public Void visit(AssignmentNode node) throws VisitingException {
		ExpressionNode lhs = node.getLhs();
		ExpressionNode rhs = node.getRhs();

		String name = determineVariableName(node);
		String right = determineVariableName(rhs);

		String identifier = ((VariableNode) lhs).getIdentifier();

		rhs.accept(this);

		appendDefinition(node);
		appendAssignment(identifier, node.getType(), right, rhs.getType());
		appendAssignment(name, node.getType(), identifier, node.getType());

		return null;
	}

	@Override
	public Void visit(BooleanNode node) throws VisitingException {
		String value = "null";
		if (node.getValue() != null) {
			value = node.getValue() ? "true" : "false";
		}
		appendDefinition(node);
		expression.append(determineVariableName(node)).append(" = ").append(value).append(";\n");
		return null;
	}

	@Override
	public Void visit(ConcatStringNode node) throws VisitingException {
		ExpressionNode lhs = node.getLhs();
		ExpressionNode rhs = node.getRhs();

		lhs.accept(this);
		rhs.accept(this);

		String name = determineVariableName(node);
		String left = determineVariableName(lhs);
		String right = determineVariableName(rhs);

		appendDefinition(node);

		expression.append(name).append(" = ");
		expression.append(left).append("==null||").append(right).append("==null ? null : ");
		expression.append(left).append(" + ").append(right).append(";\n");
		return null;
	}

	@Override
	public Void visit(DateNode node) throws VisitingException {
		String value = "null";
		if (node.getValue() != null) {
			value = "new Date(" + node.getValue().getTime() + ")";
		}
		appendDefinition(node);
		expression.append(determineVariableName(node)).append(" = ").append(value).append(";\n");
		return null;
	}

	@Override
	public Void visit(DecimalNode node) throws VisitingException {
		String value = "null";
		if (node.getValue() != null) {
			value = "new Decimal(\"" + node.getValue().toPlainString() + "\")";
		}
		appendDefinition(node);
		expression.append(determineVariableName(node)).append(" = ").append(value).append(";\n");
		return null;
	}

	@Override
	public Void visit(DivideNode node) throws VisitingException {
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

		if (lhs.getType() == Type.DECIMAL && rhs.getType() == Type.DECIMAL) {
			expression.append(left).append(".div(").append(right).append(");");
		} else if (lhs.getType() == Type.DECIMAL && rhs.getType() == Type.INTEGER) {
			expression.append(left).append(".div(new Decimal(").append(right).append("));");
		} else if (lhs.getType() == Type.INTEGER && rhs.getType() == Type.DECIMAL) {
			expression.append("(new Decimal(").append(left).append(")).div(").append(right).append(");");
		} else if (lhs.getType() == Type.INTEGER && rhs.getType() == Type.INTEGER) {
			expression.append("(").append(left).append(" / ").append(right).append(")|0;");
		}

		expression.append("\n");

		return null;
	}

	@Override
	public Void visit(EqNode node) throws VisitingException {
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
	public Void visit(FactorNode node) throws VisitingException {
		node.getArgument().accept(this);
		return null;
	}

	@Override
	public Void visit(FunctionNode node) throws VisitingException {
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
			throw new VisitingException("Function '" + identifier + "' does not exist.", node);
		}

		return null;
	}

	@Override
	public Void visit(GeqNode node) throws VisitingException {
		appendComparison(node, ">=");
		return null;
	}

	@Override
	public Void visit(GtNode node) throws VisitingException {
		appendComparison(node, ">");
		return null;
	}

	@Override
	public Void visit(IfNode node) throws VisitingException {
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
	public Void visit(IntegerNode node) throws VisitingException {
		String value = "null";
		if (node.getValue() != null) {
			value = node.getValue().toString() + "|0";
		}
		appendDefinition(node);
		expression.append(determineVariableName(node)).append(" = ").append(value).append(";\n");
		return null;
	}

	@Override
	public Void visit(LeqNode node) throws VisitingException {
		appendComparison(node, "<=");
		return null;
	}

	@Override
	public Void visit(LtNode node) throws VisitingException {
		appendComparison(node, "<");
		return null;
	}

	@Override
	public Void visit(ModuloNode node) throws VisitingException {
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

		if (lhs.getType() == Type.DECIMAL && rhs.getType() == Type.DECIMAL) {
			expression.append("(").append(left).append(".toNumber()|0) % (").append(right).append(".toNumber()|0);");
		} else if (lhs.getType() == Type.DECIMAL && rhs.getType() == Type.INTEGER) {
			expression.append("(").append(left).append(".toNumber()|0) % ").append(right).append(";");
		} else if (lhs.getType() == Type.INTEGER && rhs.getType() == Type.DECIMAL) {
			expression.append(left).append(" % (").append(right).append(".toNumber()|0);");
		} else if (lhs.getType() == Type.INTEGER && rhs.getType() == Type.INTEGER) {
			expression.append(left).append(" % ").append(right).append(";");
		}

		expression.append("\n");

		return null;
	}

	@Override
	public Void visit(MultiplyNode node) throws VisitingException {
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

		if (lhs.getType() == Type.DECIMAL && rhs.getType() == Type.DECIMAL) {
			expression.append(left).append(".mul(").append(right).append(");");
		} else if (lhs.getType() == Type.DECIMAL && rhs.getType() == Type.INTEGER) {
			expression.append(left).append(".mul(new Decimal(").append(right).append("));");
		} else if (lhs.getType() == Type.INTEGER && rhs.getType() == Type.DECIMAL) {
			expression.append("(new Decimal(").append(left).append(")).mul(").append(right).append(");");
		} else if (lhs.getType() == Type.INTEGER && rhs.getType() == Type.INTEGER) {
			expression.append(left).append(" * ").append(right).append(";");
		}

		expression.append("\n");

		return null;
	}

	@Override
	public Void visit(NegativeNode node) throws VisitingException {
		ExpressionNode argument = node.getArgument();

		argument.accept(this);

		String name = determineVariableName(node);
		String arg = determineVariableName(argument);

		appendDefinition(node);

		expression.append(name).append(" = ");
		expression.append(arg).append("==null ? null : ");

		if (argument.getType() == Type.DECIMAL) {
			expression.append(arg).append(".neg();\n");
		} else {
			expression.append("-").append(arg).append(";\n");
		}

		return null;
	}

	@Override
	public Void visit(NeqNode node) throws VisitingException {
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
	public Void visit(NotNode node) throws VisitingException {
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
	public Void visit(NullNode node) throws VisitingException {
		appendDefinition(node);
		expression.append(determineVariableName(node)).append(" = null;\n");
		return null;
	}

	@Override
	public Void visit(OrNode node) throws VisitingException {
		ExpressionNode lhs = node.getLhs();
		ExpressionNode rhs = node.getRhs();

		String name = determineVariableName(node);
		String left = determineVariableName(lhs);
		String right = determineVariableName(rhs);

		lhs.accept(this);

		appendDefinition(node);

		expression.append("if (").append(left).append(" != null && ").append(left).append(") {")
						.append(name).append(" = true;")
					.append("} else {");

		rhs.accept(this);

		expression.append("if (").append(right).append(" != null && ").append(right).append(") {")
						.append(name).append(" = true;")
					.append("} else if (").append(left).append(" == null || ").append(right).append(" == null) {")
						.append(name).append(" = null;")
					.append("} else {")
						.append(name).append(" = false;")
					.append("}")
				.append("}\n");

		return null;
	}

	@Override
	public Void visit(PositiveNode node) throws VisitingException {
		ExpressionNode argument = node.getArgument();
		argument.accept(this);

		String name = determineVariableName(node);
		String arg = determineVariableName(argument);

		appendDefinition(node);
		expression.append(name).append(" = ").append(arg).append(";\n");
		return null;
	}

	@Override
	public Void visit(PowerNode node) throws VisitingException {
		ExpressionNode lhs = node.getLhs();
		ExpressionNode rhs = node.getRhs();

		lhs.accept(this);
		rhs.accept(this);

		String name = determineVariableName(node);
		String left = determineVariableName(lhs);
		String right = determineVariableName(rhs);

		appendDefinition(node);

		expression.append(name).append(" = ")
				.append(left).append("==null || ").append(right).append("==null ? null : ");

		if (lhs.getType() == Type.INTEGER && rhs.getType() == Type.INTEGER) {
			expression.append("Math.pow(").append(left).append(", ").append(right).append(")|0;\n");
			return null;
		}

		if (lhs.getType() == Type.INTEGER) {
			left = "new Decimal(" + left + ")";
		}

		if (rhs.getType() == Type.INTEGER) {
			right = "new Decimal(" + right + ")";
		}

		expression.append("Decimal.pow(").append(left).append(", ").append(right).append(");\n");

		return null;
	}

	@Override
	public Void visit(RootNode node) throws VisitingException {
		node.getStatementListNode().accept(this);
		return null;
	}

	@Override
	public Void visit(StatementListNode node) throws VisitingException {
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
	public Void visit(StringNode node) throws VisitingException {
		String value = "null";
		if (node.getValue() != null) {
			value = '"' + escapeJavaScript(node.getValue()) + '"';
		}
		appendDefinition(node);
		expression.append(determineVariableName(node)).append(" = ").append(value).append(";\n");
		return null;
	}

	@Override
	public Void visit(SubtractNode node) throws VisitingException {
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

		if (lhs.getType() == Type.DECIMAL && rhs.getType() == Type.DECIMAL) {
			expression.append(left).append(".sub(").append(right).append(");");
		} else if (lhs.getType() == Type.DECIMAL && rhs.getType() == Type.INTEGER) {
			expression.append(left).append(".sub(new Decimal(").append(right).append("));");
		} else if (lhs.getType() == Type.INTEGER && rhs.getType() == Type.DECIMAL) {
			expression.append("(new Decimal(").append(left).append(")).sub(").append(right).append(");");
		} else if (lhs.getType() == Type.INTEGER && rhs.getType() == Type.INTEGER) {
			expression.append(left).append(" - ").append(right).append(";");
		}

		expression.append("\n");

		return null;
	}

	@Override
	public Void visit(SumNode node) throws VisitingException {
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

		if (lhs.getType() == Type.DECIMAL && rhs.getType() == Type.DECIMAL) {
			expression.append(left).append(".add(").append(right).append(");");
		} else if (lhs.getType() == Type.DECIMAL && rhs.getType() == Type.INTEGER) {
			expression.append(left).append(".add(new Decimal(").append(right).append("));");
		} else if (lhs.getType() == Type.INTEGER && rhs.getType() == Type.DECIMAL) {
			expression.append("(new Decimal(").append(left).append(")).add(").append(right).append(");");
		} else if (lhs.getType() == Type.INTEGER && rhs.getType() == Type.INTEGER) {
			expression.append(left).append(" + ").append(right).append(";");
		}

		expression.append("\n");

		return null;
	}

	@Override
	public Void visit(VariableNode node) throws VisitingException {
		appendDefinition(node);

		String identifier = node.getIdentifier();
		String name = determineVariableName(node);

		expression.append(name).append(" = ").append(identifier).append(";\n");
		return null;
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
		if (fromType == Type.INTEGER && toType == Type.DECIMAL) {
			return "new Decimal(" + value + ")";
		}

		if (fromType == Type.DECIMAL && toType == Type.INTEGER) {
			return value + ".toNumber()|0";
		}

		return value;
	}

	/**
	 * Appends a comparison to the {@link StringBuilder}.
	 * @param node The node to build the comparison for.
	 * @param comparator A string representing the Java comparison operator.
	 * @throws VisitingException
	 */
	private void appendComparison(AbstractComparisonNode node, String comparator) throws VisitingException {
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

		if (lhs.getType() == Type.DECIMAL && rhs.getType() == Type.DECIMAL) {
			expression.append(left).append(".cmp(").append(right).append(") ").append(comparator).append(" 0;\n");
		} else if (lhs.getType() == Type.DECIMAL && rhs.getType() == Type.INTEGER) {
			expression.append(left).append(".cmp(new Decimal(").append(right).append(")) ").append(comparator).append(" 0;\n");
		} else if (lhs.getType() == Type.INTEGER && rhs.getType() == Type.DECIMAL) {
			expression.append("new Decimal(").append(left).append(").cmp(").append(right).append(") ").append(comparator).append(" 0;\n");
		} else if (lhs.getType() == Type.DATE && rhs.getType() == Type.DATE) {
			expression.append(left).append(".valueOf() ").append(comparator).append(" ").append(right).append(".valueOf();\n");
		} else {
			expression.append(left).append(" ").append(comparator).append(" ").append(right).append(";\n");
		}
	}

	/**
	 * Appends the definition of the variable of the given node.
	 * @param node The node to create the variable for.
	 */
	private void appendDefinition(ExpressionNode node) {
		expression.append("var ").append(determineVariableName(node)).append(";\n");
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
		return "Decimal.config({ precision: " + mathContext.getPrecision() + ", rounding: Decimal.ROUND_"
				+ mathContext.getRoundingMode() + " });\n";
	}

	/**
	 * Escapes a string so it can be used as a Java string in Java-source code.
	 * @param input The string to be escaped.
	 * @return The escaped string.
	 */
	public static String escapeJavaScript(String input) {
		StringBuilder result = new StringBuilder(input.length() + 50);

		for (int i = 0; i < input.length(); i++) {

			// Determine the escaped value of the currect character.
			char currentChar = input.charAt(i);
			String filtered = null;
			if (currentChar == '"') {
				filtered = "\\\"";
			} else if (currentChar == '\\') {
				filtered = "\\\\";
			} else if (currentChar == '\b') {
				filtered = "\\b";
			} else if (currentChar == '\n') {
				filtered = "\\n";
			} else if (currentChar == '\t') {
				filtered = "\\t";
			} else if (currentChar == '\f') {
				filtered = "\\f";
			} else if (currentChar == '\r') {
				filtered = "\\r";
			} else if (currentChar < 0x20 || currentChar > 0x7f) {
				filtered = Integer.toHexString(currentChar | 0x10000).substring(1);
			}

			if (filtered == null) {
				result.append(input.charAt(i));
			} else {
				result.append(filtered);
			}
		}

		return result.toString();
	}

}
