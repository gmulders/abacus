package org.gertje.abacus;

import java.util.Map;

import org.gertje.abacus.functions.RandFunction;
import org.gertje.abacus.nodes.AbstractNode;
import org.gertje.abacus.nodes.NodeFactory;
import org.gertje.abacus.nodevisitors.JavaScriptTranslator;
import org.gertje.abacus.nodevisitors.SemanticsChecker;
import org.gertje.abacus.nodevisitors.Evaluator;
import org.gertje.abacus.nodevisitors.SemanticsCheckException;
import org.gertje.abacus.nodevisitors.Simplifier;
import org.gertje.abacus.nodevisitors.SimplificationException;
import org.gertje.abacus.nodevisitors.VisitingException;
import org.gertje.abacus.symboltable.NoSuchVariableException;
import org.gertje.abacus.symboltable.SymbolTable;

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
		SymbolTable sym = createSymbolTable();

		NodeFactory nodeFactory = new NodeFactory();

		LexerInterface lexer = new Lexer(expression);
 		Parser parser = new Parser(lexer, nodeFactory);

		AbstractNode tree;
		try {
			tree = parser.parse();
		}  catch (CompilerException e) {
			exception = e.getMessage();
			return expectException;
		}

		SemanticsChecker semanticsChecker = new SemanticsChecker(sym);
		Simplifier simplifier = new Simplifier(sym, nodeFactory);
		Evaluator evaluator = new Evaluator(sym);

		Object value;
		try {
			semanticsChecker.check(tree);
			tree = simplifier.simplify(tree);
			value = evaluator.evaluate(tree);
		} catch (VisitingException e) {
			exception = e.getMessage();
			return expectException;
		}

		if (expectException) {
			return false;
		}
		
		try {
			JavaScriptTranslator javaScriptTranslator = new JavaScriptTranslator();
			javaScript = javaScriptTranslator.translate(tree);
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

	
	private boolean checkSymbolTable(SymbolTable sym) {
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
	
	private SymbolTable createSymbolTable() {
		SymbolTable sym = new SymbolTable();

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
