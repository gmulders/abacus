package org.gertje.abacus.nodevisitors;

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
import org.gertje.abacus.symboltable.NoSuchFunctionException;
import org.gertje.abacus.symboltable.NoSuchVariableException;
import org.gertje.abacus.symboltable.SymbolTable;

public class Evaluator extends AbstractNodeVisitor<Object, EvaluationException> {

	/**
	 * De symboltable met de variabelen en de functies.
	 */
	private SymbolTable symbolTable;

	/**
	 * Constructor.
	 */
	public Evaluator(SymbolTable symbolTable) {
		this.symbolTable = symbolTable;
	}

	public Object evaluate(AbstractNode node) throws EvaluationException {
		return node.accept(this);
	}

	@Override
	public Object visit(AddNode node) throws EvaluationException {
		AbstractNode lhs = node.getLhs();
		AbstractNode rhs = node.getRhs();

		Object left = lhs.accept(this);
		Object right = rhs.accept(this);

		return EvaluationHelper.add(left, right);
	}

	@Override
	public Object visit(AndNode node) throws EvaluationException {
		AbstractNode lhs = node.getLhs();
		AbstractNode rhs = node.getRhs();

		Boolean left = (Boolean) lhs.accept(this);
		Boolean right = (Boolean) rhs.accept(this);

		return EvaluationHelper.and(left, right);
	}

	@Override
	public Object visit(AssignmentNode node) throws EvaluationException {
		AbstractNode lhs = node.getLhs();
		AbstractNode rhs = node.getRhs();

		// Evalueer de rechterkant van de toekenning.
		Object result = rhs.accept(this);

		// Zet het resultaat in de symboltable.
		// Wanneer de linkerkant een variabele is kunnen we het direct in de variabele zetten, anders moeten we eerst
		// de variabele uit de rechterkant halen.
		if (lhs instanceof VariableNode) {
			symbolTable.setVariableValue(((VariableNode) lhs).getIdentifier(), result);
		} else {
			symbolTable.setVariableValue(((VariableNode) ((AssignmentNode)lhs).getRhs()).getIdentifier(), result);
		}

		// Geef het resultaat terug.
		return result;
	}

	@Override
	public Object visit(BooleanNode node) throws EvaluationException {
		return node.getValue();
	}

	@Override
	public Object visit(DateNode node) throws EvaluationException {
		return node.getValue();
	}

	@Override
	public Object visit(DivideNode node) throws EvaluationException {
		AbstractNode lhs = node.getLhs();
		AbstractNode rhs = node.getRhs();

		Boolean left = (Boolean) lhs.accept(this);
		Boolean right = (Boolean) rhs.accept(this);

		return EvaluationHelper.divide(left, right);
	}

	@Override
	public Object visit(EqNode node) throws EvaluationException {
		AbstractNode lhs = node.getLhs();
		AbstractNode rhs = node.getRhs();

		Object left = lhs.accept(this);
		Object right = rhs.accept(this);

		return EvaluationHelper.eq(left, right);
	}

	@Override
	public Object visit(FactorNode node) throws EvaluationException {
		return node.getArgument().accept(this);
	}

	@Override
	public Object visit(FloatNode node) throws EvaluationException {
		return node.getValue();
	}

	@Override
	public Object visit(FunctionNode node) throws EvaluationException {
		List<AbstractNode> parameters = node.getParameters();
		String identifier = node.getIdentifier();

		// Maak een lijst met alle resultaten van de evaluatie van de parameters.
		List<Object> paramResults = new ArrayList<Object>();

		// Maak een lijst met alle types van de parameters.
		List<Class<?>> paramTypes = new ArrayList<Class<?>>();

		// Loop over alle nodes heen en vul de lijsten met de geevaluuerde waarde en het type.
		for (AbstractNode parameter : parameters) {
			paramResults.add(parameter.accept(this));
			paramTypes.add(parameter.getType());
		}
		
		try {
			return symbolTable.getFunctionReturnValue(identifier, paramResults, paramTypes);
		} catch (NoSuchFunctionException e) {
			throw new EvaluationException(e.getMessage(), node);
		}
	}

	@Override
	public Object visit(GeqNode node) throws EvaluationException {
		AbstractNode lhs = node.getLhs();
		AbstractNode rhs = node.getRhs();

		Object left = lhs.accept(this);
		Object right = rhs.accept(this);

		return EvaluationHelper.geq(left, right);
	}

	@Override
	public Object visit(GtNode node) throws EvaluationException {
		AbstractNode lhs = node.getLhs();
 		AbstractNode rhs = node.getRhs();

		Object left = lhs.accept(this);
		Object right = rhs.accept(this);

		return EvaluationHelper.gt(left, right);
	}

