package org.gertje.abacus.translator.java.nodevisitors;

import org.gertje.abacus.context.AbacusContext;
import org.gertje.abacus.nodes.AddNode;
import org.gertje.abacus.nodes.AndNode;
import org.gertje.abacus.nodes.AssignmentNode;
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
import org.gertje.abacus.nodes.NotNode;
import org.gertje.abacus.nodes.NullNode;
import org.gertje.abacus.nodes.OrNode;
import org.gertje.abacus.nodes.PositiveNode;
import org.gertje.abacus.nodes.PowerNode;
import org.gertje.abacus.nodes.StatementListNode;
import org.gertje.abacus.nodes.StringNode;
import org.gertje.abacus.nodes.SubstractNode;
import org.gertje.abacus.nodes.VariableNode;
import org.gertje.abacus.nodevisitors.AbstractNodeVisitor;
import org.gertje.abacus.symboltable.SymbolTable;
import org.gertje.abacus.translator.java.util.JavaEscaper;
import org.gertje.abacus.types.Type;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class Translator extends AbstractNodeVisitor<String, TranslationException> {

	/**
	 * De context waarbinnen de interpreter werkt.
	 */
	private final AbacusContext abacusContext;

	/**
	 * De symboltable met de variabelen en de functies.
	 */
	protected SymbolTable symbolTable;

	/**
	 * Constructor.
	 */
	public Translator(AbacusContext abacusContext) {
		this.abacusContext = abacusContext;
		this.symbolTable = abacusContext.getSymbolTable();
	}

	public String translate(Node node) throws TranslationException {
		return node.accept(this);
	}

	@Override
	public String visit(AddNode node) throws TranslationException {
		Node lhs = node.getLhs();
		Node rhs = node.getRhs();

		String left = lhs.accept(this);
		String right = rhs.accept(this);

		return "(" + determineJavaType(node.getType()) + ") EvaluationHelper.add(" + left + ", "
				+ determineFullTypeName(lhs.getType())
				+ ", " + right + ", " + determineFullTypeName(rhs.getType()) + ")";
	}

	@Override
	public String visit(AndNode node) throws TranslationException {
		Node lhs = node.getLhs();
		Node rhs = node.getRhs();

		String left = lhs.accept(this);
		String right = rhs.accept(this);

		return wrapInFunctor(Type.BOOLEAN,
				"Boolean left = (Boolean)" + left + ";\n" +
				"if (left != null && !left.booleanValue()) return Boolean.FALSE;\n" +
				"Boolean right = (Boolean)" + right + ";\n" +
				"if (right != null && !right.booleanValue()) return Boolean.FALSE;\n" +
				"if (left == null || right == null) return null;\n" +
				"return Boolean.TRUE;\n"
		);
	}

	@Override
	public String visit(AssignmentNode node) throws TranslationException {
		Node lhs = node.getLhs();
		Node rhs = node.getRhs();

		// Evalueer de rechterkant van de toekenning.
		String result = rhs.accept(this);

		// Als de linkerkant geen VariabeleNode is EN de linkerkant is geen AssignmentNode met aan de rechterkant een
		// VariableNode, dan gooien we een exceptie.
		if (!(lhs instanceof VariableNode)
				&& !((lhs instanceof AssignmentNode) && (((AssignmentNode)lhs).getRhs() instanceof VariableNode))) {
			throw new TranslationException("Left side of assignment should be a variable or an assignment.",
					node);
		}

		String identifier;
		// Bepaal de identifier
		// Wanneer de linkerkant een variabele is kunnen we het direct in de variabele zetten, anders moeten we eerst
		// de variabele uit de rechterkant halen.
		if (lhs instanceof VariableNode) {
			identifier = ((VariableNode) lhs).getIdentifier();
		} else {
			identifier = ((VariableNode) ((AssignmentNode)lhs).getRhs()).getIdentifier();
		}

		return wrapInFunctor(node.getType(),
				"symbolTable.setVariableValue(\"" + identifier + "\", " + determineFullTypeName(rhs.getType()) + ", "
						+ result + ");\n" +
				"return (" + determineJavaType(node.getType()) + ") symbolTable.getVariableValue(\"" + identifier
						+ "\");\n"
		);
	}

	@Override
	public String visit(BooleanNode node) throws TranslationException {
		if (node.getValue() == null) {
			return "null";
		}
		return node.getValue() ? "Boolean.TRUE" : "Boolean.FALSE";
	}

	@Override
	public String visit(DateNode node) throws TranslationException {
		if (node.getValue() == null) {
			return "null";
		}
		return "new java.sql.Date(" + node.getValue().getTime() + "L)";
	}

	@Override
	public String visit(DecimalNode node) throws TranslationException {
		if (node.getValue() == null) {
			return "null";
		}
		return "new java.math.BigDecimal(\"" + node.getValue().toString() + "\")";
	}

	@Override
	public String visit(DivideNode node) throws TranslationException {
		Node lhs = node.getLhs();
		Node rhs = node.getRhs();

		String left = lhs.accept(this);
		String right = rhs.accept(this);

		return "(" + determineJavaType(node.getType()) + ") EvaluationHelper.divide(" + left + ", "
				+ determineFullTypeName(lhs.getType()) + ", " + right + " , " + determineFullTypeName(rhs.getType())
				+ ", " + determineMathContext() + ")";
	}

	@Override
	public String visit(EqNode node) throws TranslationException {
		Node lhs = node.getLhs();
		Node rhs = node.getRhs();

		String left = lhs.accept(this);
		String right = rhs.accept(this);

		return "EvaluationHelper.eq(" + left + ", " + determineFullTypeName(lhs.getType()) + ", " + right + ", "
				+ determineFullTypeName(rhs.getType()) + ")";
	}

	@Override
	public String visit(FactorNode node) throws TranslationException {
		return node.getArgument().accept(this);
	}

	@Override
	public String visit(FunctionNode node) throws TranslationException {
		// TODO: Afmaken.
		List<Node> parameters = node.getParameters();
		String identifier = node.getIdentifier();

		// Maak een lijst met alle resultaten van de evaluatie van de parameters.
		List<Object> paramResults = new ArrayList<>();

		// Maak een lijst met alle types van de parameters.
		List<Type> paramTypes = new ArrayList<>();

		// Maak de functie-aanroep.
		StringBuilder buffer = new StringBuilder("function_").append(identifier).append("(");

		// Loop over alle nodes heen en vul de lijsten met de geevaluuerde waarde en het type.
		for (int i = 0; i < parameters.size(); i++) {
			Node parameter = parameters.get(i);
			String value = parameter.accept(this);
			if (i != 0) {
				buffer.append(", ");
			}
			buffer.append("(").append(determineJavaType(parameter.getType())).append(")(").append(value).append(")");
			paramResults.add(value);
			paramTypes.add(parameter.getType());
		}

		buffer.append(")");

		// Controleer of de variabele bestaat. Als deze niet bestaat gooien we een exceptie.
		if (!symbolTable.getExistsFunction(identifier, paramTypes)) {
			throw new TranslationException("Function '" + identifier + "' does not exist.", node);
		}

		return buffer.toString();
	}

	@Override
	public String visit(GeqNode node) throws TranslationException {
		Node lhs = node.getLhs();
		Node rhs = node.getRhs();

		String left = lhs.accept(this);
		String right = rhs.accept(this);

		return "EvaluationHelper.geq(" + left + ", " + determineFullTypeName(lhs.getType()) + ", " + right + ", "
				+ determineFullTypeName(rhs.getType()) + ")";
	}

	@Override
	public String visit(GtNode node) throws TranslationException {
		Node lhs = node.getLhs();
		Node rhs = node.getRhs();

		String left = lhs.accept(this);
		String right = rhs.accept(this);

		return "EvaluationHelper.gt(" + left + ", " + determineFullTypeName(lhs.getType()) + ", " + right + ", "
				+ determineFullTypeName(rhs.getType()) + ")";
	}

	@Override
	public String visit(IfNode node) throws TranslationException {
		Node condition = node.getCondition();
		Node ifBody = node.getIfBody();
		Node elseBody = node.getElseBody();

		// Evalueer de conditie.
		String cond = condition.accept(this);
		String ifBodyResult = addTypeCast(ifBody.accept(this), ifBody.getType(), node.getType());
		String elseBodyResult = addTypeCast(elseBody.accept(this), elseBody.getType(), node.getType());

		return wrapInFunctor(node.getType(),
				"Boolean condition = " + cond + ";\n" +
				"if (condition == null) return null;\n" +
				"if (condition) return " + ifBodyResult + ";" +
				"return " + elseBodyResult + ";\n"
		);
	}

	@Override
	public String visit(IntegerNode node) throws TranslationException {
		if (node.getValue() == null) {
			return "null";
		}
		return "new java.math.BigInteger(\"" + node.getValue().toString() + "\")";
	}

	@Override
	public String visit(LeqNode node) throws TranslationException {
		Node lhs = node.getLhs();
		Node rhs = node.getRhs();

		String left = lhs.accept(this);
		String right = rhs.accept(this);

		return "EvaluationHelper.leq(" + left + ", " + determineFullTypeName(lhs.getType()) + ", " + right + ", "
				+ determineFullTypeName(rhs.getType()) + ")";
	}

	@Override
	public String visit(LtNode node) throws TranslationException {
		Node lhs = node.getLhs();
		Node rhs = node.getRhs();

		String left = lhs.accept(this);
		String right = rhs.accept(this);

		return "EvaluationHelper.lt(" + left + ", " + determineFullTypeName(lhs.getType()) + ", " + right + ", "
				+ determineFullTypeName(rhs.getType()) + ")";
	}

	@Override
	public String visit(ModuloNode node) throws TranslationException {
		Node lhs = node.getLhs();
		Node rhs = node.getRhs();

		String left = lhs.accept(this);
		String right = rhs.accept(this);

		return "EvaluationHelper.modulo(" + left + ", " + determineFullTypeName(lhs.getType()) + ", " + right + ", "
				+ determineFullTypeName(rhs.getType()) + ")";
	}

	@Override
	public String visit(MultiplyNode node) throws TranslationException {
		Node lhs = node.getLhs();
		Node rhs = node.getRhs();

		String left = lhs.accept(this);
		String right = rhs.accept(this);

		return "(" + determineJavaType(node.getType()) + ") EvaluationHelper.multiply(" + left + ", "
				+ determineFullTypeName(lhs.getType()) + ", " + right + ", " + determineFullTypeName(rhs.getType())
				+ ")";
	}

	@Override
	public String visit(NegativeNode node) throws TranslationException {
		Node argument = node.getArgument();

		// Bepaal het getal dat we negatief gaan maken.
		String number = argument.accept(this);

		return "(" + determineJavaType(node.getType()) + ") EvaluationHelper.negative(" + number + ", "
				+ determineFullTypeName(argument.getType()) + ")";
	}

	@Override
	public String visit(NeqNode node) throws TranslationException {
		Node lhs = node.getLhs();
		Node rhs = node.getRhs();

		String left = lhs.accept(this);
		String right = rhs.accept(this);

		return "EvaluationHelper.neq(" + left + ", " + determineFullTypeName(lhs.getType()) + ", " + right + ", "
				+ determineFullTypeName(rhs.getType()) + ")";
	}

	@Override
	public String visit(NotNode node) throws TranslationException {
		Node argument = node.getArgument();

		// Bepaal de waarde van de boolean.
		String bool = argument.accept(this);

		return "EvaluationHelper.not(" + bool + ")";
	}

	@Override
	public String visit(NullNode node) throws TranslationException {
		return "null";
	}

	@Override
	public String visit(OrNode node) throws TranslationException {
		Node lhs = node.getLhs();
		Node rhs = node.getRhs();

		String left = lhs.accept(this);
		String right = rhs.accept(this);

		return wrapInFunctor(Type.BOOLEAN,
				"Boolean left = (Boolean)" + left + ";\n" +
				"if (left != null && left.booleanValue()) return Boolean.TRUE;\n" +
				"Boolean right = (Boolean)" + right + ";\n" +
				"if (right != null && right.booleanValue()) return Boolean.TRUE;\n" +
				"if (left == null || right == null) return null;\n" +
				"return Boolean.FALSE;\n"
		);
	}

	@Override
	public String visit(PositiveNode node) throws TranslationException {
		Node argument = node.getArgument();
		String pos = argument.accept(this);

		return pos;
	}

	@Override
	public String visit(PowerNode node) throws TranslationException {
		Node base = node.getBase();
		Node power = node.getPower();

		String baseValue = base.accept(this);
		String powerValue = power.accept(this);

		return "EvaluationHelper.power(" + baseValue + ", " + determineFullTypeName(base.getType()) + ", " + powerValue
				+ ", " + determineFullTypeName(power.getType()) + ")";
	}

	@Override
	public String visit(StatementListNode node) throws TranslationException {
		// Evalueer alle AbstractNodes en geef het resultaat van de laatste node terug.
		StringBuilder buffer = new StringBuilder();

		// Get an iterator over the child nodes.
		Iterator<Node> it = node.iterator();
		while (it.hasNext()) {
			Node subNode = it.next();
			String subNodeJava = subNode.accept(this);
			// If this is not the last node in the list, we need to wrap it in a functor to create a statement of it.
			if (it.hasNext()) {
				subNodeJava = wrapInFunctor(node.getType(), "return " + subNodeJava + ";");
			}
			buffer.append("\t\t").append(!it.hasNext() ? "return " : "").append(subNodeJava).append(";\n");
		}

		return wrapInFunctor(node.getType(), buffer.toString());
	}

	@Override
	public String visit(StringNode node) throws TranslationException {
		if (node.getValue() == null) {
			return "null";
		}
		return '"' + JavaEscaper.escapeJava(node.getValue()) + '"';
	}

	@Override
	public String visit(SubstractNode node) throws TranslationException {
		Node lhs = node.getLhs();
		Node rhs = node.getRhs();

		String left = lhs.accept(this);
		String right = rhs.accept(this);

		return "(" + determineJavaType(node.getType()) + ") EvaluationHelper.substract(" + left + ", "
				+ determineFullTypeName(lhs.getType()) + ", " + right + ", " + determineFullTypeName(rhs.getType())
				+ ")";
	}

	@Override
	public String visit(VariableNode node) throws TranslationException {
		String identifier = node.getIdentifier();

		return wrapInFunctor(node.getType(),
				"try {\n" +
				"	return (" + determineJavaType(node.getType()) + ") symbolTable.getVariableValue(\"" + identifier
						+ "\");\n" +
				"} catch (org.gertje.abacus.symboltable.NoSuchVariableException e) {\n" +
				"	throw new JavaExecutionException(\"Variable '" + identifier + "' does not exist.\", "
						+ node.getToken().getLineNumber() + " , " + node.getToken().getColumnNumber() + ");\n" +
				"}\n"
		);
	}

	/**
	 * Wraps the body in a functor with the specified return type;
	 * <pre>
	 * {@code
	 * (new F< returnType >() {
	 *     public returnType f() {
	 *         body
	 *     }
	 * })
	 * }
	 * </pre>
	 * @param type The functor´s return type
	 * @param body The functor's body.
	 * @return The functor with the specified type and body.
	 */
	public static String wrapInFunctor(Type type, String body) {
		return "(new F<" + determineJavaType(type) + ">() {\n" +
				"	public " + determineJavaType(type) + " f() throws Exception {\n" +
				body +
				"	}\n" +
				"}).f()\n";
	}

	/**
	 * Bepaalt het Java type o.b.v. het type.
	 * @param type Het type waarvoor we het Java type willen weten.
	 * @return Het Java type.
	 */
	private static String determineJavaType(Type type) {
		if (type == null) {
			return "Object";
		}

		switch (type) {
			case INTEGER: return BigInteger.class.getName();
			case BOOLEAN: return Boolean.class.getName();
			case STRING: return String.class.getName();
			case DECIMAL: return BigDecimal.class.getName();
			default: return Date.class.getName();
		}
	}

	/**
	 * Determines the full Java name of the given type.
	 * @param type The type to get the full name from.
	 * @return The full Java name of the given type.
	 */
	private String determineFullTypeName(Type type) {
		if (type == null) {
			return "null";
		}
		return "org.gertje.abacus.types.Type." + type;
	}

	/**
	 * Casts the given expression from one type to another.
	 * @param fromType The original type of the expression.
	 * @param toType The resulting type of the expression.
	 * @return The given expression casted to the desired type.
	 */
	private String addTypeCast(String expression, Type fromType, Type toType) {
		if (fromType == toType || fromType == null || toType == null) {
			return expression;
		}

		// Cast the expression from integer to decimal.
		if (fromType == Type.INTEGER && toType == Type.DECIMAL) {
			return "new java.math.BigDecimal(" + expression + ", " + determineMathContext() + ")";
		}

		// We only need to cast from integer to decimal.
		throw new IllegalStateException("An unexpected type cast was needed.");
	}

	/**
	 * Determines the Java-string representation of the {@link MathContext}.
	 * @return The Java-string representation of the {@link MathContext}.
	 */
	private String determineMathContext() {
		MathContext mathContext = abacusContext.getMathContext();
		return "new java.math.MathContext(" + mathContext.getPrecision() + ", java.math.RoundingMode."
				+ mathContext.getRoundingMode() + ")";
	}
}
