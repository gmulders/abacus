package org.gertje.abacus;

import org.gertje.abacus.context.SimpleAbacusContext;
import org.gertje.abacus.nodevisitors.Interpreter;
import org.gertje.abacus.nodevisitors.InterpreterException;
import org.gertje.abacus.parser.ParserException;
import org.gertje.abacus.symboltable.SymbolTable;
import org.junit.Assert;

/**
 * Runs the test case for the interpreter.
 */
public class InterpreterTestCaseRunner extends AbstractTestCaseRunner {

	/**
	 * Constructor.
	 */
	public InterpreterTestCaseRunner() {
		// This test case runner should use the interpreter exceptions of the test case.
		isForInterpreter = true;
	}

	@Override
	public void runTestCase() {
		// Probeer of de interpreter ook werkt.
		SymbolTable sym = createSymbolTable();
		Interpreter interpreter = new Interpreter(new SimpleAbacusContext(sym));

		Object returnValue;

		try {
			returnValue = interpreter.interpret(abacusTestCase.expression);
		} catch (ParserException | InterpreterException e) {
			if (!abacusTestCase.failsWithException) {
				Assert.fail(createMessage("Unexpected exception.", e));
			}
			return;
		}

		if (abacusTestCase.failsWithException && !abacusTestCase.succeedsInInterpreter) {
			Assert.fail(createMessage("Expected exception, but none was thrown."));
		}

		if (!checkReturnType(interpreter.getReturnType())) {
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