	@Override
	public Object visit(IfNode node) throws EvaluationException {
		AbstractNode condition = node.getCondition();
		AbstractNode ifBody = node.getIfBody();
		AbstractNode elseBody = node.getElseBody();

		// Evauleer de conditie.
		Boolean cond = (Boolean)condition.accept(this);

		// Wanneer de conditie leeg is kunnen we ook geen uitspraak doen over het resultaat, geef dus null terug.
		if (cond == null) {
			return null;
		}

		// Wanneer de conditie waar is geven we de geevalueerde if body terug, anders geven we de else body terug.
		if (Boolean.TRUE.equals(cond)) {
			return ifBody.accept(this);
		}

		// We moeten de else body geevaluateueerd teruggeven.
		return elseBody.accept(this);
	}

	@Override
	public Object visit(IntegerNode node) throws EvaluationException {
		return node.getValue();
	}

	@Override
	public Object visit(LeqNode node) throws EvaluationException {
		AbstractNode lhs = node.getLhs();
 		AbstractNode rhs = node.getRhs();

		Object left = lhs.accept(this);
		Object right = rhs.accept(this);

		return EvaluationHelper.leq(left, right);
	}

	@Override
	public Object visit(LtNode node) throws EvaluationException {
		AbstractNode lhs = node.getLhs();
		AbstractNode rhs = node.getRhs();

		Object left = lhs.accept(this);
		Object right = rhs.accept(this);

		return EvaluationHelper.lt(left, right);
	}

	@Override
	public Object visit(ModuloNode node) throws EvaluationException {
		AbstractNode lhs = node.getLhs();
		AbstractNode rhs = node.getRhs();

		Boolean left = (Boolean) lhs.accept(this);
		Boolean right = (Boolean) rhs.accept(this);

		return EvaluationHelper.modulo(left, right);
	}

	@Override
	public Object visit(MultiplyNode node) throws EvaluationException {
		AbstractNode lhs = node.getLhs();
		AbstractNode rhs = node.getRhs();

		Boolean left = (Boolean) lhs.accept(this);
		Boolean right = (Boolean) rhs.accept(this);

		return EvaluationHelper.multiply(left, right);
	}

	@Override
	public Object visit(NegativeNode node) throws EvaluationException {
		AbstractNode argument = node.getArgument();

		// Bepaal het getal dat we negatief gaan maken.
		Number number = (Number)argument.accept(this);

		return EvaluationHelper.negative(number);
	}

	@Override
	public Object visit(NeqNode node) throws EvaluationException {
		AbstractNode lhs = node.getLhs();
		AbstractNode rhs = node.getRhs();

		Object left = lhs.accept(this);
		Object right = rhs.accept(this);

		return EvaluationHelper.neq(left, right);
	}

	@Override
	public Object visit(NotNode node) throws EvaluationException {
		AbstractNode argument = node.getArgument();

		// Bepaal de waarde van de boolean.
		Boolean bool = (Boolean)argument.accept(this);

		return EvaluationHelper.not(bool);
	}

	@Override
	public Object visit(NullNode node) throws EvaluationException {
		return null;
	}

	@Override
	public Object visit(OrNode node) throws EvaluationException {
		AbstractNode lhs = node.getLhs();
		AbstractNode rhs = node.getRhs();

		Boolean left = (Boolean) lhs.accept(this);
		Boolean right = (Boolean) rhs.accept(this);

		return EvaluationHelper.or(left, right);
	}

	@Override
	public Object visit(PositiveNode node) throws EvaluationException {
		AbstractNode argument = node.getArgument();
		return argument.accept(this);
	}

	@Override
	public Object visit(PowerNode node) throws EvaluationException {
		AbstractNode base = node.getBase();
		AbstractNode power = node.getPower();

		Number baseValue = (Number)base.accept(this);
		Number powerValue = (Number)power.accept(this);

		return EvaluationHelper.power(baseValue, powerValue);
	}

	@Override
	public Object visit(StatementListNode node) throws EvaluationException {
		// Evalueer alle AbstractNodes en geef het resultaat van de laatste node terug.
		Object result = null;
		for (AbstractNode subNode : node) {
			result = subNode.accept(this);
		}

		return result;
	}

	@Override
	public Object visit(StringNode node) throws EvaluationException {
		return node.getValue();
	}

	@Override
	public Object visit(SubstractNode node) throws EvaluationException {
		AbstractNode lhs = node.getLhs();
		AbstractNode rhs = node.getRhs();

		Number left = (Number) lhs.accept(this);
		Number right = (Number) rhs.accept(this);

		return EvaluationHelper.substract(left, right);
	}

	@Override
	public Object visit(VariableNode node) throws EvaluationException {
		String identifier = node.getIdentifier();
		try {
			return symbolTable.getVariableValue(identifier);
		} catch (NoSuchVariableException e) {
			throw new EvaluationException(e.getMessage(), node);
		}
	}
}
