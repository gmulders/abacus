package org.gertje.abacus.util;

import org.gertje.abacus.nodes.EqNode;
import org.gertje.abacus.nodes.GeqNode;
import org.gertje.abacus.nodes.GtNode;
import org.gertje.abacus.nodes.LeqNode;
import org.gertje.abacus.nodes.LtNode;
import org.gertje.abacus.nodes.NeqNode;
import org.gertje.abacus.types.Type;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Helper klasse voor de controle op semantiek.
 */
public class SemanticsHelper {

	public static final String ADD_ILLEGAL_OPERAND_TYPES = "Expected two parameters of type 'Number' or type 'String' to ADD-expression.";
	public static final String AND_ILLEGAL_OPERAND_TYPES = "Expected two boolean parameters to AND-expression.";
	public static final String ARRAY_ILLEGAL_INDEX_TYPE = "Expected integer expression for array index.";
	public static final String ARRAY_ILLEGAL_ARRAY_TYPE = "Expected an array expression.";
	public static final String ASSIGNMENT_ILLEGAL_LEFT_OPERAND = "Left side of assignment should be a variable or an assignment.";
	public static final String ASSIGNMENT_ILLEGAL_RIGHT_OPERAND = "Expected expression of the same type as the variable.";
	public static final String DIVIDE_ILLEGAL_OPERAND_TYPES = "Expected two parameters of type 'number' to divide-expression.";
	public static final String EQ_ILLEGAL_OPERAND_TYPES = "Expected two parameters of the same type to comparison-expression.";
	public static final String GEQ_ILLEGAL_OPERAND_TYPES = "Expected two parameters of the same type to comparison-expression.";
	public static final String GT_ILLEGAL_OPERAND_TYPES = "Expected two parameters of the same type to comparison-expression.";
	public static final String IF_ILLEGAL_CONDITION_TYPE = "Expected boolean parameter to IF-expression.";
	public static final String IF_ILLEGAL_BODY_TYPES = "IF-body and ELSE-body should have the same type.";
	public static final String IF_ILLEGAL_BODY_NULL = "IF-body and ELSE-body should not be both null.";
	public static final String LEQ_ILLEGAL_OPERAND_TYPES = "Expected two parameters of the same type to less-then-equals-expression.";
	public static final String LT_ILLEGAL_OPERAND_TYPES = "Expected two parameters of the same type to less-then-expression.";
	public static final String MODULO_ILLEGAL_OPERAND_TYPES = "Expected two parameters of type 'number' to modulo-expression.";
	public static final String MULTIPLY_ILLEGAL_OPERAND_TYPES = "Expected two parameters of type 'number' to multiply-expression.";
	public static final String NEGATIVE_ILLEGAL_OPERAND_TYPE = "Expected a number expression in NegativeNode.";
	public static final String NEQ_ILLEGAL_OPERAND_TYPES = "Expected two parameters of the same type to not-equals-expression.";
	public static final String NOT_ILLEGAL_OPERAND_TYPE = "Expected a boolean expression in NotNode.";
	public static final String OR_ILLEGAL_OPERAND_TYPES = "Expected two boolean parameters to OR-expression.";
	public static final String POSITIVE_ILLEGAL_OPERAND_TYPE = "Expected a number expression in PositiveNode.";
	public static final String POWER_ILLEGAL_OPERAND_TYPES = "Expected two parameters of type 'number' to POWER-expression.";
	public static final String SUBSTRACT_ILLEGAL_OPERAND_TYPES = "Expected two parameters of number type to SUBSTRACT-expression.";

	/**
	 * Lijst met toegestane types voor de {@link EqNode} en de {@link NeqNode}.
	 */
	public static List<Type> ALLOWED_TYPES_EQ_NEQ
			= Collections.unmodifiableList(Arrays.asList(Type.BOOLEAN, Type.DECIMAL, Type.STRING, Type.DATE));

	/**
	 * Lijst met toegestane types voor de {@link GeqNode}, de {@link GtNode}, de {@link LeqNode} en {@link LtNode}.
	 */
	public static List<Type> ALLOWED_TYPES_GEQ_GT_LEQ_LT
			= Collections.unmodifiableList(Arrays.asList(Type.DECIMAL, Type.STRING, Type.DATE));

	/**
	 * Controleert de typen van de lhs en de rhs, wanneer beiden niet van het zelfde type zijn of ze komen niet voor in
	 * de lijst met toegestane typen geeft de methode false terug.
	 * @return <code>true</code> wanneer de typen goed zijn, anders <code>false</code>.
	 */
	public static boolean checkComparisonTypes(Type lhsType, Type rhsType, List<Type> allowedTypes) {
		// Wanneer zowel links als rechts null zijn, geven we true terug.
		if (lhsType == null && rhsType == null) {
			return true;
		}

		// We casten de Integer's naar Decimal's, omdat dit makkelijk te vergelijken is.
		if (Type.equals(lhsType, Type.INTEGER)) {
			lhsType = Type.DECIMAL;
		}
		// We casten de Integer's naar Decimal's, omdat dit makkelijk te vergelijken is.
		if (Type.equals(rhsType, Type.INTEGER)) {
			rhsType = Type.DECIMAL;
		}

		for(Type type : allowedTypes) {
			if ((Type.equals(lhsType, type) || lhsType == null) && (Type.equals(rhsType, type) || rhsType == null)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Checks whether the two given types are compatible for assignment.
	 * @param variableType The type of the variable to assign to.
	 * @param valueType The type of the value that gets assigned.
	 * @return {@code true} if the types are compatible, {@code false} otherwise.
	 */
	public static boolean checkAssignmentType(Type variableType, Type valueType) {
		// Wanneer het type van de waarde null is, is de waarde null. Deze situatie kan alleen voorkomen wanneer niet
		// eerst alle child-nodes van een node bezocht kunnen worden, bijv. wanneer we de tree interpreten. Neem bijv.
		// de expressie 'a = false ? (true ? 3 : 2) : null', hier weten we na het evalueren van de else-body dat de
		// waarde van de (buitenste) if 'null' is, maar omdat we de if-body nooit bepaald hebben weten we ook niet het
		// type van de buitenste if.
		if (valueType == null) {
			return true;
		}

		// Wanneer de beide types gelijk zijn, zijn ze compatible.
		if (Type.equals(variableType, valueType)) {
			return true;
		}

		// Wanneer de types allebei numeriek zijn, zijn ze ook compatible.
		if (Type.isNumber(variableType) && Type.isNumber(valueType)) {
			return true;
		}

		// Wanneer we hier komen zijn ze niet compatible.
		return false;
	}

	/**
	 * Checks the compatibility of the types.
	 * @param a The first type.
	 * @param b The second type.
	 * @return {@code true} if the types of are compatible, {@code false} otherwise.
	 */
	public static boolean checkTypeCompatibility(Type a, Type b) {
		// The types are compatible when they are equal.
		if (a == b) {
			return true;
		}

		// When one of the types is null, they are compatible.
		if (a == null || b == null) {
			return true;
		}

		// When the types differ, but are both numbers, they are compatible.
		if (Type.isNumber(a) && Type.isNumber(b)) {
			return true;
		}

		// Otherwise they are not compatible.
		return false;
	}

}
