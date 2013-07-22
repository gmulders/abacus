package org.gertje.abacus.nodevisitors;

import java.util.Stack;

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

public class JavaScriptTranslator extends AbstractNodeVisitor {

	/**
	 * Deze stack gebruiken we om gedeeltelijke vertalingen in op te slaan.
	 */
	protected Stack<String> partStack;
	
	/**
	 * Deze stack gebruiken we om variabelen die we gebruiken binnen een ifNode op te slaan. Dit is nodig omdat 
	 * JavaScript anders met NULL waardes omgaat dan Abacus.
	 */
	protected Stack<String> variableStack;

	/**
	 * Deze stack gebruiken we om afgeleide if-statements in op te slaan, dit is inclusief toekenning aan een variabele.
	 */
	protected Stack<String> declarationStack;

	/**
	 * Constructor.
	 */
	public JavaScriptTranslator() {
		partStack = new Stack<String>();
		variableStack = new Stack<String>();
		declarationStack = new Stack<String>();
	}

	public String translate(AbstractNode node) throws VisitingException {
		
		ExpressionTranslator expressionTranslator = new ExpressionTranslator(node);

		StringBuilder translation = new StringBuilder();
		
		// Wrap de expressie in een closure. Wanneer er geen declaraties zijn en er zijn geen controles op null en de
		// node is een NodeList, dan is deze closure onnodig. Voorlopig laat ik dit wel zo.
		translation.append("(function(){")
			.append(expressionTranslator.getDeclarations())
			.append(expressionTranslator.getNullableCheck())
			.append("return ").append(expressionTranslator.getExpression()).append(";")
		.append("})()");

		return translation.toString();
	}
	
	@Override
	public void visit(AddNode node) throws VisitingException {
		createScriptForSimpleTwoSideNode(node, node.getLhs(), node.getRhs(), "+");
	}

	@Override
	public void visit(AndNode node) throws VisitingException {
		createScriptForSimpleTwoSideNode(node, node.getLhs(), node.getRhs(), "&&");
	}

	@Override
	public void visit(AssignmentNode node) throws VisitingException {
		node.getLhs().accept(this);
		// Pop tussendoor even de assignee van de variabelestack om te voorkomen dat we controleren of de assignee niet
		// null is. Anders zou je zoiets krijgen: a = 3 --> (function(){if(a==null)return null;return a = 3;})()
		variableStack.pop();
		
		node.getRhs().accept(this);
		
		String rhsScript = partStack.pop();
		String lhsScript = partStack.pop();
		
		String script = parenthesize(node.getPrecedence(), node.getLhs().getPrecedence(), lhsScript) 
				+ "="
				+ parenthesize(node.getPrecedence(), node.getRhs().getPrecedence(), rhsScript);
		
		partStack.push(script);
	}

	@Override
	public void visit(BooleanNode node) throws VisitingException {
		partStack.push(node.getValue().booleanValue() ? "true" : "false");
	}

	@Override
	public void visit(DateNode node) throws VisitingException {
		// TODO: data (meervoud van datum) kunnen we nog niet parsen...
		partStack.push("new Date('TODO')");
	}

	@Override
	public void visit(DivideNode node) throws VisitingException {
		createScriptForSimpleTwoSideNode(node, node.getLhs(), node.getRhs(), "/");
	}

	@Override
	public void visit(EqNode node) throws VisitingException {
		createScriptForSimpleTwoSideNode(node, node.getLhs(), node.getRhs(), "==");
	}

	@Override
	public void visit(FactorNode node) throws VisitingException {
		node.getArgument().accept(this);
		partStack.push("(" + partStack.pop() + ")");		
	}

	@Override
	public void visit(FloatNode node) throws VisitingException {
		partStack.push(node.getValue().toString());
	}

	@Override
	public void visit(FunctionNode node) throws VisitingException {
		// Loop eerst over alle parameters heen, om de stack op te bouwen.
		for (AbstractNode childNode : node.getParameters()) {
			childNode.accept(this);
		}
		
		String arguments = "";
		// Haal evenveel elementen van de stack als er zojuist bijgekomen zijn.
		for (int i = 0; i < node.getParameters().size(); i++) {
			arguments = partStack.pop() + (i != 0 ? "," : "") + arguments;
		}
		
		partStack.push(node.getIdentifier() + "(" + arguments + ")");
	}

	@Override
	public void visit(GeqNode node) throws VisitingException {
		createScriptForSimpleTwoSideNode(node, node.getLhs(), node.getRhs(), ">=");
	}

	@Override
	public void visit(GtNode node) throws VisitingException {
		createScriptForSimpleTwoSideNode(node, node.getLhs(), node.getRhs(), ">");
	}

