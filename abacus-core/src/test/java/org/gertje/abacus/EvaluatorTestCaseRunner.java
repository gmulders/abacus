package org.gertje.abacus;

import org.gertje.abacus.context.AbacusContext;
import org.gertje.abacus.context.SimpleAbacusContext;
import org.gertje.abacus.exception.CompilerException;
import org.gertje.abacus.lexer.AbacusLexer;
import org.gertje.abacus.lexer.Lexer;
import org.gertje.abacus.nodes.AbacusNodeFactory;
import org.gertje.abacus.nodes.Node;
import org.gertje.abacus.nodes.NodeFactory;
import org.gertje.abacus.nodes.RootNode;
import org.gertje.abacus.nodevisitors.Evaluator;
import org.gertje.abacus.nodevisitors.GraphVizTranslator;
import org.gertje.abacus.nodevisitors.SemanticsChecker;
import org.gertje.abacus.nodevisitors.Simplifier;
import org.gertje.abacus.nodevisitors.VisitingException;
import org.gertje.abacus.parser.Parser;
import org.gertje.abacus.symboltable.SymbolTable;
import org.junit.Assert;

/**
 * Runs the test for the evaluator case.
 */
public class EvaluatorTestCaseRunner extends AbstractTestCaseRunner {

	@Override
	public void runTestCase() {
		// Maak een nieuwe symboltable en vul deze met wat waarden.
		SymbolTable sym = createSymbolTable();

		NodeFactory nodeFactory = new AbacusNodeFactory();

		Lexer lexer = new AbacusLexer(abacusTestCase.expression);
		Parser parser = new Parser(lexer, nodeFactory);

		RootNode rootNode;
		Node node;
		try {
			rootNode = parser.parse();
		} catch (CompilerException e) {
			if (!abacusTestCase.failsWithException) {
				Assert.fail(createMessage("Unexpected exception.", e));
			}
			return;
		}

		//vizualize(rootNode);

		AbacusContext abacusContext = new SimpleAbacusContext(sym);
		SemanticsChecker semanticsChecker = new SemanticsChecker(sym);
		Simplifier simplifier = new Simplifier(abacusContext, nodeFactory);
		Evaluator expressionEvaluator = new Evaluator(abacusContext);

		Object returnValue;
		try {
			semanticsChecker.check(rootNode);

			//vizualize(rootNode);

			if (!checkReturnType(rootNode.getType())) {
				Assert.fail(createMessage("Incorrect return type."));
			}

			node = simplifier.simplify(rootNode);

			//vizualize(node);

			returnValue = expressionEvaluator.evaluate(node);
		} catch (VisitingException e) {
			if (!abacusTestCase.failsWithException) {
				Assert.fail(createMessage("Unexpected exception.", e));
			}
			return;
		}

		if (abacusTestCase.failsWithException) {
			Assert.fail(createMessage("Expected exception, but none was thrown."));
		}

		if (!checkReturnType(rootNode.getType())) {
			Assert.fail(createMessage("Incorrect return type."));
		}

		if (!checkReturnValue(returnValue)) {
			Assert.fail(createMessage("Incorrect return value: " + returnValue));
		}

		if (!checkSymbolTable(sym)) {
			Assert.fail(createMessage("Incorrect symbol table."));
		}
	}

	/**
	 * Uses the {@link GraphVizTranslator} to make a GraphViz definition and prints it to System.out.
	 * @param node The node to visualize.
	 */
	private void vizualize(Node node) {
		try {
			GraphVizTranslator graphVizTranslator = new GraphVizTranslator();
			System.out.println(graphVizTranslator.translate(node));
		} catch (VisitingException e) {
			Assert.fail(createMessage("Unexpected exception.", e));
		}
	}
}
