package org.gertje.abacus;

import org.gertje.abacus.functions.RandFunction;
import org.gertje.abacus.symboltable.NoSuchVariableException;
import org.gertje.abacus.symboltable.SimpleSymbolTable;
import org.gertje.abacus.symboltable.SymbolTable;
import org.gertje.abacus.types.Type;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Abstract class to run a test case.
 */
public abstract class AbstractTestCaseRunner {

	/**
	 * The current test case.
	 */
	protected AbacusTestCase abacusTestCase;

	/**
	 * Indicates whether the test case is run by an interpreter.
	 */
	protected boolean isForInterpreter;

	/**
	 * Runs the test case.
	 */
	public abstract void runTestCase();

	/**
	 * Creates the symbol table from the {@link AbacusTestCase#variableListBefore}.
	 * @return the symbol table.
	 */
	protected SimpleSymbolTable createSymbolTable() {
		SimpleSymbolTable symbolTable = new SimpleSymbolTable();
		symbolTable.registerFunction(new RandFunction());

		List<AbacusTestCase.Value> valueList = abacusTestCase.variableListBefore;

		if (valueList == null) {
			return symbolTable;
		}

		Map<String, SimpleSymbolTable.Variable> map = new HashMap<>(valueList.size());
		for (AbacusTestCase.Value value : valueList) {
			SimpleSymbolTable.Variable variable = new SimpleSymbolTable.Variable(value.name, value.type);
			variable.setValue(convertToType(value.value, value.type));

			map.put(value.name, variable);
		}

		symbolTable.setVariables(map);

		return symbolTable;
	}

	/**
	 * Converts the given value to the given type.
	 * @param value The value in String representation.
	 * @param type The type to convert the value to.
	 * @return The converted value.
	 */
	protected Object convertToType(String value, Type type) {
		if (value == null || "null".equals(value)) {
			return null;
		}

		switch (type) {
			case DECIMAL: return new BigDecimal(value);
			case INTEGER: return new BigInteger(value);
			case BOOLEAN: return Boolean.valueOf(value);
			case DATE: return Date.valueOf(value);
			case STRING: return value;
		}

		throw new IllegalArgumentException("Could not convert the string to the correct type.");
	}

	/**
	 * Controleert of de return-waarde klopt met de verwachtte-return-waarde.
	 * @param value De return-waarde.
	 * @return {@code true} wanneer de return-waarde correct was, anders {@code false}.
	 */
	protected boolean checkReturnValue(Object value) {
		Object expectedValue = determineExpectedValue();

		// Wanneer het resultaat null is en we hadden dit ook verwacht geven we true terug.
		if (value == null && expectedValue == null) {
			return true;
		}

		// Wanneer het resultaat of de verwachting null is geven we false terug, dit kan omdat als ze allebei null
		// hadden moeten zijn hadden we al true terug gegeven bij vorige vergelijking.
		if (value == null || expectedValue == null) {
			return false;
		}

		if (((Comparable) value).compareTo(expectedValue) != 0) {
			return false;
		}

		return true;
	}

	/**
	 * Bepaalt de verwachtte waarde.
	 * @return De verwachtte waarde.
	 */
	protected Object determineExpectedValue() {
		if (isForInterpreter && abacusTestCase.returnValue.hasDeviantTypeInInterpreter) {
			return convertToType(abacusTestCase.returnValue.value, abacusTestCase.returnValue.typeInInterpreter);
		}

		return convertToType(abacusTestCase.returnValue.value, abacusTestCase.returnValue.type);
	}

	/**
	 * Controleert of het type gelijk is aan het verwachtte type.
	 * @param type Het type.
	 * @return {@code true} wanneer beide types overeenkomen, anders {@code false}.
	 */
	protected boolean checkReturnType(Type type) {
		if (isForInterpreter && abacusTestCase.returnValue.hasDeviantTypeInInterpreter) {
			return type == abacusTestCase.returnValue.typeInInterpreter;
		}

		return type == abacusTestCase.returnValue.type;
	}

	/**
	 * Checks if the resulting symbol table corresponds to what is expected.
	 * @param symbolTable The resulting symbol table.
	 * @return {@code true} if it corresponds, {@code false} otherwise.
	 */
	protected boolean checkSymbolTable(SymbolTable symbolTable) {
		List<AbacusTestCase.Value> valueAfterList = abacusTestCase.variableListAfter;

		if (valueAfterList == null) {
			return true;
		}

		try {
			for (AbacusTestCase.Value value : valueAfterList) {
				Comparable variableValue = (Comparable) symbolTable.getVariableValue(value.name);
				Comparable expectedValue = (Comparable) convertToType(value.value, value.type);
				Type variableType = symbolTable.getVariableType(value.name);

				if (variableType != value.type) {
					return false;
				}

				if (variableValue == null && expectedValue == null) {
					continue;
				}

				if (variableValue.compareTo(expectedValue) != 0) {
					return false;
				}
			}
		} catch (NoSuchVariableException e) {
			return false;
		}
		return true;
	}

	/**
	 * Creates a message for when the Assert failes.
	 * @param message The basis of the message.
	 * @return The message with extra information.
	 */
	protected String createMessage(String message) {
		return createMessage(message, null);
	}

	/**
	 * Creates a message for when the Assert failes.
	 * @param message The basis of the message.
	 * @param e The exception thrown by Abacus.
	 * @return The message with extra information.
	 */
	protected String createMessage(String message, Exception e) {
		String exceptionStackTrace = null;
		if (e != null) {
			StringWriter stringWriter = new StringWriter();
			PrintWriter printWriter = new PrintWriter(stringWriter);
			e.printStackTrace(printWriter);
			exceptionStackTrace = stringWriter.toString();
		}

		return "Test file: '" + abacusTestCase.getFilename() + " Expression: '" + abacusTestCase.expression + "' - "
				+ message + (exceptionStackTrace == null ? "" : "\n" + exceptionStackTrace);
	}

	public void setAbacusTestCase(AbacusTestCase abacusTestCase) {
		this.abacusTestCase = abacusTestCase;
	}
}
