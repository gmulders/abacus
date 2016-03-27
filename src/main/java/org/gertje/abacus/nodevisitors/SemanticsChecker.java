package org.gertje.abacus.nodevisitors;

import org.gertje.abacus.nodes.AddNode;
import org.gertje.abacus.nodes.AndNode;
import org.gertje.abacus.nodes.AssignmentNode;
import org.gertje.abacus.nodes.BooleanNode;
import org.gertje.abacus.nodes.DateNode;
import org.gertje.abacus.nodes.DecimalNode;
import org.gertje.abacus.nodes.DivideNode;
import org.gertje.abacus.nodes.EqNode;
import org.gertje.abacus.nodes.ExpressionNode;
import org.gertje.abacus.nodes.FactorNode;
import org.gertje.abacus.nodes.FunctionNode;
import org.gertje.abacus.nodes.GeqNode;
import org.gertje.abacus.nodes.GtNode;
import org.gertje.abacus.nodes.IfNode;
import org.gertje.abacus.nodes.IntegerNode;
import org.gertje.abacus.nodes.LeqNode;
import org.gertje.abacus.nodes.LtNode;
import org.gertje.abacus.nodes.ModuloNode;
import org.gertje.abacus.nodes.MultiplyNode;
import org.gertje.abacus.nodes.NegativeNode;
import org.gertje.abacus.nodes.NeqNode;
import org.gertje.abacus.nodes.Node;
import org.gertje.abacus.nodes.NotNode;
import org.gertje.abacus.nodes.NullNode;
import org.gertje.abacus.nodes.OrNode;
import org.gertje.abacus.nodes.PositiveNode;
import org.gertje.abacus.nodes.PowerNode;
import org.gertje.abacus.nodes.RootNode;
import org.gertje.abacus.nodes.StatementListNode;
import org.gertje.abacus.nodes.StringNode;
import org.gertje.abacus.nodes.SubstractNode;
import org.gertje.abacus.nodes.VariableNode;
import org.gertje.abacus.symboltable.NoSuchFunctionException;
import org.gertje.abacus.symboltable.NoSuchVariableException;
import org.gertje.abacus.symboltable.SymbolTable;
import org.gertje.abacus.types.Type;
import org.gertje.abacus.util.SemanticsHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Semantics checker for an AST.
 */
public class SemanticsChecker implements NodeVisitor<Void, SemanticsCheckException> {

	/**
	 * De symboltable met de variabelen en de functies.
	 */
	private SymbolTable symbolTable;

	/**
	 * Constructor.
	 */
	public SemanticsChecker(SymbolTable symbolTable) {
		this.symbolTable = symbolTable;
	}

	public void check(Node node) throws SemanticsCheckException {
		node.accept(this);
	}

	@Override
	public Void visit(AddNode node) throws SemanticsCheckException {
		ExpressionNode lhs = node.getLhs();
		ExpressionNode rhs = node.getRhs();

		lhs.accept(this);
		rhs.accept(this);

		// Wanneer niet beide zijden van het type 'String' of 'Number' zijn moeten we een exceptie gooien.
		if (!(Type.isStringOrUnknown(lhs.getType()) && Type.isStringOrUnknown(rhs.getType()))
				&& !(Type.isNumberOrUnknown(lhs.getType()) && Type.isNumberOrUnknown(rhs.getType()))) {
			throw new SemanticsCheckException(SemanticsHelper.ADD_ILLEGAL_OPERAND_TYPES, node);
		}

		return null;
	}

	@Override
	public Void visit(AndNode node) throws SemanticsCheckException {
		ExpressionNode lhs = node.getLhs();
		ExpressionNode rhs = node.getRhs();

		lhs.accept(this);
		rhs.accept(this);

		// Beide zijden moeten van het type Boolean of onbekend zijn.
		if (!Type.isBooleanOrUnknown(lhs.getType()) || !Type.isBooleanOrUnknown(rhs.getType())) {
			throw new SemanticsCheckException(SemanticsHelper.AND_ILLEGAL_OPERAND_TYPES, node);
		}

		return null;
	}

