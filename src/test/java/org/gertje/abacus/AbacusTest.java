package org.gertje.testabacus;

import java.util.Map;

import org.gertje.abacus.Compiler;
import org.gertje.abacus.CompilerException;
import org.gertje.abacus.nodes.AbstractNode;
import org.gertje.abacus.nodes.NodeFactory;
import org.gertje.abacus.symboltable.SymbolTable;

public class AbacusTest {

	private String expression;
	private Object expectedValue;
	private boolean expectException;
	private Map<String, Object> symbolsBefore;
	private Map<String, Object> symbolsAfter;
	private String exception;
	private boolean result;

	public AbacusTest(String expression, Object expectedValue, boolean expectException, Map<String, Object> symbolsBefore, Map<String, Object> symbolsAfter) {
		this.expression = expression;
		this.expectedValue = expectedValue;
		this.expectException = expectException;
		this.symbolsBefore = symbolsBefore;
		this.symbolsAfter = symbolsAfter;
	}
	
	public boolean run() {
		result = runTest();
		return result;
	}
	
	private boolean runTest() {
		// Maak een nieuwe symboltable en vul deze met wat waarden.
		SymbolTable sym = createSymbolTable();
		
		Compiler compiler = new Compiler(sym, new NodeFactory());

		Object value;
		try {
			AbstractNode node = compiler.compile(expression, null);
			value = node.evaluate(sym);
		} catch (CompilerException ce) {
			exception = ce.getMessage();
			return expectException;
		}

		if (expectException) {
			return false;
		}
		
		if (value == null && expectedValue == null) {
			return true;
		}

		if (!expectedValue.getClass().equals(value.getClass())) {
			return false;
		}

		if (value == null) {
			return false;
		}

		if (((Comparable) value).compareTo(expectedValue) != 0) {
			return false;
		}
		
		if (!checkSymbolTable(sym)) {
			return false;
		}
		
		return true;
	}

	
	private boolean checkSymbolTable(SymbolTable sym) {
		for (Map.Entry<String, Object> entry : symbolsAfter.entrySet()) {
			if (((Comparable) entry.getValue()).compareTo(sym.getVariableValue(entry.getKey())) != 0) {
				return false;
			}			
        }

		return true;
	}
	
	private SymbolTable createSymbolTable() {
		SymbolTable sym = new SymbolTable();

		sym.setVariables(symbolsBefore);
		
		return sym;
	}

	public void printResult() {
		if (result) {
			System.out.println("OK: " + expression + " " + (exception != null ? exception : ""));
		} else {
			System.out.println("Error: " + expression + " " + (exception != null ? exception : ""));
		}
	}

	public String getException() {
		return exception;
	}

	public String getExpression() {
		return expression;
	}

	public void setExpression(String expression) {
		this.expression = expression;
	}
}
