package org.gertje.abacus.symboltable;

import org.gertje.abacus.functions.Function;
import org.gertje.abacus.nodes.Node;
import org.gertje.abacus.types.Type;
import org.gertje.abacus.util.CastHelper;
import org.gertje.abacus.util.SemanticsHelper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Deze klasse stelt een vrij simpele implementatie van een SimpleSymbolTable voor. Voor de meeste toepassingen is dit
 * voldoende. Wanneer de toepassing meer eisen stelt kan deze klasse overschreven worden, of een eigen implementatie van
 * SymbolTable gemaakt worden.
 * 
 * Een aantal eigenschappen van deze implementatie zijn:
 *
 * - Alle variabelen 'leven' in dezelfde 'ruimte', d.w.z. er wordt geen stack bijgehouden.
 * - Ook alle functies 'leven' in dezelfde 'ruimte'.
 * - Functies kunnen niet overloaded worden, d.w.z. een functie met een bepaalde naam bestaat maar een keer.
 */
public class SimpleSymbolTable implements SymbolTable {

	/**
	 * Represents a variable in this SymbolTable.
	 */
	public static class Variable {
		private String name;
		private Object value;
		private Type type;

		public Variable(String name, Type type) {
			this.name = name;
			this.type = type;
		}

		public String getName() {
			return name;
		}

		public Object getValue() {
			return value;
		}

		public void setValue(Object value) {
			this.value = value;
		}

		public Type getType() {
			return type;
		}
	}

	private Map<String, Variable> variables;
	private Map<String, Function> functions;

	public SimpleSymbolTable() {
		variables = new HashMap<>();
		functions = new HashMap<>();
	}

	/**
	 * Voegt de variabelen toe aan de symboltable, wanneer de variabele al bestaat wordt de waarde hiervan overschreven.
	 */
	public void setVariables(Map<String, Variable> variables) {
		this.variables.putAll(variables);
	}

	/**
	 * Voegt de variabele toe aan de symboltable, wanneer de variabele al bestaat wordt de waarde hiervan overschreven.
	 */
	public void addVariable(Variable variable) {
		this.variables.put(variable.getName(), variable);
	}

	/**
	 * Haal alle variabalen op uit de symboltable.
	 */
	public Map<String, Variable> getVariables() {
		return variables;
	}

	@Override
	public void setVariableValue(String identifier, Type type, Object value) throws IllegalTypeException {
		// Wanneer de variabele nog niet bestaat maken we die nu eerst aan.
		if (!getExistsVariable(identifier)) {
			variables.put(identifier, new Variable(identifier, type));
		}

		// Haal de variabele op.
		Variable variable = variables.get(identifier);

		// Controleer of de types compatible zijn.
		if (!SemanticsHelper.checkAssignmentType(variable.type, type)) {
			throw new IllegalTypeException("Cannot assign value of '" + type + "' to a variable with type '"
					+ variable.type + "'.");
		}

		// Cast the value to the correct type.
		value = CastHelper.castValue(value, type, variable.type);

		// Ken de waarde toe.
		variable.value = value;
	}

	@Override
	public boolean getExistsVariable(String identifier) {
		return variables.containsKey(identifier);
	}

	@Override
	public Object getVariableValue(String identifier) throws NoSuchVariableException {
		// De variabele moet altijd bestaan, wanneer deze niet bestaat gooien we een exceptie.
		if (!getExistsVariable(identifier)) {
			throw new NoSuchVariableException("Variable '" + identifier + "' does not exsist.");
		}
		// De variabele bestaat, geef de waarde terug.
		return variables.get(identifier).value;
	}

	@Override
	public Type getVariableType(String identifier) throws NoSuchVariableException {
		// De variabele moet altijd bestaan, wanneer deze niet bestaat gooien we een exceptie.
		if (!getExistsVariable(identifier)) {
			throw new NoSuchVariableException("Variable '" + identifier + "' does not exsist.");
		}
		// De variabele bestaat, geef het type terug.
		return variables.get(identifier).type;
	}

	/**
	 * Voegt een functie toe aan de symboltable.
	 */
	public void registerFunction(Function function) {
		functions.put(function.getName(), function);
	}

	@Override
	public boolean getExistsFunction(String identifier, List<Type> types) {
		if (!functions.containsKey(identifier)) {
			return false;
		}
		
		// Controleer of de functie de types accepteerd.
		return functions.get(identifier).acceptsParameters(types);
	}

	@Override
	public Object getFunctionReturnValue(String identifier, List<Object> params, List<Type> types)
			throws NoSuchFunctionException {
		// De functie moet bestaan, wanneer deze niet bestaat gooien we een exceptie.
		if (!getExistsFunction(identifier, types)) {
			throw new NoSuchFunctionException("Function '" + identifier + "' does not exsist.");
		}

		// De functie bestaat; evalueer de functie met de meegegeven parameters.
		return functions.get(identifier).evaluate(params);
	}

	@Override
	public Type getFunctionReturnType(String identifier, List<Node> params) {
		// Bepaal het return type.
		return functions.get(identifier).getReturnType();
	}
}