	@Override
	public void visit(IfNode node) throws VisitingException {

		ExpressionTranslator conditionTranslator = new ExpressionTranslator(node.getCondition());
		ExpressionTranslator ifBodyTranslator = new ExpressionTranslator(node.getIfbody());
		ExpressionTranslator elseBodyTranslator = new ExpressionTranslator(node.getElsebody());
		
		// Bouw javascript op die
		StringBuilder ifExpression = new StringBuilder();
		ifExpression
			.append("var _").append(variableStack.size()).append(" = (function(){")
				.append(conditionTranslator.getDeclarations())
				.append(conditionTranslator.getNullableCheck())
				.append("if(").append(conditionTranslator.getExpression()).append("){")
					.append(ifBodyTranslator.getDeclarations())
					.append(ifBodyTranslator.getNullableCheck())
					.append("return ").append(ifBodyTranslator.getExpression()).append(";")
				.append("}else{")
					.append(elseBodyTranslator.getDeclarations())
					.append(elseBodyTranslator.getNullableCheck())
					.append("return ").append(elseBodyTranslator.getExpression()).append(";")
				.append("}")
			.append("})()");
		
		declarationStack.push(ifExpression.toString());
		String variable = "_" + variableStack.size();
		partStack.push(variable);
		variableStack.push(variable);
	}

	@Override
	public void visit(IntegerNode node) throws VisitingException {
		partStack.push(node.getValue().toString());
	}

	@Override
	public void visit(LeqNode node) throws VisitingException {
		createScriptForSimpleTwoSideNode(node, node.getLhs(), node.getRhs(), "<=");
	}

	@Override
	public void visit(LtNode node) throws VisitingException {
		createScriptForSimpleTwoSideNode(node, node.getLhs(), node.getRhs(), "<");
	}

	@Override
	public void visit(ModuloNode node) throws VisitingException {
		createScriptForSimpleTwoSideNode(node, node.getLhs(), node.getRhs(), "%");
	}

	@Override
	public void visit(MultiplyNode node) throws VisitingException {
		createScriptForSimpleTwoSideNode(node, node.getLhs(), node.getRhs(), "*");
	}

	@Override
	public void visit(NegativeNode node) throws VisitingException {
		node.getArgument().accept(this);
		
		partStack.push("-" + partStack.pop());
	}

	@Override
	public void visit(NeqNode node) throws VisitingException {
		createScriptForSimpleTwoSideNode(node, node.getLhs(), node.getRhs(), "!=");
	}

	@Override
	public void visit(NotNode node) throws VisitingException {
		node.getArgument().accept(this);
		
		partStack.push("!" + partStack.pop());
	}

	@Override
	public void visit(NullNode node) throws VisitingException {
		partStack.push("null");
	}

	@Override
	public void visit(OrNode node) throws VisitingException {
		createScriptForSimpleTwoSideNode(node, node.getLhs(), node.getRhs(), "||");
	}

	@Override
	public void visit(PositiveNode node) throws VisitingException {
		node.getArgument().accept(this);
		
		partStack.push(partStack.pop());
	}

	@Override
	public void visit(PowerNode node) throws VisitingException {
		node.getBase().accept(this);
		node.getPower().accept(this);

		String power = partStack.pop();
		String base = partStack.pop();

		partStack.push("Math.pow(" + base + ", " + power + ")");
	}

	@Override
	public void visit(StatementListNode node) throws VisitingException {
		// Wanneer er 1 statement in de lijst zit hoeven we dit statement niet te bundelen in een closure.
		if (node.size() == 1) {
			// Haal het ene element op en zorg dat het op de stack terecht komt.
			node.get(0).accept(this);
			// Doe NIETS: stack.push(stack.pop());
			return;
		}

		// Er zitten meerdere statements in de lijst, bundel ze in een closure.
		String closure = "(function(){";

		// Haal evenveel elementen van de stack als er zojuist bijgekomen zijn.
		for (int i = 0; i < node.size(); i++) {
			// Bewaar het aantal variabelen dat nu op de variabelen stack zit.
			int variableStackSize = variableStack.size();
			// Zorg dat de JavaScript voor dit ene element op de stack komt.
			node.get(i).accept(this);
			// Wanneer dit niet het laatste statement is moeten we alle variabelen die nog op de stack zitten poppen om
			// te voorkomen dat we allerlei checks op null gaan doen.
			if (i < node.size() - 1) {
				for (int j = 0; j < variableStack.size() - variableStackSize; j++) {
					variableStack.pop();
				}
			}
			// Zet voor het laatste statement 'return '.
			closure += (i == node.size() - 1 ? "return ": "") + partStack.pop() + ";";
		}

		closure += "})()";

		partStack.push(closure);
	}

	@Override
	public void visit(StringNode node) throws VisitingException {
		partStack.push("'" + node.getValue() + "'");
	}

