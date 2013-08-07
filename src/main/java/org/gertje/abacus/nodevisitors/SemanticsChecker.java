package org.gertje.abacus.nodevisitors;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import org.gertje.abacus.nodes.AbstractNode;
import org.gertje.abacus.nodes.AddNode;
import org.gertje.abacus.nodes.AndNode;
import org.gertje.abacus.nodes.AssignmentNode;
import org.gertje.abacus.nodes.BooleanNode;
import org.gertje.abacus.nodes.DateNode;
import org.gertje.abacus.nodes.DivideNode;
import org.gertje.abacus.nodes.EqNode;
import org.gertje.abacus.nodes.FactorNode;
import org.gertje.abacus.nodes.FloatNode;
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
import org.gertje.abacus.nodes.NotNode;
import org.gertje.abacus.nodes.NullNode;
import org.gertje.abacus.nodes.OrNode;
import org.gertje.abacus.nodes.PositiveNode;
import org.gertje.abacus.nodes.PowerNode;
import org.gertje.abacus.nodes.StatementListNode;
import org.gertje.abacus.nodes.StringNode;
import org.gertje.abacus.nodes.SubstractNode;
import org.gertje.abacus.nodes.VariableNode;
import org.gertje.abacus.symboltable.SymbolTable;
import org.gertje.abacus.symboltable.NoSuchFunctionException;
import org.gertje.abacus.symboltable.NoSuchVariableException;

public class SemanticsChecker extends AbstractNodeVisitor<Void, SemanticsCheckException> {

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

	public Void check(AbstractNode node) throws SemanticsCheckException {
		node.accept(this);

		return null;
	}

	@Override
	public Void visit(AddNode node) throws SemanticsCheckException {
		AbstractNode lhs = node.getLhs();
		AbstractNode rhs = node.getRhs();

		lhs.accept(this);
		rhs.accept(this);

		// Wanneer niet beide zijden van het type 'String' of 'Number' zijn moeten we een exceptie gooien.
		if (!(lhs.getType().equals(String.class) && rhs.getType().equals(String.class))
				&& !(isNumber(lhs.getType()) && isNumber(rhs.getType()))) {
			throw new SemanticsCheckException(
					"Expected two parameters of type 'Number' or type 'String' to ADD-expression.",
					node);
		}

		return null;
	}

	@Override
	public Void visit(AndNode node) throws SemanticsCheckException {
		AbstractNode lhs = node.getLhs();
		AbstractNode rhs = node.getRhs();

		lhs.accept(this);
		rhs.accept(this);

		// Beide zijden moeten van het type Boolean zijn.
		if (!lhs.getType().equals(Boolean.class) || !rhs.getType().equals(Boolean.class)) {
			throw new SemanticsCheckException("Expected two boolean parameters to AND-expression.", node);
		}

		return null;
	}

