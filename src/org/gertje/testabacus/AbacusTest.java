package org.gertje.testabacus;

import java.math.BigDecimal;

import org.gertje.abacus.Compiler;
import org.gertje.abacus.CompilerException;
import org.gertje.abacus.nodes.AbstractNode;
import org.gertje.abacus.symboltable.SymbolTable;


public class AbacusTest {

	private class TestSet {
		String expression;
		Class<?> expectedType;
		Object expectedValue;
		boolean expectException;
		
		String exception;

		TestSet(String expression, Class<?> expectedType, Object expectedValue, boolean expectException) {
			this.expression = expression;
			this.expectedType = expectedType;
			this.expectedValue = expectedValue;
			this.expectException = expectException;
		}
	}

	private SymbolTable sym;
	private Compiler compiler;

	private TestSet[] testSets = new TestSet[] {
			new TestSet("3*3+a", BigDecimal.class, BigDecimal.valueOf(11), false),
			new TestSet("3*(3+a)", BigDecimal.class, BigDecimal.valueOf(15), false),
			new TestSet("s3*3+a", null, null, true),
			new TestSet("3==3", Boolean.class, Boolean.TRUE, false),			
			new TestSet("2 == 3 ? 'aap' : 'geen aap'", String.class, "geen aap", false),
			new TestSet("a = 3", BigDecimal.class, BigDecimal.valueOf(3), false),
			new TestSet("a = 3;\n3-3;", BigDecimal.class, BigDecimal.valueOf(0), false),
	};
	/**
	 * @param args
	 * @throws CompilerException
	 */
	public static void main(String[] args) {
		AbacusTest abacusTest = new AbacusTest();
		abacusTest.run();
	}

	private void run() {
		sym = new SymbolTable();
		sym.setVariableValue("a", BigDecimal.valueOf(2.0));
		sym.setVariableValue("b", BigDecimal.valueOf(3.0));
		sym.setVariableValue("c", BigDecimal.valueOf(4.2));

		for (String key : sym.getVariables().keySet()) {
			System.out.println("Variable: " + key + ", Value: " + sym.getVariables().get(key));
		}

		System.out.println();
		
		compiler = new Compiler(sym);
		
		for (TestSet testSet : testSets) {
			if (!test(testSet)) {
				printError(testSet);
			} else {
				printOk(testSet);
			}
		}

		System.out.println();
		
		for (String key : sym.getVariables().keySet()) {
			System.out.println("Variable: " + key + ", Value: " + sym.getVariables().get(key));
		}
	}

	private boolean test(TestSet testSet) {

		Object value;
		try {
			AbstractNode node = compiler.compile(testSet.expression, null);
			value = node.evaluate(sym);
		} catch (CompilerException ce) {
			testSet.exception = ce.getMessage();
			return testSet.expectException;
		}

		if (testSet.expectException) {
			return false;
		}
		
		if (!testSet.expectedType.equals(value.getClass())) {
			return false;
		}

		if (value == null) {
			return false;
		}

		return ((Comparable) value).compareTo(testSet.expectedValue) == 0;
	}
	
	private static void printOk(TestSet testSet) {
		System.out.println("OK: " + testSet.expression);
	}
	
	private static void printError(TestSet testSet) {
		System.out.println("Error: " + testSet.expression + " " + testSet.exception);
	}
}