	@Override
	public Void visit(AssignmentNode node) throws SemanticsCheckException {
		ExpressionNode lhs = node.getLhs();
		ExpressionNode rhs = node.getRhs();

		lhs.accept(this);
		rhs.accept(this);

		// Als de linkerkant geen VariabeleNode is EN de linkerkant is geen AssignmentNode met aan de rechterkant een
		// VariableNode, dan gooien we een exceptie.
		if (!(lhs instanceof VariableNode)
				&& !((lhs instanceof AssignmentNode) && (((AssignmentNode)lhs).getRhs() instanceof VariableNode))) {
			throw new SemanticsCheckException(SemanticsHelper.ASSIGNMENT_ILLEGAL_LEFT_OPERAND, node);
		}

		// Controleer of de types van de linker en de rechterkant overeenkomen.
		if (!SemanticsHelper.checkAssignmentType(lhs.getType(), rhs.getType())) {
			throw new SemanticsCheckException(SemanticsHelper.ASSIGNMENT_ILLEGAL_RIGHT_OPERAND, node);
		}

		return null;
	}

	@Override
	public Void visit(BooleanNode node) throws SemanticsCheckException {
		return null;
	}

	@Override
	public Void visit(DateNode node) throws SemanticsCheckException {
		return null;
	}

	@Override
	public Void visit(DecimalNode node) throws SemanticsCheckException {
		return null;
	}

	@Override
	public Void visit(DivideNode node) throws SemanticsCheckException {
		ExpressionNode lhs = node.getLhs();
		ExpressionNode rhs = node.getRhs();

		lhs.accept(this);
		rhs.accept(this);

		// Beide zijden moeten van het type 'number', of een onbekend type zijn.
		if (!Type.isNumberOrUnknown(lhs.getType()) || !Type.isNumberOrUnknown(rhs.getType())) {
			throw new SemanticsCheckException(SemanticsHelper.DIVIDE_ILLEGAL_OPERAND_TYPES, node);
		}

		return null;
	}

	@Override
	public Void visit(EqNode node) throws SemanticsCheckException {
		ExpressionNode lhs = node.getLhs();
		ExpressionNode rhs = node.getRhs();

		lhs.accept(this);
		rhs.accept(this);

		// Controleer of de types voorkomen in de lijst.
		if (!SemanticsHelper.checkComparisonTypes(lhs.getType(), rhs.getType(), SemanticsHelper.ALLOWED_TYPES_EQ_NEQ)) {
			throw new SemanticsCheckException(SemanticsHelper.EQ_ILLEGAL_OPERAND_TYPES, node);
		}

		return null;
	}

	@Override
	public Void visit(FactorNode node) throws SemanticsCheckException {
		ExpressionNode argument = node.getArgument();

		argument.accept(this);

		// We hoeven in deze node verder niets te controleren.

		return null;
	}

	@Override
	public Void visit(FunctionNode node) throws SemanticsCheckException {
		List<ExpressionNode> parameters = node.getParameters();
		String identifier = node.getIdentifier();

		// Maak een lijst van Objecten aan waarin we de parameters gaan evalueren.
		List<Type> types = new ArrayList<>();

		// Loop over alle nodes heen.
		for (ExpressionNode parameter : parameters) {
			parameter.accept(this);
			// Voeg het type van de node toe aan de lijst.
			types.add(parameter.getType());
		}

		// Controleer of de variabele bestaat. Als deze niet bestaat gooien we een exceptie.
		if (!symbolTable.getExistsFunction(identifier, types)) {
			throw new SemanticsCheckException("Function '" + identifier + "' does not exist.", node);
		}

		// Haal het type van de variabele op en zet deze op de node.
		// TODO: Moet dit wel hier gebeuren? Zie ook TODO hieronder.
		try {
			node.setReturnType(symbolTable.getFunctionReturnType(identifier, parameters));
		} catch (NoSuchFunctionException e) {
			throw new SemanticsCheckException(e.getMessage(), node);
		}

		return null;
	}

	@Override
	public Void visit(GeqNode node) throws SemanticsCheckException {
		ExpressionNode lhs = node.getLhs();
		ExpressionNode rhs = node.getRhs();

		lhs.accept(this);
		rhs.accept(this);

		// Controleer of de types voorkomen in de lijst.
		if (!SemanticsHelper.checkComparisonTypes(lhs.getType(), rhs.getType(),
				SemanticsHelper.ALLOWED_TYPES_GEQ_GT_LEQ_LT)) {
			throw new SemanticsCheckException(SemanticsHelper.GEQ_ILLEGAL_OPERAND_TYPES, node);
		}

		return null;
	}

	@Override
	public Void visit(GtNode node) throws SemanticsCheckException {
		ExpressionNode lhs = node.getLhs();
		ExpressionNode rhs = node.getRhs();

		lhs.accept(this);
		rhs.accept(this);

		// Controleer of de types voorkomen in de lijst.
		if (!SemanticsHelper.checkComparisonTypes(lhs.getType(), rhs.getType(),
				SemanticsHelper.ALLOWED_TYPES_GEQ_GT_LEQ_LT)) {
			throw new SemanticsCheckException(SemanticsHelper.GT_ILLEGAL_OPERAND_TYPES, node);
		}

		return null;
	}

