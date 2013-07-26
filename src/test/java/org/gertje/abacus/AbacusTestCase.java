package org.gertje.abacus;

import java.util.Map;

import org.gertje.abacus.functions.RandFunction;
import org.gertje.abacus.nodes.AbacusNodeFactory;
import org.gertje.abacus.nodes.AbstractNode;
import org.gertje.abacus.nodevisitors.JavaScriptTranslator;
import org.gertje.abacus.nodevisitors.VisitingException;
import org.gertje.abacus.symboltable.NoSuchVariableException;
import org.gertje.abacus.symboltable.SimpleSymbolTable;

public class AbacusTestCase {

	private String expression;
	private Object expectedValue;
	private boolean expectException;
	private Map<String, Object> symbolsBefore;
	private Map<String, Object> symbolsAfter;
	private String exception;
	private boolean result;
	private String javaScript;

	public AbacusTestCase(String expression, Object expectedValue, boolean expectException, Map<String, Object> symbolsBefore, Map<String, Object> symbolsAfter) {
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
		SimpleSymbolTable sym = createSymbolTable();
		
		Compiler compiler = new Compiler(sym, new AbacusNodeFactory());
		AbstractNode node = null;

		Object value;
		try {
			node = compiler.compile(expression, null);
			value = node.evaluate(sym);
		} catch (AbacusException ce) {
			exception = ce.getMessage();
			return expectException;
		}

		if (expectException) {
			return false;
		}
		
		try {
			JavaScriptTranslator javaScriptTranslator = new JavaScriptTranslator();
			javaScript = javaScriptTranslator.translate(node);
		} catch (VisitingException ve) {
			exception = ve.getMessage();
			return false;
		}
		
		// Wanneer het resultaat null is en we hadden dit ook verwacht geven we true terug.
		if (value == null && expectedValue == null) {
			return true;
		}

		// Wanneer het resultaat of de verwachting null is geven we false terug, dit kan omdat als ze allebei null 
		// hadden moeten zijn hadden we al true terug gegeven bij vorige vergelijking.
		if (value == null || expectedValue == null) {
			return false;
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

	
	private boolean checkSymbolTable(SimpleSymbolTable sym) {
		try {
			for (Map.Entry<String, Object> entry : symbolsAfter.entrySet()) {
				if (((Comparable) entry.getValue()).compareTo(sym.getVariableValue(entry.getKey())) != 0) {
					return false;
				}
			}
		} catch (NoSuchVariableException e) {
			return false;
		}
		return true;
	}
	
	private SimpleSymbolTable createSymbolTable() {
		SimpleSymbolTable sym = new SimpleSymbolTable();

		sym.setVariables(symbolsBefore);
		
		sym.registerFunction(new RandFunction());
		return sym;
	}

	public boolean printResult() {
		if (result) {
			System.out.println("OK: " + expression + " " + (exception != null ? exception : "") + " " + (javaScript != null ? javaScript : ""));
		} else {
			System.out.println("Error: " + expression + " " + (exception != null ? exception : "") + " " + (javaScript != null ? javaScript : ""));
		}
		
		return result;
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