	@Override
	public void visit(SubstractNode node) throws VisitingException {
		createScriptForSimpleTwoSideNode(node, node.getLhs(), node.getRhs(), "-");
	}

	@Override
	public void visit(VariableNode node) throws VisitingException {
		// Duw de identifier op de variabelen stack, doe dit zodat we hierover kunnen beschikken in een IfNode. Dit doen
		// we omdat JavaScript anders omgaat met NULL-waardes dan Abacus.
		variableStack.push(node.getIdentifier());
		partStack.push(node.getIdentifier());
	}

	/**
	 * Voegt indien nodig haakjes om het JavaScript stukje.
	 * 
	 * @param parentNodePrecedence getal van volgorde van executie van de parent node.
	 * @param childNodePrecedence getal van volgorde van executie van de child node.
	 * @param part
	 * @return het JavaScript stukje met, indien nodig, haakjes eromheen.
	 */
	protected static String parenthesize(int parentNodePrecedence, int childNodePrecedence, String part) {
		// Wanneer deze node een lagere prio heeft dan de node onder hem moeten we haakjes toevoegen.
		if (parentNodePrecedence < childNodePrecedence) {
			part = "(" + part + ")";
		}
		return part;
	}

	/**
	 * Maakt JavaScript aan voor een node die een lhs en een rhs side heeft.
	 * @param node
	 * @param lhs
	 * @param rhs
	 * @param operator
	 * @throws VisitingException
	 */
	protected void createScriptForSimpleTwoSideNode(AbstractNode node, AbstractNode lhs, AbstractNode rhs, 
			String operator) throws VisitingException {
		lhs.accept(this);
		rhs.accept(this);
		
		String rhsScript = partStack.pop();
		String lhsScript = partStack.pop();
		
		String script = parenthesize(node.getPrecedence(), lhs.getPrecedence(), lhsScript) 
				+ operator 
				+ parenthesize(node.getPrecedence(), rhs.getPrecedence(), rhsScript);
		
		partStack.push(script);
	}
	
	/**
	 * Inner klasse om een expressie te kunnen vertalen. (Let op: met expressie bedoel ik hier een onderdeel van een 
	 * if-statement, zie ook Parser.expression().)
	 */
	private class ExpressionTranslator {
		/** StringBuilder om de declaraties in op te bouwen. */
		StringBuilder declarations;
		/** StringBuilder om de nullable controle in op te bouwen. */
		StringBuilder nullableCheck;
		/** String om de expressie in op te bouwen. */
		String expression;

		/**
		 * Maakt een nieuwe instantie aan. Vertaal de expressie en bouw nodige gedeelten op.
		 * @throws VisitingException 
		 */
		private ExpressionTranslator(AbstractNode node) throws VisitingException {
			declarations = new StringBuilder();
			nullableCheck = new StringBuilder();
			
			// Haal de grootte van de stapel met variabelen op, zodat we straks weten hoeveel we eraf moeten halen.
			int size = variableStack.size();

			// Visit de node.
			node.accept(JavaScriptTranslator.this);
			
			expression = partStack.pop();
			createNullableScript(size);

			// Loop over alle elementen in de ifStatementStack heen, om deze achter elkaar te plakken.
			while(!declarationStack.empty()) {
				declarations.append(declarationStack.pop()).append(";");
			}
		}

		/**
		 * Maakt het gedeelte waar gecontroleerd wordt of alle variabelen wel netjes gezet zijn. Dit doen we omdat 
		 * JavaScript anders met null-waarden omgaat dan Abacus.
		 * 
		 * @param size De grootte van de variabelestack voordat de node waarvoor nullablescript aangemaakt moet worden 
		 * 			ge-visit was.
		 * @return String met het gedeelte wat op null-waarden controleert.
		 */
		protected void createNullableScript(int size) {
			// Wanneer de index tot waar we de stack moeten poppen kleiner of gelijk is aan de stackgrootte zijn we klaar.
			if (size >= variableStack.size()) {
				return;
			}

			// Open de if.
			nullableCheck.append("if(");
			
			// Loop over de variabelen heen die null kunnen zijn.
			while(variableStack.size() > size) {
				// Controleer of de variabele gelijk is aan null.
				nullableCheck.append(variableStack.pop()).append("==null");
				
				// Wanneer dit niet de laatste in de stack is moeten we het 'of' teken erachter aan tonen.
				if (variableStack.size() != size) {
					nullableCheck.append("||");
				}
			}
			
			// Sluit de if, wanneer aan de conditie voldaan wordt geven we null terug.
			nullableCheck.append(")return null;");
		}

		public StringBuilder getDeclarations() {
			return declarations;
		}

		public StringBuilder getNullableCheck() {
			return nullableCheck;
		}

		public String getExpression() {
			return expression;
		}
	}
}
