package org.gertje.abacus.functions;

import org.gertje.abacus.types.Type;

import java.util.List;

/**
 * Interface that every function should implement.
 */
public interface Function {

	/**
	 * Geeft de naam van de functie terug.
	 */
	String getName();

	/**
	 * Returns the return-type of the function.
	 */
	Type getReturnType();

	/**
	 * Evaluates the function.
	 */
	Object evaluate(List<Object> params);

	/**
	 * Verifieert of de functie de parameters accepteert.
	 */
	boolean acceptsParameters(List<Type> types);

	/**
	 * Controleert of de functie gelijk is aan de meegegeven functie.
	 */
	boolean equals(Function function);
}