	@Override
	public Void visit(IfNode node) throws SemanticsCheckException {
		ExpressionNode condition = node.getCondition();
		ExpressionNode ifbody = node.getIfBody();
		ExpressionNode elsebody = node.getElseBody();

		condition.accept(this);
		ifbody.accept(this);
		elsebody.accept(this);

		// De waarde van de conditie moet van het type 'boolean' zijn.
 		if (!Type.isBooleanOrUnknown(condition.getType())) {
			throw new SemanticsCheckException(SemanticsHelper.IF_ILLEGAL_CONDITION_TYPE, node);
		}

		// De waardes van beide bodies moeten van het zelfde type zijn of een van beide mag null zijn.
		if (!SemanticsHelper.checkTypeCompatibility(ifbody.getType(), elsebody.getType())) {
			throw new SemanticsCheckException(SemanticsHelper.IF_ILLEGAL_BODY_TYPES, node);
		}

		// Determine the type of the node.
		Type type;

		// Find the first non-null type.
		if (ifbody.getType() != null) {
			type = ifbody.getType();
		} else {
			type = elsebody.getType();
		}

		// If the found type is of type Integer, check if the elsebody is of type Decimal. If so, widen the type to
		// Decimal.
		if (type == Type.INTEGER && elsebody.getType() == Type.DECIMAL) {
			type = Type.DECIMAL;
		}

		// Set the type of the node.
		node.setType(type);

		return null;
	}

	@Override
	public Void visit(IntegerNode node) throws SemanticsCheckException {
		return null;
	}

	@Override
	public Void visit(LeqNode node) throws SemanticsCheckException {
		ExpressionNode lhs = node.getLhs();
		ExpressionNode rhs = node.getRhs();

		lhs.accept(this);
		rhs.accept(this);

		// Controleer of de types voorkomen in de lijst.
		if (!SemanticsHelper.checkComparisonTypes(lhs.getType(), rhs.getType(),
				SemanticsHelper.ALLOWED_TYPES_GEQ_GT_LEQ_LT)) {
			throw new SemanticsCheckException(SemanticsHelper.LEQ_ILLEGAL_OPERAND_TYPES, node);
		}

		return null;
	}

	@Override
	public Void visit(LtNode node) throws SemanticsCheckException {
		ExpressionNode lhs = node.getLhs();
		ExpressionNode rhs = node.getRhs();

		lhs.accept(this);
		rhs.accept(this);

		// Controleer of de types voorkomen in de lijst.
		if (!SemanticsHelper.checkComparisonTypes(lhs.getType(), rhs.getType(),
				SemanticsHelper.ALLOWED_TYPES_GEQ_GT_LEQ_LT)) {
			throw new SemanticsCheckException(SemanticsHelper.LT_ILLEGAL_OPERAND_TYPES, node);
		}

		return null;
	}

	@Override
	public Void visit(ModuloNode node) throws SemanticsCheckException {
		ExpressionNode lhs = node.getLhs();
		ExpressionNode rhs = node.getRhs();

		lhs.accept(this);
		rhs.accept(this);

		// Beide zijden moeten van het type 'number', of een onbekend type zijn.
		if (!Type.isNumberOrUnknown(lhs.getType()) || !Type.isNumberOrUnknown(rhs.getType())) {
			throw new SemanticsCheckException(SemanticsHelper.MODULO_ILLEGAL_OPERAND_TYPES, node);
		}

		return null;
	}

	@Override
	public Void visit(MultiplyNode node) throws SemanticsCheckException {
		ExpressionNode lhs = node.getLhs();
		ExpressionNode rhs = node.getRhs();

		lhs.accept(this);
		rhs.accept(this);

		// Beide zijden moeten van het type 'number', of een onbekend type zijn.
		if (!Type.isNumberOrUnknown(lhs.getType()) || !Type.isNumberOrUnknown(rhs.getType())) {
			throw new SemanticsCheckException(SemanticsHelper.MULTIPLY_ILLEGAL_OPERAND_TYPES, node);
		}

		return null;
	}

	@Override
	public Void visit(NegativeNode node) throws SemanticsCheckException {
		ExpressionNode argument = node.getArgument();

		argument.accept(this);

		// Het argument moet een getal of onbekend zijn.
		if (!Type.isNumberOrUnknown(argument.getType())) {
			throw new SemanticsCheckException(SemanticsHelper.NEGATIVE_ILLEGAL_OPERAND_TYPE, node);
		}

		return null;
	}

