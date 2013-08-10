package org.gertje.abacus.symboltable;

import java.util.List;

import org.gertje.abacus.nodes.AbstractNode;


public interface SymbolTable {

	/**
	 * Bepaalt of de variabele met de meegegeven identifier bestaat.
	 * @param identifier
	 * @return <code>true</code> wanneer de variabele bestaat, anders <code>false</code>.
	 */
	public boolean getExistsVariable(String identifier);

	/**
	 * Zet de waarde van de variabele met de meegegeven identifier op de meegegeven waarde.
	 * @param identifier
	 * @param value
	 */
	public void setVariableValue(String identifier, Object value);

	/**
	 * Geeft de waarde van de variabele met de meegegeven identifier terug.
	 * @param identifier
	 * @return de waarde van de variabele.
	 */
	public Object getVariableValue(String identifier) throws NoSuchVariableException;

	/**
	 * Geeft het type van de variabele met de meegegeven identifier terug.
	 * @param identifier
	 * @return het type van de variabele.
	 */
	public Class<?> getVariableType(String identifier) throws NoSuchVariableException;

	/**
	 * Bepaalt of de variabele met de meegegeven identifier een waarde van het meegegeven type kan bevatten.
	 * @param identifier
	 * @param type
	 * @return <code>true</code> wanneer de variabele van het meegegeven type is, anders <code>false</code>.
	 */
	public boolean getIsVariableTypeAllowed(String identifier, Class<?> type);

	/**
	 * Bepaalt of de functie bestaat voor de meegegeven identifier met de meegegeven parameters.
	 * @param identifier
	 * @param types
	 * @return <code>true</code> wanneer de functie bestaat, anders <code>false</code>.
	 */
	public boolean getExistsFunction(String identifier, List<Class<?>> types);

	/**
	 * Evalueert de functie met de meegegeven identifier met de meegegeven parameters.
	 * @param identifier
	 * @param params
	 * @return de returnwaarde van de functie.
	 */
	public Object getFunctionReturnValue(String identifier, List<Object> params, List<Class<?>> types)
			throws NoSuchFunctionException;

	/**
	 * Bepaalt het return type van functie met de meegegeven parameters.
	 * @param identifier
	 * @param params
	 * @return het return type van de functie.
	 */
	public Class<?> getFunctionReturnType(String identifier, List<AbstractNode> params) throws NoSuchFunctionException;
}
