package org.gertje.abacus;

import org.gertje.abacus.context.AbacusContext;
import org.gertje.abacus.context.SimpleAbacusContext;
import org.gertje.abacus.exception.CompilerException;
import org.gertje.abacus.lexer.AbacusLexer;
import org.gertje.abacus.lexer.Lexer;
import org.gertje.abacus.nodes.AbacusNodeFactory;
import org.gertje.abacus.nodes.Node;
import org.gertje.abacus.nodes.NodeFactory;
import org.gertje.abacus.nodevisitors.Evaluator;
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
		Evaluator evaluator = new Evaluator(abacusContext);

		Object returnValue;
		try {
			semanticsChecker.check(tree);

			if (!checkReturnType(tree.getType())) {
				Assert.fail(createMessage("Incorrect return type."));
			}

			tree = simplifier.simplify(tree);

			returnValue = evaluator.evaluate(tree);
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

		if (!checkReturnValue(returnValue)) {
			Assert.fail(createMessage("Incorrect return value."));
		}

		if (!checkSymbolTable(sym)) {
			Assert.fail(createMessage("Incorrect symbol table."));
		}
	}
}