	@Override
	public Void visit(NeqNode node) throws SemanticsCheckException {
		ExpressionNode lhs = node.getLhs();
		ExpressionNode rhs = node.getRhs();

		lhs.accept(this);
		rhs.accept(this);

		// Controleer of de types voorkomen in de lijst.
		if (!SemanticsHelper.checkComparisonTypes(lhs.getType(), rhs.getType(), SemanticsHelper.ALLOWED_TYPES_EQ_NEQ)) {
			throw new SemanticsCheckException(SemanticsHelper.NEQ_ILLEGAL_OPERAND_TYPES, node);
		}

		return null;
	}

	@Override
	public Void visit(NotNode node) throws SemanticsCheckException {
		ExpressionNode argument = node.getArgument();

		argument.accept(this);

		// Het argument moet een boolean zijn.
		if (!Type.isBooleanOrUnknown(argument.getType())) {
			throw new SemanticsCheckException(SemanticsHelper.NOT_ILLEGAL_OPERAND_TYPE, node);
		}

		return null;
	}

	@Override
	public Void visit(NullNode node) throws SemanticsCheckException {
		return null;
	}

	@Override
	public Void visit(OrNode node) throws SemanticsCheckException {
		ExpressionNode lhs = node.getLhs();
		ExpressionNode rhs = node.getRhs();

		lhs.accept(this);
		rhs.accept(this);

		// Beide zijden moeten van het type Boolean of onbekend zijn.
		if (!Type.isBooleanOrUnknown(lhs.getType()) || !Type.isBooleanOrUnknown(rhs.getType())) {
			throw new SemanticsCheckException(SemanticsHelper.OR_ILLEGAL_OPERAND_TYPES, node);
		}

		return null;
	}

	@Override
	public Void visit(PositiveNode node) throws SemanticsCheckException {
		ExpressionNode argument = node.getArgument();

        argument.accept(this);

		// Het argument moet een 'number' of onbekend zijn.
		if (!Type.isNumberOrUnknown(argument.getType())) {
			throw new SemanticsCheckException(SemanticsHelper.POSITIVE_ILLEGAL_OPERAND_TYPE, node);
		}

		return null;
	}

	@Override
	public Void visit(PowerNode node) throws SemanticsCheckException {
		ExpressionNode base = node.getBase();
		ExpressionNode power = node.getPower();

		base.accept(this);
		power.accept(this);

		// Beide zijden moeten van het type 'number' of onbekend zijn.
		if (!Type.isNumberOrUnknown(base.getType()) || !Type.isNumberOrUnknown(power.getType())) {
			throw new SemanticsCheckException(SemanticsHelper.POWER_ILLEGAL_OPERAND_TYPES, node);
		}

		return null;
	}

	@Override
	public Void visit(RootNode node) throws SemanticsCheckException {
		return node.getStatementListNode().accept(this);
	}

	@Override
	public Void visit(StatementListNode node) throws SemanticsCheckException {
		// Accepteer voor alle nodes.
		for (Node subNode : node) {
			subNode.accept(this);
		}

		return null;
	}

	@Override
	public Void visit(StringNode node) throws SemanticsCheckException {
		return null;
	}

	@Override
	public Void visit(SubstractNode node) throws SemanticsCheckException {
		ExpressionNode lhs = node.getLhs();
		ExpressionNode rhs = node.getRhs();

		lhs.accept(this);
		rhs.accept(this);

		// Beide zijden moeten van het type 'number' of onbekend zijn.
		if (!Type.isNumberOrUnknown(rhs.getType()) || !Type.isNumberOrUnknown(lhs.getType())) {
				throw new SemanticsCheckException(SemanticsHelper.SUBSTRACT_ILLEGAL_OPERAND_TYPES, node);
		}

		return null;
	}

	@Override
	public Void visit(VariableNode node) throws SemanticsCheckException {
		String identifier = node.getIdentifier();

		// Controleer of de variabele bestaat. Als deze niet bestaat gooien we een exceptie.
		if (!symbolTable.getExistsVariable(identifier)) {
			throw new SemanticsCheckException("Variable '" + identifier + "' does not exist.", node);
		}

		try {
			// Haal het type van de variabele op.
			// TODO: Moet dit wel hier gebeuren? Zie ook TODO hierboven.
			node.setType(symbolTable.getVariableType(identifier));
		} catch (NoSuchVariableException e) {
			throw new SemanticsCheckException(e.getMessage(), node);
		}

		return null;
	}
}

