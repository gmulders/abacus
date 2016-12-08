package org.gertje.abacus.functions;

import org.gertje.abacus.types.Type;

import java.util.List;

/**
 * Abstract class with some sensible default implementations for some of the methods of {@link Function}.
 */
abstract public class AbstractFunction implements Function {

	private List<Type> allowedTypes;
	private boolean canLastTypeRepeat;

	/**
	 * Constructor
	 */
	public AbstractFunction(List<Type> allowedTypes, boolean canLastTypeRepeat) {
		this.allowedTypes = allowedTypes;
		this.canLastTypeRepeat = canLastTypeRepeat;
	}

	/**
	 * Bepaalt of de functie de parameters accepteert.
	 */
	public boolean acceptsParameters(List<Type> types) {
		// Bepaal of de types overeenkomen.
		return determineTypesMatch(types);
	}

	/**
	 * Geeft terug of beide functies hetzelfde zijn, dit is het geval wanneer:
	 *
	 * - de functies dezelfde parameters accepteren.
	 */
	public boolean equals(Function function) {
		// Bepaal of de types overeenkomen.
		return determineTypesMatch(((AbstractFunction) function).allowedTypes);
	}

	/**
	 * Bepaalt of de meegegeven types matchen met de toegestane types.
	 */
	protected boolean determineTypesMatch(List<Type> types) {
		// Wanneer beide null zijn of lengte 0 hebben komen de types overeen.
		if ((types == null || types.size() == 0) && (allowedTypes == null || allowedTypes.size() == 0)) {
			return true;
		}
		
		// Wanneer slechts een van beide null is, komen de types niet overeen.
		if (types == null || allowedTypes == null) {
			return false;
		}

		// Wanneer de lengte van de inkomende array korter is dan de lengte van de toegestane parameters matchen ze 
		// sowieso niet.
		if (types.size() < allowedTypes.size()) {
			return false;
		}

		// Wanneer de lengte van de inkomende array langer is dan de lengte van de toegestane parameters en het laatste
		// argument mag zich niet herhalen matchen ze niet.
		if (types.size() > allowedTypes.size() && !canLastTypeRepeat) {
			return false;
		}

		// Variabele die we in de loop gebruiken die het huidige toegestane type aangeeft.
		Type currentType = null;

		// Loop over alle argumenten heen en controleer of ze het juiste type hebben.
		for (int i = 0; i < types.size(); i++) {
			// Bepaal het huidige type, dit is in principe het huidige element, behalve als de array langer is, dan is 
			// het het laatste element (oftewel het blijft het laatste element).
			currentType = i < allowedTypes.size() ? allowedTypes.get(i) : currentType;

			// Wanneer de types niet overeenkomen geven we false terug.
			if (!Type.equals(currentType, types.get(i))) {
				return false;
			}
		}

		// De types matchen.
		return true;
	}
}
