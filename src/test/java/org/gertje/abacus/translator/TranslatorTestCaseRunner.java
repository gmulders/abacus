package org.gertje.abacus.translator;

import org.gertje.abacus.AbstractTestCaseRunner;
import org.gertje.abacus.context.AbacusContext;
import org.gertje.abacus.context.SimpleAbacusContext;
import org.gertje.abacus.exception.CompilerException;
import org.gertje.abacus.lexer.AbacusLexer;
import org.gertje.abacus.lexer.Lexer;
import org.gertje.abacus.nodes.AbacusNodeFactory;
import org.gertje.abacus.nodes.Node;
import org.gertje.abacus.nodes.NodeFactory;
import org.gertje.abacus.nodevisitors.SemanticsChecker;
import org.gertje.abacus.nodevisitors.Simplifier;
import org.gertje.abacus.nodevisitors.VisitingException;
import org.gertje.abacus.parser.Parser;
import org.gertje.abacus.symboltable.SymbolTable;
import org.gertje.abacus.translator.java.nodevisitors.Translator;
import org.gertje.charsequencecompiler.CharSequenceCompiler;
import org.junit.Assert;

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

		Node tree;
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
		Translator translator = new Translator(abacusContext);

		String expression;
		try {
			semanticsChecker.check(tree);

			if (!checkReturnType(tree.getType())) {
				Assert.fail(createMessage("Incorrect return type."));
			}

			tree = simplifier.simplify(tree);

			expression = translator.translate(tree);
		} catch (VisitingException e) {
			if (!abacusTestCase.failsWithException) {
				Assert.fail(createMessage("Unexpected exception.", e));
			}
			return;
		}

		if (abacusTestCase.failsWithException) {
			Assert.fail(createMessage("Expected exception, but none was thrown."));
		}

		if (!checkReturnType(tree.getType())) {
			Assert.fail(createMessage("Incorrect return type."));
		}

		// System.out.println(expression);

		String javaSource = createJavaSource(expression);
		CharSequenceCompiler<ExpressionWrapper> charSequenceCompiler = new CharSequenceCompiler<>(this.getClass().getClassLoader(), null);

		ExpressionWrapper expressionWrapper;

		Object returnValue;
		try {
			Class<ExpressionWrapper> clazz = charSequenceCompiler.compile("org.gertje.abacus.translator.Z", javaSource, null, ExpressionWrapper.class);
			expressionWrapper = clazz.newInstance();
			expressionWrapper.setSymbolTable(sym);
			returnValue = expressionWrapper.f();
		} catch (Exception e) {
			Assert.fail(createMessage("Unexpected exception.", e));
			return;
		}

		if (!checkReturnValue(returnValue)) {
			Assert.fail(createMessage("Incorrect return value."));
		}

		if (!checkSymbolTable(sym)) {
			Assert.fail(createMessage("Incorrect symbol table."));
		}
	}

	private String createJavaSource(String expression) {
		return "package org.gertje.abacus.translator;\n" +
					"import org.gertje.abacus.translator.java.runtime.*;\n" +
					"import org.gertje.abacus.util.*;\n" +
					"import org.gertje.abacus.types.*;\n" +
					"\n" +
					"\n" +
					"public class Z extends ExpressionWrapper<Object> {\n" +
					"\n" +
					"\tpublic Object f() throws Exception {\n" +
					"\t\treturn " + expression + ";\n" +
					"\t}\n\n" +
					"}\n"
				;
	}
}
