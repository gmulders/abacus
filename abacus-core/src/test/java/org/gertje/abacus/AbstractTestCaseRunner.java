package org.gertje.abacus;

import org.gertje.abacus.functions.RandFunction;
import org.gertje.abacus.symboltable.NoSuchVariableException;
import org.gertje.abacus.symboltable.SimpleSymbolTable;
import org.gertje.abacus.symboltable.SymbolTable;
import org.gertje.abacus.symboltable.Variable;
import org.gertje.abacus.types.Type;
import org.gertje.abacus.util.JavaTypeHelper;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.sql.Date;
import java.util.Arrays;
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

		Map<String, Variable> variableMap = new HashMap<>(valueList.size());
		Map<String, Object> valueMap = new HashMap<>(valueList.size());
		for (AbacusTestCase.Value value : valueList) {
			variableMap.put(value.name, new Variable(value.name, value.type));
			valueMap.put(value.name, convertToType(value.value, value.type));
		}

		symbolTable.setVariables(variableMap, valueMap);

		return symbolTable;
	}

	/**
	 * Converts the given value to the given type.
	 * @param value The value in String representation.
	 * @param type The type to convert the value to.
	 * @return The converted value.
	 */
	protected Object convertToType(Object value, Type type) {
		if (value == null || "null".equals(value)) {
			return null;
		}

		if (type.isArray()) {
			List valueList = (List) value;

			Type componentType = type.determineComponentType();

			Object[] array = (Object[]) Array.newInstance(JavaTypeHelper.determineJavaType(componentType),
					valueList.size());

			for (int i = 0; i < array.length; i++) {
				array[i] = convertToType(valueList.get(i), componentType);
			}

			return array;
		}

		switch (type.getBaseType()) {
			case DECIMAL: return new BigDecimal(value.toString());
			case INTEGER: return value instanceof Double ? (long) ((double) value) : Long.valueOf(value.toString());
			case BOOLEAN: return Boolean.valueOf(value.toString());
			case DATE: return Date.valueOf(value.toString());
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
		return checkValues(value, determineExpectedValue());
	}

	/**
	 * Bepaalt de verwachtte waarde.
	 * @return De verwachtte waarde.
	 */
	protected Object determineExpectedValue() {
		return convertToType(abacusTestCase.returnValue.value, abacusTestCase.returnValue.type);
	}

	/**
	 * Controleert of het type gelijk is aan het verwachtte type.
	 * @param type Het type.
	 * @return {@code true} wanneer beide types overeenkomen, anders {@code false}.
	 */
	protected boolean checkReturnType(Type type) {
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
				Object variableValue = symbolTable.getVariableValue(value.name);
				Object expectedValue = convertToType(value.value, value.type);
				Type variableType = symbolTable.getVariableType(value.name);

				if (!Type.equals(variableType, value.type)) {
					return false;
				}

				if (!checkValues(variableValue, expectedValue)) {
					return false;
				}
			}
		} catch (NoSuchVariableException e) {
			return false;
		}
		return true;
	}

	private boolean checkValues(Object variableValue, Object expectedValue) {
		// If they are both null, they are equal.
		if (variableValue == null && expectedValue == null) {
			return true;
		}

		// If one is null, they are not equal (since they are not both null).
		if (variableValue == null || expectedValue == null) {
			return false;
		}

		// If either of one is an array, but the other is not, they are not equal.
		if (variableValue instanceof Object[] && !(expectedValue instanceof Object[])
				|| !(variableValue instanceof Object[]) && expectedValue instanceof Object[]) {
			return false;
		}

		if (variableValue instanceof Object[]) {
			return Arrays.deepEquals((Object[]) variableValue, (Object[]) expectedValue);
		}

		if (((Comparable) variableValue).compareTo(expectedValue) != 0) {
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
