package org.gertje.abacus.nodevisitors;

import org.gertje.abacus.*;
import org.gertje.abacus.nodes.*;
import org.gertje.abacus.symboltable.SymbolTable;

import java.math.BigDecimal;
import java.math.BigInteger;

public class Simplifier extends AbstractNodeVisitor<AbstractNode, SimplificationException> {

	/**
	 * De symboltable met de variabelen en de functies.
	 */
	private SymbolTable symbolTable;

	/**
	 * De nodefactory die we gebruiken om nodes aan te maken.
	 */
	private NodeFactory nodeFactory;

	/**
	 * De evaluator die we gebruiken om nodes te vereenvoudigen waar mogelijk.
	 */
	private Evaluator evaluator;

	/**
	 * Constructor.
	 */
	public Simplifier(SymbolTable symbolTable, NodeFactory nodeFactory) {
		this.symbolTable = symbolTable;
		this.nodeFactory = nodeFactory;

		// Maak een evaluator aan om de nodes te vereenvoudigen.
		evaluator = new Evaluator(symbolTable);
	}

	public AbstractNode simplify(AbstractNode node) throws SimplificationException {
		return node.accept(this);
	}

	@Override
	public AbstractNode visit(AddNode node) throws SimplificationException {
		AbstractNode lhs = node.getLhs();
		AbstractNode rhs = node.getRhs();

		// Vereenvoudig de nodes indien mogelijk.
		lhs = lhs.accept(this); node.setLhs(lhs);
		rhs = rhs.accept(this); node.setRhs(rhs);

		// Wanneer een van beide zijden niet constant is kunnen we de node niet verder vereenvoudigen. Geef de huidige
		// instantie terug.
		if (!lhs.getIsConstant() || !rhs.getIsConstant()) {
			return node;
		}

		// Wanneer we hier komen zijn beide zijden constant. Vereenvoudig de node.
		Object result;
		try {
			result = evaluator.evaluate(node);
		} catch (EvaluationException e) {
			throw new SimplificationException(e.getMessage(), node);
		}

		if (node.getType().equals(BigDecimal.class)) {
			return nodeFactory.createFloatNode((BigDecimal) result, node.getToken());
		} else if (node.getType().equals(BigInteger.class)) {
			return nodeFactory.createIntegerNode((BigInteger) result, node.getToken());
		} else {
			return nodeFactory.createStringNode((String) result, node.getToken());
		}
	}

	@Override
	public AbstractNode visit(AndNode node) throws SimplificationException {
		AbstractNode lhs = node.getLhs();
		AbstractNode rhs = node.getRhs();

		// Vereenvoudig de nodes indien mogelijk.
		lhs = lhs.accept(this); node.setLhs(lhs);
		rhs = rhs.accept(this); node.setRhs(rhs);

		try {
			// Wanneer beide zijden constant zijn kunnen we de node vereenvoudigen.
			if (lhs.getIsConstant() && rhs.getIsConstant()) {
				return nodeFactory.createBooleanNode((Boolean)evaluator.evaluate(node), node.getToken());
			}

			// Wanneer slechts de linker zijde of de rechter zijde constant is en deze naar 'false' evalueert, evalueert
			// de hele expressie naar false en kunnen we de node vereenvoudigen.
			if (lhs.getIsConstant() && !((Boolean)evaluator.evaluate(lhs)).booleanValue()
					|| rhs.getIsConstant() && !((Boolean)evaluator.evaluate(rhs)).booleanValue()) {
				return nodeFactory.createBooleanNode(Boolean.FALSE, node.getToken());
			}

			// Wanneer slechts de linker zijde of de rechter zijde constant is en deze naar 'true' evalueert, evalueert
			// de huidige expressie naar de niet constante expressie.
			if (lhs.getIsConstant() && ((Boolean)evaluator.evaluate(lhs)).booleanValue()) {
				return rhs;
			} else if (rhs.getIsConstant() && ((Boolean)evaluator.evaluate(rhs)).booleanValue()) {
				return lhs;
			}
		} catch (EvaluationException e) {
			throw new SimplificationException(e.getMessage(), node);
		}

		return node;
	}

	@Override
	public AbstractNode visit(AssignmentNode node) throws SimplificationException {
		AbstractNode lhs = node.getLhs();
		AbstractNode rhs = node.getRhs();

		// Vereenvoudig de nodes indien mogelijk.
		lhs = lhs.accept(this); node.setLhs(lhs);
		rhs = rhs.accept(this); node.setRhs(rhs);

		// We kunnen deze node niet verder vereenvoudigen, geef de node terug.
		return node;
	}

	@Override
	public AbstractNode visit(BooleanNode node) throws SimplificationException {
		return node;
	}

	@Override
	public AbstractNode visit(DateNode node) throws SimplificationException {
		return node;
	}

	@Override
	public AbstractNode visit(DivideNode node) throws SimplificationException {
		return null;
	}

	@Override
	public AbstractNode visit(EqNode node) throws SimplificationException {
		return null;
	}

	@Override
	public AbstractNode visit(FactorNode node) throws SimplificationException {
		return null;
	}

	@Override
	public AbstractNode visit(FloatNode node) throws SimplificationException {
		return null;
	}

	@Override
	public AbstractNode visit(FunctionNode node) throws SimplificationException {
		return null;
	}

	@Override
	public AbstractNode visit(GeqNode node) throws SimplificationException {
		return null;
	}

	@Override
	public AbstractNode visit(GtNode node) throws SimplificationException {
		return null;
	}

	@Override
	public AbstractNode visit(IfNode node) throws SimplificationException {
		return null;
	}

	@Override
	public AbstractNode visit(IntegerNode node) throws SimplificationException {
		return null;
	}

	@Override
	public AbstractNode visit(LeqNode node) throws SimplificationException {
		return null;
	}

	@Override
	public AbstractNode visit(LtNode node) throws SimplificationException {
		return null;
	}

	@Override
	public AbstractNode visit(ModuloNode node) throws SimplificationException {
		return null;
	}

	@Override
	public AbstractNode visit(MultiplyNode node) throws SimplificationException {
		return null;
	}

	@Override
	public AbstractNode visit(NegativeNode node) throws SimplificationException {
		return null;
	}

	@Override
	public AbstractNode visit(NeqNode node) throws SimplificationException {
		return null;
	}

	@Override
	public AbstractNode visit(NotNode node) throws SimplificationException {
		return null;
	}

	@Override
	public AbstractNode visit(NullNode node) throws SimplificationException {
		return null;
	}

	@Override
	public AbstractNode visit(OrNode node) throws SimplificationException {
		return null;
	}

	@Override
	public AbstractNode visit(PositiveNode node) throws SimplificationException {
		return null;
	}

	@Override
	public AbstractNode visit(PowerNode node) throws SimplificationException {
		return null;
	}

	@Override
	public AbstractNode visit(StatementListNode node) throws SimplificationException {
		return null;
	}

	@Override
	public AbstractNode visit(StringNode node) throws SimplificationException {
		return null;
	}

	@Override
	public AbstractNode visit(SubstractNode node) throws SimplificationException {
		return null;
	}

	@Override
	public AbstractNode visit(VariableNode node) throws SimplificationException {
		return null;
	}
}
