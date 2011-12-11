package org.gertje.abacus.symboltable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.gertje.abacus.functions.FunctionInterface;
import org.gertje.abacus.nodes.AbstractNode;

/**
 * Deze klasse stelt een vrij simpele implementatie van een SymbolTable voor. Voor de meeste toepassingen is dit 
 * voldoende. Wanneer de toepassing meer eisen stelt kan deze klasse overschreven worden, of een eigen implementatie van
 * SymbolTableInterface gemaakt worden.
 * 
 * Een aantal eigenschappen van deze implementatie zijn:
 * - Alle variabelen 'leven' in dezelfde 'ruimte', d.w.z. er wordt geen stack bijgehouden.
 * - Ook alle functies 'leven' in dezelfde 'ruimte'.
 * - Functies kunnen niet overloaded worden, d.w.z. een functie met een bepaalde naam bestaat maar een keer.
 */
public class SymbolTable implements SymbolTableInterface {

	private Map<String, Object> variables;
	private Map<String, FunctionInterface> functions;

	public SymbolTable() {
		variables = new HashMap<String, Object>();
		functions = new HashMap<String, FunctionInterface>();
	}
	
	/**
	 * Voegt de variabelen toe aan de symboltable, wanneer de variabele al bestaat wordt de waarde hiervan overschreven.
	 */
	public void setVariables(Map<String, Object> variables) {
		variables.putAll(variables);
	}

	/**
	 * Haal alle variabalen op uit de symboltable.
	 */
	public Map<String, Object> getVariables() {
		return variables;
	}

	@Override
	public void setVariableValue(String key, Object value) {
		variables.put(key, value);
	}

	@Override
	public boolean getExistsVariable(String identifier) {
		return variables.containsKey(identifier);
	}

	@Override
	public Object getVariableValue(String identifier) {
		// De variabele moet altijd bestaan, wanneer deze niet bestaat gooien we een exceptie.
		if (!variables.containsKey(identifier)) {
			throw new RuntimeException("Variable '" + identifier + "' does not exsist.");
		}
		// De variabele bestaat, geef de waarde terug.
		return variables.get(identifier);
	}

	@Override
	public Class<?> getVariableType(String identifier) {
		// De variabele moet altijd bestaan, wanneer deze niet bestaat gooien we een exceptie.
		if (!variables.containsKey(identifier)) {
			throw new RuntimeException("Variable '" + identifier + "' does not exsist.");
		}
		// De variabele bestaat, geef het type terug.
		return variables.get(identifier).getClass();
	}

	/**
	 * Voegt een functie toe aan de symboltable.
	 */
	public void registerFunction(FunctionInterface function) {
		functions.put(function.getName(), function);
	}

	@Override
	public boolean getExistsFunction(String identifier, List<AbstractNode> params) {
		if (!functions.containsKey(identifier)) {
			return false;
		}
		
		// Maak een lijst van Objecten aan waarin we de parameters gaan evalueren.
		List<Class<?>> types = new ArrayList<Class<?>>();

		// Loop over alle nodes heen.
		for (AbstractNode param : params) {
			// Voeg het type van de node toe aan de lijst.
			types.add(param.getType());
		}		

		// Controleer of de functie de types accepteerd.
		return functions.get(identifier).acceptsParameters(types);
	}

	@Override
	public Object getFunctionReturnValue(String identifier, List<AbstractNode> params) {
		// De functie moet bestaan, wanneer deze niet bestaat gooien we een exceptie.
		if (!getExistsFunction(identifier, params)) {
			throw new RuntimeException("Function '" + identifier + "' does not exsist.");
		}

		// Maak een lijst van Objecten aan waarin we de parameters gaan evalueren.
		List<Object> evaluatedParams = new ArrayList<Object>();
		
		// Loop over alle nodes heen.
		for (AbstractNode param : params) {
			// Evalueer de nodes en sla het resultaat op in de lijst.
			evaluatedParams.add(param.evaluate(this));
		}
		
		// De functie bestaat; evalueer de functie met de meegegeven parameters.
		return functions.get(identifier).evaluate(evaluatedParams);
	}

	@Override
	public Class<?> getFunctionReturnType(String identifier, List<AbstractNode> params) {
		// Bepaal het return type.
		return functions.get(identifier).getReturnType();
	}
	
	@Override
	public boolean getIsVariableTypeAllowed(String identifier, Class<?> type) {
		return true;
	}
}
