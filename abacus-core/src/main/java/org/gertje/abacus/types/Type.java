package org.gertje.abacus.types;

import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.Objects;
import java.util.WeakHashMap;

/**
 * Represents a type.
 */
public class Type {

	public enum BaseType {
		INTEGER(0),
		STRING(1),
		DECIMAL(2),
		BOOLEAN(3),
		DATE(4);

		private int index;

		BaseType(int index) {
			this.index = index;
		}
	}

	private static final Map<Type, WeakReference<Type>> types = new WeakHashMap<>();

	public static synchronized Type get(BaseType baseType, int dimensionality) {
		Type type = new Type(baseType, dimensionality);

		WeakReference<Type> typeWeakReference = types.get(type);
		if (typeWeakReference != null) {
			return typeWeakReference.get();
		}

		types.put(type, new WeakReference<>(type));
		return type;
	}

	public static final Type INTEGER = get(BaseType.INTEGER, 0);
	public static final Type STRING = get(BaseType.STRING, 0);
	public static final Type DECIMAL = get(BaseType.DECIMAL, 0);
	public static final Type BOOLEAN = get(BaseType.BOOLEAN, 0);
	public static final Type DATE = get(BaseType.DATE, 0);

	/**
	 * Bepaalt of het meegegeven type een nummer is.
	 * @param type Het type waarvan de methode bepaalt of het een nummer is.
	 * @return {@code true} wanneer het meegegeven type een nummer is, anders {@code false}.
	 */
	public static boolean isNumber(Type type) {
		return Type.equals(DECIMAL, type) || Type.equals(INTEGER, type);
	}

	/**
	 * Bepaalt of het meegegeven type het type onbekend is.
	 * @param type Het type waarvan de methode bepaalt of het het type van null is.
	 * @return {@code true} wanneer het meegegeven type het type van null is, anders {@code false}.
	 */
	public static boolean isUnknown(Type type) {
		return type == null;
	}

	public static boolean equals(Type type1, Type type2) {
		return Objects.equals(type1, type2);
	}

	private BaseType baseType;
	private int dimensionality;

	private Type(BaseType baseType, int dimensionality) {
		this.baseType = baseType;
		this.dimensionality = dimensionality;
	}

	public boolean isArray() {
		return dimensionality > 0;
	}

	public Type determineComponentType() {
		assert dimensionality > 0;
		return get(baseType, dimensionality - 1);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Type type = (Type) o;

		if (dimensionality != type.dimensionality) return false;
		return baseType == type.baseType;
	}

	@Override
	public int hashCode() {
		int result = baseType.hashCode();
		result = 31 * result + dimensionality;
		return result;
	}

	public BaseType getBaseType() {
		return baseType;
	}

	public int getDimensionality() {
		return dimensionality;
	}
}