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
import org.gertje.abacus.symboltable.SymbolTable;
import org.gertje.abacus.util.JavaEscaper;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class Translator extends AbstractNodeVisitor<String, TranslationException> {

	/**
	 * De symboltable met de variabelen en de functies.
	 */
	protected SymbolTable symbolTable;

	/**
	 * Constructor.
	 */
	public Translator(SymbolTable symbolTable) {
		this.symbolTable = symbolTable;
	}

	public String translate(AbstractNode node) throws TranslationException {
		return node.accept(this);
	}

	@Override
	public String visit(AddNode node) throws TranslationException {
		AbstractNode lhs = node.getLhs();
		AbstractNode rhs = node.getRhs();

		String left = lhs.accept(this);
		String right = rhs.accept(this);

		return "CheckingExecutionHelper.add(" + left + ", " + right + ", "
				+ node.getToken().getLineNumber() + " , " + node.getToken().getColumnNumber() + ")";
	}

	@Override
	public String visit(AndNode node) throws TranslationException {
		AbstractNode lhs = node.getLhs();
		AbstractNode rhs = node.getRhs();

		String left = lhs.accept(this);
		String right = rhs.accept(this);

		return wrapInFunctor("Boolean",
				"Object bool = " + left + ";\n" +
				"if (!(bool instanceof Boolean)) {\n" +
				"	throw new JavaExecutionException(\"Expected two boolean parameters to AND-expression.\", "
						+ node.getToken().getLineNumber() + " , " + node.getToken().getColumnNumber() + ");\n" +
				"}\n" +
				"if (!((Boolean)bool)) return Boolean.FALSE;\n" +
				"bool = " + right + "\n" +
				"if (!(bool instanceof Boolean)) {\n" +
				"	throw new JavaExecutionException(\"Expected two boolean parameters to AND-expression.\", "
						+ node.getToken().getLineNumber() + " , " + node.getToken().getColumnNumber() + ");\n" +
				"}\n" +
				"return (Boolean)bool;\n"
		);
	}

	@Override
	public String visit(AssignmentNode node) throws TranslationException {
		AbstractNode lhs = node.getLhs();
		AbstractNode rhs = node.getRhs();

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

		return wrapInFunctor("Object",
				"symbolTable.setVariableValue(\"" + identifier + "\", " + result + ");\n" +
				"return symbolTable.getVariableValue(\"" + identifier + "\");\n"
		);
	}

	@Override
	public String visit(BooleanNode node) throws TranslationException {
		return node.getValue() ? "Boolean.TRUE" : "Boolean.FALSE";
	}

	@Override
	public String visit(DateNode node) throws TranslationException {
		return "new Date(\"" + node.getValue().getTime() + "\")";
	}

	@Override
	public String visit(DivideNode node) throws TranslationException {
		AbstractNode lhs = node.getLhs();
		AbstractNode rhs = node.getRhs();

		String left = lhs.accept(this);
		String right = rhs.accept(this);

		return "CheckingExecutionHelper.divide(" + left + ", " + right + ", "
				+ node.getToken().getLineNumber() + " , " + node.getToken().getColumnNumber() + ")";
	}

	@Override
	public String visit(EqNode node) throws TranslationException {
		AbstractNode lhs = node.getLhs();
		AbstractNode rhs = node.getRhs();

		String left = lhs.accept(this);
		String right = rhs.accept(this);

		return "CheckingExecutionHelper.eq(" + left + ", " + right + ", "
				+ node.getToken().getLineNumber() + " , " + node.getToken().getColumnNumber() + ")";
	}

	@Override
	public String visit(FactorNode node) throws TranslationException {
		return node.getArgument().accept(this);
	}

	@Override
	public String visit(FloatNode node) throws TranslationException {
		BigDecimal value = node.getValue();
		return "BigDecimal.valueOf(" + value.unscaledValue().longValue() + ", " + value.scale() + ")";
	}

	@Override
	public String visit(FunctionNode node) throws TranslationException {
		// TODO: Afmaken.
		List<AbstractNode> parameters = node.getParameters();
		String identifier = node.getIdentifier();

		// Maak een lijst met alle resultaten van de evaluatie van de parameters.
		List<Object> paramResults = new ArrayList<Object>();

		// Maak een lijst met alle types van de parameters.
		List<Class<?>> paramTypes = new ArrayList<Class<?>>();

		// Maak de functie-aanroep.
		StringBuilder buffer = new StringBuilder("function_").append(identifier).append("(");

		// Loop over alle nodes heen en vul de lijsten met de geevaluuerde waarde en het type.
		for (int i = 0; i < parameters.size(); i++) {
			AbstractNode parameter = parameters.get(i);
			String value = parameter.accept(this);
			if (i != 0) {
				buffer.append(", ");
			}
			buffer.append("(").append(parameter.getType().getName()).append(")(").append(value).append(")");
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
		AbstractNode lhs = node.getLhs();
		AbstractNode rhs = node.getRhs();

		String left = lhs.accept(this);
		String right = rhs.accept(this);

		return "CheckingExecutionHelper.geq(" + left + ", " + right + ", "
				+ node.getToken().getLineNumber() + " , " + node.getToken().getColumnNumber() + ")";
	}

	@Override
	public String visit(GtNode node) throws TranslationException {
		AbstractNode lhs = node.getLhs();
		AbstractNode rhs = node.getRhs();

		String left = lhs.accept(this);
		String right = rhs.accept(this);

		return "CheckingExecutionHelper.gt(" + left + ", " + right + ", "
				+ node.getToken().getLineNumber() + " , " + node.getToken().getColumnNumber() + ")";
	}

	@Override
	public String visit(IfNode node) throws TranslationException {
		AbstractNode condition = node.getCondition();
		AbstractNode ifBody = node.getIfBody();
		AbstractNode elseBody = node.getElseBody();

		// Evauleer de conditie.
		String cond = condition.accept(this);
		String ifBodyResult = ifBody.accept(this);
		String elseBodyResult = elseBody.accept(this);

		return wrapInFunctor("Object",
				"Object condition = " + cond + ";\n" +
				"if (!(condition instanceof Boolean)) {\n" +
				"	throw new JavaExecutionException(\"Expected boolean parameter to IF-expression.\", "
						+ node.getToken().getLineNumber() + " , " + node.getToken().getColumnNumber() + ");\n" +
				"}\n" +
				"if (condition == null) return null;\n" +
				"Boolean bool = (Boolean)condition;\n" +
				"if (bool) return " + ifBodyResult + ";" +
				"return " + elseBodyResult + ";\n"
		);
	}

	@Override
	public String visit(IntegerNode node) throws TranslationException {
		return "BigInteger.valueOf(" + node.getValue().longValue() + ")";
	}

	@Override
	public String visit(LeqNode node) throws TranslationException {
		AbstractNode lhs = node.getLhs();
		AbstractNode rhs = node.getRhs();

		String left = lhs.accept(this);
		String right = rhs.accept(this);

		return "CheckingExecutionHelper.leq(" + left + ", " + right + ", "
				+ node.getToken().getLineNumber() + " , " + node.getToken().getColumnNumber() + ")";
	}

	@Override
	public String visit(LtNode node) throws TranslationException {
		AbstractNode lhs = node.getLhs();
		AbstractNode rhs = node.getRhs();

		String left = lhs.accept(this);
		String right = rhs.accept(this);

		return "CheckingExecutionHelper.lt(" + left + ", " + right + ", "
				+ node.getToken().getLineNumber() + " , " + node.getToken().getColumnNumber() + ")";
	}

	@Override
	public String visit(ModuloNode node) throws TranslationException {
		AbstractNode lhs = node.getLhs();
		AbstractNode rhs = node.getRhs();

		String left = lhs.accept(this);
		String right = rhs.accept(this);

		return "CheckingExecutionHelper.modulo(" + left + ", " + right + ", "
				+ node.getToken().getLineNumber() + " , " + node.getToken().getColumnNumber() + ")";
	}

	@Override
	public String visit(MultiplyNode node) throws TranslationException {
		AbstractNode lhs = node.getLhs();
		AbstractNode rhs = node.getRhs();

		String left = lhs.accept(this);
		String right = rhs.accept(this);

		return "CheckingExecutionHelper.multiply(" + left + ", " + right + ", "
				+ node.getToken().getLineNumber() + " , " + node.getToken().getColumnNumber() + ")";
	}

	@Override
	public String visit(NegativeNode node) throws TranslationException {
		AbstractNode argument = node.getArgument();

		// Bepaal het getal dat we negatief gaan maken.
		String number = argument.accept(this);

		return "CheckingExecutionHelper.negative(" + number + ", "
				+ node.getToken().getLineNumber() + " , " + node.getToken().getColumnNumber() + ")";
	}

	@Override
	public String visit(NeqNode node) throws TranslationException {
		AbstractNode lhs = node.getLhs();
		AbstractNode rhs = node.getRhs();

		String left = lhs.accept(this);
		String right = rhs.accept(this);

		return "CheckingExecutionHelper.neq(" + left + ", " + right + ", "
				+ node.getToken().getLineNumber() + " , " + node.getToken().getColumnNumber() + ")";
	}

	@Override
	public String visit(NotNode node) throws TranslationException {
		AbstractNode argument = node.getArgument();

		// Bepaal de waarde van de boolean.
		String bool = argument.accept(this);

		return "CheckingExecutionHelper.not(" + bool + ", "
				+ node.getToken().getLineNumber() + " , " + node.getToken().getColumnNumber() + ")";
	}

	@Override
	public String visit(NullNode node) throws TranslationException {
		return "null";
	}

	@Override
	public String visit(OrNode node) throws TranslationException {
		AbstractNode lhs = node.getLhs();
		AbstractNode rhs = node.getRhs();

		String left = lhs.accept(this);
		String right = rhs.accept(this);

		return wrapInFunctor("Boolean",
				"Object bool = " + left + ";\n" +
				"if (!(bool instanceof Boolean)) {\n" +
				"	throw new JavaExecutionException(\"Expected a boolean expression in NotNode.\", "
						+ node.getToken().getLineNumber() + " , " + node.getToken().getColumnNumber() + ");\n" +
				"}\n" +
				"if (bool) return Boolean.TRUE;\n" +
				"bool = " + right + "\n" +
				"if (!(bool instanceof Boolean)) {\n" +
				"	throw new JavaExecutionException(\"Expected a boolean expression in NotNode.\", "
						+ node.getToken().getLineNumber() + " , " + node.getToken().getColumnNumber() + ");\n" +
				"}\n" +
				"return bool;\n"
		);
	}

	@Override
	public String visit(PositiveNode node) throws TranslationException {
		AbstractNode argument = node.getArgument();
		String pos = argument.accept(this);

		return pos;
	}

	@Override
	public String visit(PowerNode node) throws TranslationException {
		AbstractNode base = node.getBase();
		AbstractNode power = node.getPower();

		String baseValue = base.accept(this);
		String powerValue = power.accept(this);

		return "CheckingExecutionHelper.power(" + baseValue + ", " + powerValue + ", "
				+ node.getToken().getLineNumber() + " , " + node.getToken().getColumnNumber() + ")";
	}

	@Override
	public String visit(StatementListNode node) throws TranslationException {
		// Evalueer alle AbstractNodes en geef het resultaat van de laatste node terug.
		StringBuilder buffer = new StringBuilder();

		for (int i = 0; i < node.size(); i++) {
			String subNode = node.get(i).accept(this);
			buffer.append("\t\t").append(i == node.size() - 1 ? "return " : "").append(subNode).append(";\n");
		}

		return wrapInFunctor(node.getType().getName(), buffer.toString());
	}

	@Override
	public String visit(StringNode node) throws TranslationException {
		return '"' + JavaEscaper.escapeJava(node.getValue()) + '"';
	}

	@Override
	public String visit(SubstractNode node) throws TranslationException {
		AbstractNode lhs = node.getLhs();
		AbstractNode rhs = node.getRhs();

		String left = lhs.accept(this);
		String right = rhs.accept(this);

		return "CheckingExecutionHelper.substract(" + left + ", " + right + ", "
				+ node.getToken().getLineNumber() + " , " + node.getToken().getColumnNumber() + ")";
	}

	@Override
	public String visit(VariableNode node) throws TranslationException {
		String identifier = node.getIdentifier();

		return wrapInFunctor("Object",
				"try {\n" +
				"	return symbolTable.getVariableValue(\"" + identifier + "\");\n" +
				"} catch (NoSuchVariableException e) {\n" +
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
	 * @param returnType The functor´s return type
	 * @param body The functor's body.
	 * @return The functor with the specified type and body.
	 */
	public static String wrapInFunctor(String returnType, String body) {
		return "(new F<" + returnType + ">() {\n" +
				"	public " + returnType + " f() throws Exception {\n" +
				body +
				"	}\n" +
				"}).f()\n";
	}
}
