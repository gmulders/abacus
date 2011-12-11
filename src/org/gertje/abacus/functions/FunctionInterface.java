package org.gertje.abacus.functions;

import java.util.List;

public interface FunctionInterface {

	/**
	 * Geeft de naam van de functie terug.
	 */
	public String getName();

	/**
	 * Returns the return-type of the function.
	 */
	public Class<?> getReturnType();

	/**
	 * Evaluates the function.
	 */
	public Object evaluate(List<Object> params);

	/**
	 * Verifieert of de functie de parameters accepteert.
	 */
    public boolean acceptsParameters(List<Class<?>> types);

	/**
	 * Controleert of de functie gelijk is aan de meegegeven functie.
	 */
	public boolean equals(FunctionInterface function);
}
