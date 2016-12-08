package org.gertje.abacus.symboltable;

import org.gertje.abacus.functions.Function;
import org.gertje.abacus.nodes.ExpressionNode;
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

	private Map<String, Variable> variables;
	private Map<String, Object> values;

	private Map<String, Function> functions;

	public SimpleSymbolTable() {
		variables = new HashMap<>();
		values = new HashMap<>();
		functions = new HashMap<>();
	}

	/**
	 * Voegt de variabelen toe aan de symboltable, wanneer de variabele al bestaat wordt de waarde hiervan overschreven.
	 */
	public void setVariables(Map<String, Variable> variables, Map<String, Object> values) {
		this.variables.putAll(variables);
		this.values.putAll(values);
	}

	public void addVariable(Variable variable) {
		this.variables.put(variable.getIdentifier(), variable);
	}

	/**
	 * Haal alle variabalen op uit de symboltable.
	 */
	public Map<String, Variable> getVariables() {
		return variables;
	}

	@Override
	public void setVariableValue(String identifier, Object value) throws IllegalTypeException, NoSuchVariableException {
		// Wanneer de variabele nog niet bestaat maken we die nu eerst aan.
		if (!getExistsVariable(identifier)) {
			throw new NoSuchVariableException("Variable '" + identifier + "' does not exsist.");
		}

		// Ken de waarde toe.
		values.put(identifier, value);
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
		return values.get(identifier);
	}

	@Override
	public Type getVariableType(String identifier) throws NoSuchVariableException {
		// De variabele moet altijd bestaan, wanneer deze niet bestaat gooien we een exceptie.
		if (!getExistsVariable(identifier)) {
			throw new NoSuchVariableException("Variable '" + identifier + "' does not exsist.");
		}
		// De variabele bestaat, geef het type terug.
		return variables.get(identifier).getType();
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
	public Type getFunctionReturnType(String identifier, List<ExpressionNode> params) {
		// Bepaal het return type.
		return functions.get(identifier).getReturnType();
	}
}
