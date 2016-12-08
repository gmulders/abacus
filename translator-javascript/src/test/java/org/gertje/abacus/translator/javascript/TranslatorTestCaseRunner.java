package org.gertje.abacus.translator.javascript;

import org.gertje.abacus.AbacusTestCase;
import org.gertje.abacus.AbstractTestCaseRunner;
import org.gertje.abacus.context.AbacusContext;
import org.gertje.abacus.context.SimpleAbacusContext;
import org.gertje.abacus.exception.CompilerException;
import org.gertje.abacus.lexer.AbacusLexer;
import org.gertje.abacus.lexer.Lexer;
import org.gertje.abacus.nodes.AbacusNodeFactory;
import org.gertje.abacus.nodes.Node;
import org.gertje.abacus.nodes.NodeFactory;
import org.gertje.abacus.nodes.RootNode;
import org.gertje.abacus.nodevisitors.SemanticsChecker;
import org.gertje.abacus.nodevisitors.Simplifier;
import org.gertje.abacus.nodevisitors.VisitingException;
import org.gertje.abacus.parser.Parser;
import org.gertje.abacus.symboltable.SymbolTable;
import org.gertje.abacus.translator.javascript.nodevisitors.JavaScriptTranslator;
import org.gertje.abacus.types.Type;
import org.junit.Assert;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Runs the test case for the JavaScript translator.
 */
public class TranslatorTestCaseRunner extends AbstractTestCaseRunner {

	@Override
	public void runTestCase() {
		// Maak een nieuwe symboltable en vul deze met wat waarden.
		SymbolTable sym = createSymbolTable();

		NodeFactory nodeFactory = new AbacusNodeFactory();

		Lexer lexer = new AbacusLexer(abacusTestCase.expression);
		Parser parser = new Parser(lexer, nodeFactory);

		RootNode tree;
		try {
			tree = parser.parse();
		} catch (CompilerException e) {
			if (!abacusTestCase.failsWithException) {
				Assert.fail(createMessage("Unexpected exception.", e));
			}
			return;
		}

		AbacusContext abacusContext = new SimpleAbacusContext(sym);
		SemanticsChecker semanticsChecker = new SemanticsChecker(sym);
		Simplifier simplifier = new Simplifier(abacusContext, nodeFactory);
		JavaScriptTranslator translator = new JavaScriptTranslator(abacusContext);

		String javascript;
		try {
			semanticsChecker.check(tree);

			if (!checkReturnType(tree.getType())) {
				Assert.fail(createMessage("Incorrect return type."));
			}

			Node node = simplifier.simplify(tree);

			javascript = translator.translate(node);
		} catch (VisitingException e) {
			if (!abacusTestCase.failsWithException) {
				Assert.fail(createMessage("Unexpected exception.", e));
			}
			return;
		}

		if (abacusTestCase.failsWithException) {
			Assert.fail(createMessage("Expected exception, but none was thrown."));
		}

		ScriptEngineManager scriptEngineManager = new ScriptEngineManager();
		ScriptEngine nashorn = scriptEngineManager.getEngineByName("nashorn");

		try {
			javascript = createJavaScript(javascript);
//			System.out.println(javascript);
			nashorn.eval(javascript);
		} catch (ScriptException e) {
			Assert.fail(createMessage(e.getMessage()));
		}

		boolean error = (Boolean)nashorn.get("error");
		String message = (String)nashorn.get("message");

		if (error) {
//			System.out.println(javascript);
			Assert.fail(createMessage(message));
		}
	}

	/**
	 * Creates a piece of JavaScript that runs the expression and checks if the returned value is correct.
	 * @param expression The expression to run.
	 * @return JavaScipt script.
	 */
	private String createJavaScript(String expression) {
		return
				"// " + abacusTestCase.expression + "\n" +
				"load('classpath:decimal.min.js');\n" +
				"var function_rand = Math.random;\n" +
				"var error = false;\n" +
				"var message = '';\n" +
				"\n" +
				createJavaScriptForSymbolTable() +
				"\n" +
				"var returnValue = " + expression + ";\n" +
				"\n" +
				"if (" + determineUnequalsCheck("returnValue", abacusTestCase.returnValue.value,
						abacusTestCase.returnValue.type) + ") {\n" +
				"\terror = true;\n" +
				"\tmessage = 'Incorrect return value; ' + returnValue;\n" +
				"}\n" +
				"\n" +
				createJavaScriptForCheckSymbolTable();
	}

