package org.gertje.abacus.symboltable;

import org.gertje.abacus.types.Type;

/**
 * Represents a variable.
 */
public class Variable {

	/**
	 * The identifier of the variable.
	 */
	private String identifier;

	/**
	 * The type of the variable.
	 */
	private Type type;

	public Variable(String identifier, Type type) {
		this.identifier = identifier;
		this.type = type;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Variable variable = (Variable) o;

		if (type != variable.type) return false;
		return identifier.equals(variable.identifier);

	}

	@Override
	public int hashCode() {
		int result = type.hashCode();
		result = 31 * result + identifier.hashCode();
		return result;
	}

	public String getIdentifier() {
		return identifier;
	}

	public Type getType() {
		return type;
	}
}