	@Override
	public Void visit(AssignmentNode node) throws SemanticsCheckException {
		AbstractNode lhs = node.getLhs();
		AbstractNode rhs = node.getRhs();

		lhs.accept(this);
		rhs.accept(this);

		// Als de linkerkant geen VariabeleNode is EN de linkerkant is geen AssignmentNode met aan de rechterkant een
		// VariableNode, dan gooien we een exceptie.
		if (!(lhs instanceof VariableNode) 
				&& !((lhs instanceof AssignmentNode) && (((AssignmentNode)lhs).getRhs() instanceof VariableNode))) {
			throw new SemanticsCheckException("Left side of assignment should be a variable or an assignment.",
					node);
		}

		// Controleer of de types van de linker en de rechterkant overeenkomen.
		if (lhs.getType() != rhs.getType()) {
			throw new SemanticsCheckException("Expected expression of the same type as the variable.", node);
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
	public Void visit(DivideNode node) throws SemanticsCheckException {
		AbstractNode lhs = node.getLhs();
		AbstractNode rhs = node.getRhs();

		lhs.accept(this);
		rhs.accept(this);

		// Beide zijden moeten van het type 'number' zijn.
		if (!isNumber(lhs.getType()) || !isNumber(rhs.getType())) {
			throw new SemanticsCheckException("Expected two parameters of type 'number' to divide-expression.",
					node);
		}

		return null;
	}

	@Override
	public Void visit(EqNode node) throws SemanticsCheckException {
		AbstractNode lhs = node.getLhs();
		AbstractNode rhs = node.getRhs();

		lhs.accept(this);
		rhs.accept(this);

		// Een == vergelijking kan booleans, getallen, strings en datums vergelijken.
		List<Class<?>> allowedTypes = new ArrayList<Class<?>>();
		allowedTypes.add(Boolean.class);
		allowedTypes.add(BigDecimal.class);
		allowedTypes.add(String.class);
		allowedTypes.add(Date.class);

		// Controleer of de types voorkomen in de lijst.
		if (!checkComparisonTypes(lhs.getType(), rhs.getType(), allowedTypes)) {
			throw new SemanticsCheckException("Expected two parameters of the same type to comparison-expression.",
					node);
		}

		return null;
	}

	@Override
	public Void visit(FactorNode node) throws SemanticsCheckException {
		AbstractNode argument = node.getArgument();

		argument.accept(this);

		// We hoeven in deze node verder niets te controleren.

		return null;
	}

	@Override
	public Void visit(FloatNode node) throws SemanticsCheckException {
		return null;
	}

	@Override
	public Void visit(FunctionNode node) throws SemanticsCheckException {
		List<AbstractNode> parameters = node.getParameters();
		String identifier = node.getIdentifier();

		// Maak een lijst van Objecten aan waarin we de parameters gaan evalueren.
		List<Class<?>> types = new ArrayList<Class<?>>();

		// Loop over alle nodes heen.
		for (AbstractNode parameter : parameters) {
			parameter.accept(this);
			// Voeg het type van de node toe aan de lijst.
			types.add(parameter.getType());
		}

		// Controleer of de variabele bestaat. Als deze niet bestaat gooien we een exceptie.
		if (!symbolTable.getExistsFunction(identifier, types)) {
			throw new SemanticsCheckException("Function '" + identifier + "' does not exist.", node);
		}

		// Haal het type van de variabele op en zet deze op de node.
		// TODO: Moet dit wel hier gebeuren?
		try {
			node.setReturnType(symbolTable.getFunctionReturnType(identifier, parameters));
		} catch (NoSuchFunctionException e) {
			throw new SemanticsCheckException(e.getMessage(), node);
		}

		return null;
	}

	@Override
	public Void visit(GeqNode node) throws SemanticsCheckException {
		AbstractNode lhs = node.getLhs();
		AbstractNode rhs = node.getRhs();

		lhs.accept(this);
		rhs.accept(this);

		// Een >= vergelijking kan getallen, strings en datums vergelijken.
		List<Class<?>> allowedTypes = new ArrayList<Class<?>>();
		allowedTypes.add(BigDecimal.class);
		allowedTypes.add(String.class);
		allowedTypes.add(Date.class);

        // Controleer of de types voorkomen in de lijst.
		if (!checkComparisonTypes(lhs.getType(), rhs.getType(), allowedTypes)) {
			throw new SemanticsCheckException("Expected two parameters of the same type to comparison-expression.",
					node);
		}

		return null;
	}

	@Override
	public Void visit(GtNode node) throws SemanticsCheckException {
		AbstractNode lhs = node.getLhs();
		AbstractNode rhs = node.getRhs();

		lhs.accept(this);
		rhs.accept(this);

		// Een > vergelijking kan getallen, strings en datums vergelijken.
		List<Class<?>> allowedTypes = new ArrayList<Class<?>>();
		allowedTypes.add(BigDecimal.class);
		allowedTypes.add(String.class);
		allowedTypes.add(Date.class);

		// Controleer of de types voorkomen in de lijst.
		if (!checkComparisonTypes(lhs.getType(), rhs.getType(), allowedTypes)) {
			throw new SemanticsCheckException("Expected two parameters of the same type to comparison-expression.",
					node);
		}

		return null;
	}

	@Override
	public Void visit(IfNode node) throws SemanticsCheckException {
		AbstractNode condition = node.getCondition();
		AbstractNode ifbody = node.getIfBody();
		AbstractNode elsebody = node.getElseBody();

		// De waarde van de conditie moet van het type 'boolean' zijn.
 		if (!condition.getType().equals(Boolean.class)) {
			throw new SemanticsCheckException("Expected boolean parameter to IF-expression.", node);
		}

		// De waardes van beide bodies moeten van het zelfde type zijn of een van beide mag null zijn.
		if (ifbody.getType() != elsebody.getType()
				&& !ifbody.getType().equals(Object.class)
				&& !elsebody.getType().equals(Object.class)) {
			throw new SemanticsCheckException("IF-body and ELSE-body should have the same type.", node);
		}

		// De waardes van de bodies mogen niet allebei null zijn.
		if (ifbody.getType().equals(Object.class) && elsebody.getType().equals(Object.class)) {
			throw new SemanticsCheckException("IF-body and ELSE-body should not be both null.", node);
		}

		return null;
	}

	@Override
	public Void visit(IntegerNode node) throws SemanticsCheckException {
		return null;
	}

	@Override
	public Void visit(LeqNode node) throws SemanticsCheckException {
		AbstractNode lhs = node.getLhs();
		AbstractNode rhs = node.getRhs();

		lhs.accept(this);
		rhs.accept(this);

		// Een <= vergelijking kan getallen, strings en datums vergelijken.
		List<Class<?>> allowedTypes = new ArrayList<Class<?>>();
		allowedTypes.add(BigDecimal.class);
		allowedTypes.add(String.class);
		allowedTypes.add(Date.class);

		// Controleer of de types voorkomen in de lijst.
		if (!checkComparisonTypes(lhs.getType(), rhs.getType(), allowedTypes)) {
			throw new SemanticsCheckException(
					"Expected two parameters of the same type to less-then-equals-expression.",
					node);
		}

		return null;
	}

	@Override
	public Void visit(LtNode node) throws SemanticsCheckException {
		AbstractNode lhs = node.getLhs();
		AbstractNode rhs = node.getRhs();

		lhs.accept(this);
		rhs.accept(this);

		// Een > vergelijking kan getallen, strings en datums vergelijken.
		List<Class<?>> allowedTypes = new ArrayList<Class<?>>();
		allowedTypes.add(BigDecimal.class);
		allowedTypes.add(String.class);
		allowedTypes.add(Date.class);

		// Controleer of de types voorkomen in de lijst.
		if (!checkComparisonTypes(lhs.getType(), rhs.getType(), allowedTypes)) {
			throw new SemanticsCheckException("Expected two parameters of the same type to less-then-expression.",
					node);
		}

		return null;
	}

	@Override
	public Void visit(ModuloNode node) throws SemanticsCheckException {
		AbstractNode lhs = node.getLhs();
		AbstractNode rhs = node.getRhs();

		lhs.accept(this);
		rhs.accept(this);

		// Beide zijden moeten van het type 'number' zijn.
 		if (!isNumber(lhs.getType()) || !isNumber(rhs.getType())) {
			throw new SemanticsCheckException("Expected two parameters of type 'number' to modulo-expression.", node);
		}

		return null;
	}

	@Override
	public Void visit(MultiplyNode node) throws SemanticsCheckException {
		AbstractNode lhs = node.getLhs();
		AbstractNode rhs = node.getRhs();

		lhs.accept(this);
		rhs.accept(this);

		// Beide zijden moeten van het type 'number' zijn.
 		if (!isNumber(lhs.getType()) || !isNumber(rhs.getType())) {
			throw new SemanticsCheckException("Expected two parameters of type 'number' to multiply-expression.",
					node);
		}

		return null;
	}

	@Override
	public Void visit(NegativeNode node) throws SemanticsCheckException {
		AbstractNode argument = node.getArgument();

		argument.accept(this);

		// Het argument moet een getal zijn.
		if (!isNumber(argument.getType())) {
			throw new SemanticsCheckException("Expected a number expression in NegativeNode.", node);
		}

		return null;
	}

	@Override
	public Void visit(NeqNode node) throws SemanticsCheckException {
		AbstractNode lhs = node.getLhs();
		AbstractNode rhs = node.getRhs();

		lhs.accept(this);
		rhs.accept(this);

		// Een == vergelijking kan booleans, getallen, strings en datums vergelijken.
		List<Class<?>> allowedTypes = new ArrayList<Class<?>>();
		allowedTypes.add(Boolean.class);
		allowedTypes.add(BigDecimal.class);
		allowedTypes.add(String.class);
		allowedTypes.add(Date.class);

		// Controleer of de types voorkomen in de lijst.
		if (!checkComparisonTypes(lhs.getType(), rhs.getType(), allowedTypes)) {
			throw new SemanticsCheckException("Expected two parameters of the same type to not-equals-expression.",
					node);
		}

		return null;
	}

	@Override
	public Void visit(NotNode node) throws SemanticsCheckException {
		AbstractNode argument = node.getArgument();

		argument.accept(this);

		// Het argument moet een boolean zijn.
		if (argument.getType().equals(Boolean.class)) {
			throw new SemanticsCheckException("Expected a boolean expression in NotNode.", node);
		}

		return null;
	}

	@Override
	public Void visit(NullNode node) throws SemanticsCheckException {
		return null;
	}

	@Override
	public Void visit(OrNode node) throws SemanticsCheckException {
		AbstractNode lhs = node.getLhs();
		AbstractNode rhs = node.getRhs();

		lhs.accept(this);
		rhs.accept(this);

		// Beide zijden moeten van het type Boolean zijn.
		if (!lhs.getType().equals(Boolean.class) || !rhs.getType().equals(Boolean.class)) {
			throw new SemanticsCheckException("Expected two boolean parameters to OR-expression.", node);
		}

		return null;
	}

	@Override
	public Void visit(PositiveNode node) throws SemanticsCheckException {
		AbstractNode argument = node.getArgument();

        argument.accept(this);

		// Het argument moet een float of een integer zijn.
		if (!argument.getType().equals(BigDecimal.class) && !argument.getType().equals(BigInteger.class)) {
			throw new SemanticsCheckException("Expected a number expression in PositiveNode.", node);
		}

		return null;
	}

	@Override
	public Void visit(PowerNode node) throws SemanticsCheckException {
		AbstractNode base = node.getBase();
		AbstractNode power = node.getPower();

		base.accept(this);
		power.accept(this);
		
		// Beide zijden moeten van het type 'number' zijn.
		if (!isNumber(base.getType()) || !isNumber(power.getType())) {
			throw new SemanticsCheckException("Expected two parameters of type 'number' to POWER-expression.", node);
		}

		return null;
	}

	@Override
	public Void visit(StatementListNode node) throws SemanticsCheckException {
		// Accepteer voor alle nodes.
		for (AbstractNode subNode : node) {
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
		AbstractNode lhs = node.getLhs();
		AbstractNode rhs = node.getRhs();

		lhs.accept(this);
		rhs.accept(this);

		// Beide zijden moeten van het type 'number' zijn.
		if (!isNumber(rhs.getType()) || !isNumber(lhs.getType())) {
			throw new SemanticsCheckException("Expected two parameters of number type to SUBSTRACT-expression.", node);
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

		Class<?> type;
		try {
			// Haal het type van de variabele op.
			type = symbolTable.getVariableType(identifier);
		} catch (NoSuchVariableException e) {
			throw new SemanticsCheckException(e.getMessage(), node);
		}

		node.setType(type);

		return null;
	}

    /**
	 * Bepaalt of het meegegeven type een nummer is.
	 * @param type Het type waarvan de methode bepaalt of het een nummer is.
	 * @return <code>true</code> wanneer het meegegeven type een nummer is, anders <code>false</code>.
	 */
	protected boolean isNumber(Class<?> type) {
		return BigDecimal.class.equals(type) || BigInteger.class.equals(type);
	}

    /**
	 * Controleert de typen van de lhs en de rhs, wanneer beiden niet van het zelfde type zijn of ze komen niet voor in
	 * de lijst met toegestane typen geeft de methode false terug.
	 * @return <code>true</code> wanneer de typen goed zijn, anders <code>false</code>.
	 */
	private boolean checkComparisonTypes(Class<?> lhsType, Class<?> rhsType, List<Class<?>> allowedTypes) {
		// We casten de BigInteger's naar BigDecimal's, omdat dit makkelijk te vergelijken is.
		if (lhsType.equals(BigInteger.class)) {
			lhsType = BigDecimal.class;
		}
		// We casten de BigInteger's naar BigDecimal's, omdat dit makkelijk te vergelijken is.
		if (rhsType.equals(BigInteger.class)) {
			rhsType = BigDecimal.class;
		}

		for(Class<?> type : allowedTypes) {		
			if (lhsType.equals(type) && rhsType.equals(type)) {
				return true;
			}
		}
		return false;
	}
}