	/**
	 * Creates a piece of JavaScript that declares and initializes all variables from the symbol table.
	 * @return a piece of JavaScript that declares and initializes all variables from the symbol table.
	 */
	private String createJavaScriptForSymbolTable() {
		if (abacusTestCase.variableListBefore == null) {
			return "";
		}

		StringBuilder builder = new StringBuilder();

		for (AbacusTestCase.Value value : abacusTestCase.variableListBefore) {
			builder.append("var ").append(value.name).append(" = ")
					.append(formatValueForJavaScript(value.value, value.type))
					.append(";\n");
		}

		return builder.toString();
	}

	/**
	 * Creates a piece of JavaScript that checks whether the (JavaScript) variables have the correct values after
	 * running the expression.
	 * @return a piece of JavaScript that checks whether the (JavaScript) variables have the correct values after
	 * running the expression.
	 */
	private String createJavaScriptForCheckSymbolTable() {
		if (abacusTestCase.variableListAfter == null) {
			return "";
		}

		StringBuilder builder = new StringBuilder();

		for (AbacusTestCase.Value value : abacusTestCase.variableListAfter) {
			builder.append("if (");

			if (value.type.isArray()) {
				List<String> parts = new ArrayList<>();
				createJavaScriptForCompareArrays(parts, value.name, (ArrayList)value.value, value.type,
						new LinkedList<Integer>());
				builder.append(joinStrings(parts, "||"));
			} else {
				builder.append(determineUnequalsCheck(value.name, value.value, value.type));
			}

			builder.append(") {\n")
						.append("\terror = true;\n")
						.append("\tmessage = 'Incorrect value for ").append(value.name).append(":' + ")
						.append(value.name).append(";\n")
					.append("}\n");
		}

		return builder.toString();
	}

	public static String joinStrings(List<String> strings, String separator) {
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < strings.size(); i++) {
			sb.append(strings.get(i));
			if(i < strings.size() - 1)
				sb.append(separator);
		}
		return sb.toString();
	}

	private void createJavaScriptForCompareArrays(List<String> parts, String name, ArrayList array, Type type,
			LinkedList<Integer> indices) {

		if (array == null) {
			String nameIndex = "";
			for (Integer index : indices) {
				nameIndex = "[" + index + "]" + nameIndex;
			}
			parts.add(name + nameIndex + " != null");
			return;
		}

		Type subType = type.determineComponentType();
		for (int i = 0; i < array.size(); i++) {
			indices.push(i);
			if (subType.isArray()) {
				createJavaScriptForCompareArrays(parts, name, (ArrayList)array.get(i), subType, indices);
			} else {
				String nameIndex = "";
				for (Integer index : indices) {
					nameIndex = "[" + index + "]" + nameIndex;
				}
				parts.add(determineUnequalsCheck(name + nameIndex, array.get(i), subType));
			}
			indices.pop();
		}
	}

	/**
	 * Creates JavaScript that determines whether a variable and a value are unequal.
	 * @param name The name of the variable.
	 * @param value The value.
	 * @param type The type of the variable.
	 * @return The JavaScript that does the comparison.
	 */
	private String determineUnequalsCheck(String name, Object value, Type type) {
		String javaScriptValue = formatValueForJavaScript(value, type);

		if (type == Type.DATE) {
			return "(" + name + " == null ? null : " + name + ".valueOf())"
					+ " !== "
					+ "(" + javaScriptValue + " == null ? null : " + javaScriptValue + ".valueOf()) ";
		}

		if (Type.equals(type, Type.DECIMAL)) {
			return "(" + name + " == null ? " + javaScriptValue + " != null : " + name + ".cmp(" + javaScriptValue + ") != 0)";
		}

		return name + " !== " + javaScriptValue;
	}

	/**
	 * Formats the value for JavaScript.
	 * @param value The value to be formatted.
	 * @param type The type of the value.
	 * @return The value formatted for JavaScript.
	 */
	private String formatValueForJavaScript(Object value, Type type) {
		if (Type.equals(type, Type.STRING) && value != null) {
			return "'" + value + "'";
		}

		if (Type.equals(type, Type.DATE) && value != null) {
			Date date = Date.valueOf(value.toString());
			return "new Date(" + date.getTime() + ")";
		}

		if (Type.equals(type, Type.DECIMAL) && value != null) {
			return "new Decimal('" + value + "')";
		}

		if (Type.equals(type, Type.INTEGER) && value != null) {
			if (value instanceof Double) {
				return String.valueOf(((Double)value).longValue());
			}
			return Long.valueOf((String)value).toString();
		}

		if (value == null) {
			return "null";
		}

		return value.toString();
	}
}
