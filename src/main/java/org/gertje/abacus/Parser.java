package org.gertje.abacus;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.gertje.abacus.Token.TokenType;
import org.gertje.abacus.nodes.AbstractNode;
import org.gertje.abacus.nodes.AssignmentNode;
import org.gertje.abacus.nodes.NodeFactoryInterface;
import org.gertje.abacus.nodes.StatementListNode;

class Parser {

	// Constanten voor mogelijke waarden van booleans.
	private static final String BOOLEAN_TRUE = "true";
	private static final String BOOLEAN_FALSE = "false";
	private static final String NULL = "null";

	/**
	 * De lexer.
	 */
	private Lexer lex;
	
	/**
	 * De fabriek voor nodes.
	 */
	private NodeFactoryInterface nodeFactory;

	public Parser(Lexer lex, NodeFactoryInterface nodeFactory) {
		this.lex = lex;
		this.nodeFactory = nodeFactory;
	}

	/**
	 * Bouwt een AST op van de expressie.
	 * @throws CompilerException
	 */
	public StatementListNode buildAST() throws CompilerException {
		return statementList(lex.getNextToken());
	}

	private StatementListNode statementList(Token nextToken) throws CompilerException {
		StatementListNode list = nodeFactory.createStatementListNode(nextToken);
		// Zolang het token niet het einde van de input aangeeft maken we expressies aan.
		while (nextToken.getType() != TokenType.END_OF_INPUT) {
			list.add(statement(nextToken));
			nextToken = lex.getNextToken();
		}

		return list;
	}

	private AbstractNode statement(Token nextToken) throws CompilerException {
		AbstractNode statement = null;

		// Wanneer het volgende token een ASSIGNMENT is, maken we een AssignmentNode aan.
		if (lex.peekToken().getType() == TokenType.ASSIGNMENT) {
			statement = assignment(nextToken);
	
		// Anders verwachten we een ifElse.
		} else {
			statement = expression(nextToken);
		}

		// We verwachten het 'end of expression' of het 'end of input' token.
		nextToken = lex.peekToken();
		if (nextToken.getType() != TokenType.END_OF_EXPRESSION 
				&& nextToken.getType() != TokenType.END_OF_INPUT) {
			throw new ParserException("Unexpected token '" + nextToken.getType().toString() + "'.", nextToken);
		}

		// Wanneer het token niet het 'end of input' token was, halen we het token van de stack.
		if (nextToken.getType() != TokenType.END_OF_INPUT) {
			lex.getNextToken();
		}

		return statement;
	}

	private AssignmentNode assignment(Token nextToken) throws CompilerException {
		// Aan de linker kant verwachten we een variabele.
		if (!determineIsVariable(nextToken)) {
			throw new ParserException("Expected variable on left hand side of assignment.", nextToken);
		}
		
		// Verwacht een '=' teken. Het kan niet anders dan dat we deze vinden, want anders zaten we niet in deze 
		// methode. Controleer toch even...
		if (lex.getNextToken().getType() != TokenType.ASSIGNMENT) {
			throw new ParserException("Expected '='.", nextToken);
		}

		return nodeFactory.createAssignmentNode(
				nodeFactory.createVariableNode(nextToken.getValue(), nextToken),
				conditional(lex.getNextToken()),
				nextToken);
	}

	private AbstractNode expression(Token nextToken) throws CompilerException {
		return conditional(nextToken);
	}

	private AbstractNode conditional(Token nextToken) throws CompilerException {
		// Geef de 
		AbstractNode condition = booleanOp(nextToken);

		// Spiek wat het volgende token is.
		nextToken = lex.peekToken();

		// Wanneer het volgende token een IF token is bepalen we ook de if tak.
		if (nextToken.getType() == TokenType.IF) {
			// Haal het gespiekte token van de stack.
			Token ifToken = lex.getNextToken();
			// Het volgende token kan ook een if-else zijn.
			AbstractNode ifbody = expression(lex.getNextToken());

			// Haal het volgende token op.
			nextToken = lex.getNextToken();
			// Het token moet een else token zijn.
			if (nextToken.getType() != TokenType.ELSE) {
				throw new ParserException("Expected ELSE token (:).", nextToken);
			}
			// De else-body kan ook een if-else zijn.
			AbstractNode elsebody = expression(lex.getNextToken());

			// Maak een nieuwe ASTNode aan met het juiste type en de juiste operanden.
			AbstractNode result = nodeFactory.createIfNode(condition, ifbody, elsebody, ifToken);
			condition = result;
		}
		// Geef het uiteindelijke resultaat terug.
		return condition;
	}

	/**
	 * Voert de operatie uit voor boolean operators of geef hem door aan sterkere operators.
	 * @throws CompilerException
	 */
	private AbstractNode booleanOp(Token nextToken) throws CompilerException {
		AbstractNode lhs = comparison(nextToken);

		// Spiek wat het volgende token is.
		nextToken = lex.peekToken();

		// Zolang het volgende token een OR of een AND operatie is voeren we deze uit.
		while (nextToken.getType() == TokenType.BOOLEAN_OR || nextToken.getType() == TokenType.BOOLEAN_AND) {
			// Haal het gespiekte token van de stack.
			Token orOrAndToken = lex.getNextToken();
			// Bepaal de rechter AST van de operatie.
			AbstractNode rhs = comparison(lex.getNextToken());

			// Maak voor elke mogelijke boolean operatie een andere ASTNode aan.
			if(nextToken.getType() == TokenType.BOOLEAN_AND) {
				lhs = nodeFactory.createAndNode(lhs, rhs, orOrAndToken);
			} else {
				lhs = nodeFactory.createOrNode(lhs, rhs, orOrAndToken);
			}

			// Spiek naar het volgende token.
			nextToken = lex.peekToken();
		}
		// Geef het resultaat (de lhs) terug.
		return lhs;
	}

	/**
	 * Voert de operatie uit voor conditie operators of geef hem door aan sterkere operators.
	 * @throws CompilerException
	 */
	private AbstractNode comparison(Token nextToken) throws CompilerException {
		AbstractNode lhs = addition(nextToken);

		// Spiek wat het volgende token is.
		nextToken = lex.peekToken();

		// Zolang het volgende token een vergelijking is < <= == => > of != voeren we die operatie uit.
		while (nextToken.getType() == TokenType.LT || nextToken.getType() == TokenType.LEQ 
				|| nextToken.getType() == TokenType.EQ
				|| nextToken.getType() == TokenType.GEQ || nextToken.getType() == TokenType.GT
				|| nextToken.getType() == TokenType.NEQ
				) {
			// Haal het gespiekte token van de stack.
			Token comparisonToken = lex.getNextToken();
			// Bepaal de rechter AST van de operatie.
			AbstractNode rhs = addition(lex.getNextToken());

			// Maak afhankelijk van de vergelijking de juiste soort ASTNode aan.
			if (nextToken.getType() == TokenType.LT) {
				lhs = nodeFactory.createLtNode(lhs, rhs, comparisonToken);
			} else if (nextToken.getType() == TokenType.LEQ) {
				lhs = nodeFactory.createLeqNode(lhs, rhs, comparisonToken);
			} else if (nextToken.getType() == TokenType.EQ) {
				lhs = nodeFactory.createEqNode(lhs, rhs, comparisonToken);
			} else if (nextToken.getType() == TokenType.GEQ) {
				lhs = nodeFactory.createGeqNode(lhs, rhs, comparisonToken);
			} else if (nextToken.getType() == TokenType.GT) {
				lhs = nodeFactory.createGtNode(lhs, rhs, comparisonToken);
			} else {
				lhs = nodeFactory.createNeqNode(lhs, rhs, comparisonToken);
			}

			// Spiek naar het volgende token.
			nextToken = lex.peekToken();
		}
		
		// Geef de lhs terug.
		return lhs;
	}

	/**
	 * Voert de operatie uit voor de plus en min operators of geef hem door aan sterkere operators.
	 * @throws CompilerException 
	 */
	private AbstractNode addition(Token nextToken) throws CompilerException {
		AbstractNode lhs = term(nextToken);

		// Spiek wat het volgende token is.
		nextToken = lex.peekToken();
		
		// Zolang het volgende token een sommatie is voeren we die operatie uit.
		while (nextToken.getType() == TokenType.PLUS || nextToken.getType() == TokenType.MINUS) {
			// Haal het gespiekte token van de stack.
			Token additionToken = lex.getNextToken();
			// Bepaal de rechter AST van de operatie.
			AbstractNode rhs = term(lex.getNextToken());

			// Maak afhankelijk van de operatie de juiste soort ASTNode aan.
			if (nextToken.getType() == TokenType.PLUS) {
				lhs = nodeFactory.createAddNode(lhs, rhs, additionToken);
			} else {
				lhs = nodeFactory.createSubstractNode(lhs, rhs, additionToken);
			}

			// Spiek naar het volgende token.
			nextToken = lex.peekToken();
		}
		// Geef de lhs terug.
		return lhs;
	}

	/**
	 * Voert de operatie uit voor de vermenigvuldig operators of geef hem door aan sterkere operators.
	 * @throws CompilerException
	 */
	private AbstractNode term(Token nextToken) throws CompilerException {
		AbstractNode lhs = power(nextToken);

		// Spiek wat het volgende token is.
		nextToken = lex.peekToken();

		// Zolang het volgende token een sommatie is voeren we die operatie uit.
		while (nextToken.getType() == TokenType.MULTIPLY || nextToken.getType() == TokenType.DIVIDE 
				|| nextToken.getType() == TokenType.PERCENT) {
			// Haal het gespiekte token van de stack.
			Token termToken = lex.getNextToken();
			// Bepaal de rechter AST van de operatie.
			AbstractNode rhs = power(lex.getNextToken());

			// Maak afhankelijk van de operatie de juiste soort ASTNode aan.
			if (nextToken.getType() == TokenType.MULTIPLY) {
				lhs = nodeFactory.createMultiplyNode(lhs, rhs, termToken);
			} else if (nextToken.getType() == TokenType.DIVIDE) {
				lhs = nodeFactory.createDivideNode(lhs, rhs, termToken);
			} else {
				lhs = nodeFactory.createModuloNode(lhs, rhs, termToken);
			}
			
			// Spiek naar het volgende token.
			nextToken = lex.peekToken();
		}
		// Geef de lhs terug.
		return lhs;
	}

	/**
	 * Voert de operatie uit voor de macht operator of geef hem door aan sterkere operators.
	 * @throws CompilerException
	 */
	private AbstractNode power(Token nextToken) throws CompilerException {
		AbstractNode lhs = unary(nextToken);

		// Spiek wat het volgende token is.
		nextToken = lex.peekToken();

		// Als de volgende token een macht operator is voeren we die operatie uit.
		if (nextToken.getType() == TokenType.POWER) {
			// Haal het gespiekte token van de stack.
			Token powerToken = lex.getNextToken();
			// Bepaal de rechter AST van de operatie.
			AbstractNode rhs = power(lex.getNextToken());
			lhs = nodeFactory.createPowerNode(lhs, rhs, powerToken);
		}
		// Geef de lhs terug.
		return lhs;
	}

	/**
	 * Geeft een Node terug specifiek voor de unary methode, of geeft de token door aan een sterkere operator.
	 * @throws CompilerException
	 */
	private AbstractNode unary(Token nextToken) throws CompilerException {
		// Maak afhankelijk van het type van de token de juiste ASTNode aan.
		if (nextToken.getType() == TokenType.PLUS) {
			return nodeFactory.createPositiveNode(factor(lex.getNextToken()), nextToken);
		} else if (nextToken.getType() == TokenType.MINUS) {
			return nodeFactory.createNegativeNode(factor(lex.getNextToken()), nextToken);
		} else if (nextToken.getType() == TokenType.NOT) {
			return nodeFactory.createNotNode(factor(lex.getNextToken()), nextToken);
		}
		// Wanneer we hier komen geven we een factor node terug.
		return factor(nextToken);
	}

	/**
	 * Geeft een Node terug specifiek voor een getal, een factor, een variabele of een functie.
	 * @throws CompilerException 
	 */
	private AbstractNode factor(Token nextToken) throws CompilerException {
		// Wanneer het token een decimaal getal is geven we een FloatNode terug.
		if (nextToken.getType() == TokenType.FLOAT) {
			BigDecimal number;
			// Probeer de string naar een BigDecimal te casten.
			try {
				number = new BigDecimal(nextToken.getValue());
			} catch (NumberFormatException nfe) {
				throw new ParserException("Illegal number format; " + nfe.getMessage(), nextToken);
			}
			return nodeFactory.createFloatNode(number, nextToken);

		// Wanneer het token een geheel getal is geven we een IntegerNode terug.
		} else if (nextToken.getType() == TokenType.INTEGER) {
				BigInteger number;
				// Probeer de string naar een BigInteger te casten.
				try {
					number = new BigInteger(nextToken.getValue());
				} catch (NumberFormatException nfe) {
					throw new ParserException("Illegal number format; " + nfe.getMessage(), nextToken);
				}
				return nodeFactory.createIntegerNode(number, nextToken);


		// Wanneer het token een string is geven we een StringNode terug.
		} else if (nextToken.getType() == TokenType.STRING) {
			return nodeFactory.createStringNode(nextToken.getValue(), nextToken);

		// Wanneer het token een linker haakje is geven we een FactorNode terug.
		} else if (nextToken.getType() == TokenType.LEFT_PARENTHESIS) {
			AbstractNode factorNode = nodeFactory.createFactorNode(expression(lex.getNextToken()), nextToken);
			// We verwachten een rechter haakje.
			Token token = lex.getNextToken();
			if (token.getType() != TokenType.RIGHT_PARENTHESIS) {
				throw new ParserException("Expected ')'", token);
			}
			return factorNode;

		// Wanneer het token een identifier is geven we afhankelijk van de waarde en wat er achter aan komt een van de
		// volgende Nodes terug:
		// - BooleanNode
		// - FunctionNode
		// - VariabeleNode.
		} else if (nextToken.getType() == TokenType.IDENTIFIER) {
			// Als het token een boolean is geven we een BooleanNode terug.
			if (determineIsBoolean(nextToken)) {
				return nodeFactory.createBooleanNode(Boolean.valueOf(BOOLEAN_TRUE.equals(nextToken.getValue())),
						nextToken);
			}
			
			// Als het token de waarde 'null' bevat geven we een NullNode terug.
			if (NULL.equals(nextToken.getValue())) {
				return nodeFactory.createNullNode(nextToken);
			}

			// Als het token een variabele is geven we een VariableNode terug.
			if (determineIsVariable(nextToken)) {
				return nodeFactory.createVariableNode(nextToken.getValue(), nextToken);
			}

			// Als het token een function is geven we een FunctionNode terug.
			if (determineIsFunction(nextToken)) {
				// We moeten nu een lijst opbouwen met parameters.
				List<AbstractNode> params = buildParameters();

				return nodeFactory.createFunctionNode(nextToken.getValue(), params, nextToken);
			}
		}
		// Wanneer we hier komen is er iets fout, geef een fout.
		throw new ParserException("Expected expression, found token: type: '" + nextToken.getType().toString()
				+ "', value: '" + nextToken.getValue() + "'.", nextToken);
	}

	private List<AbstractNode> buildParameters() throws CompilerException {
		// Haal het linkerhaakje van de stack.
		lex.getNextToken();
		// Maak een variabele met de parameters aan.
		List<AbstractNode> params = new ArrayList<AbstractNode>();
		// Wanneer het volgende token een rechterhaakje is heeft de functie geen parameters.
		if (lex.peekToken().getType() == TokenType.RIGHT_PARENTHESIS) {
			// Haal het token van de stack en geef de lege array terug.
			lex.getNextToken();
			return params;
		}

		// Loop net zolang door een loop totdat we er zelf uitbreken.
		while (true) {
			// We verwachten een expressie.
			params.add(expression(lex.getNextToken()));

			// Het volgende token moet een , zijn of een rechterhaakje.
			Token nextToken = lex.getNextToken();
			// Wanneer het volgende token een rechterhaakje is breken we de loop af.
			if (nextToken.getType() == TokenType.RIGHT_PARENTHESIS) {
				break;

			// Wanneer het volgende token geen komma is gooien we een exceptie.
			} else if (nextToken.getType() != TokenType.COMMA) {
				throw new ParserException("Expected comma.", nextToken);
			}

			// Als we hier komen was het token een komma en moeten we nog een argument parsen.
		}

		return params;
	}
	
	/**
	 * Bepaalt of het token een boolean is, dit is het geval wanneer het token:
	 * - een identifier is
	 * - EN gelijk is aan 'true' OF gelijk is aan 'false'.
	 * @param token
	 * @return <code>true</code> wanneer het token een boolean is, anders <code>false</code>.
	 */
	private boolean determineIsBoolean(Token token) {
		return token.getType() == TokenType.IDENTIFIER
				&& (BOOLEAN_TRUE.equals(token.getValue()) || BOOLEAN_FALSE.equals(token.getValue()));
	}

	/**
	 * Bepaalt of het token een functie is, dit is het geval wanneer het token:
	 * - een identifier is
	 * - EN gevolgd wordt door een linkerhaakje.
	 * @param token
	 * @return <code>true</code> wanneer het token een functie is, anders <code>false</code>.
	 * @throws LexerException 
	 */
	private boolean determineIsFunction(Token token) throws LexerException {
		return token.getType() == TokenType.IDENTIFIER
				&& lex.peekToken().getType() == TokenType.LEFT_PARENTHESIS;
	}
	
	/**
	 * Bepaalt of het meegegeven token een variabele is. Dit is het geval wanneer het token:
	 * - een identifier
	 * - EN het is geen boolean
	 * - EN het is geen functie.
	 * @param nextToken
	 * @return <code>true</code> wanneer het token een variabele is, anders <code>false</code>.
	 * @throws LexerException 
	 */
	private boolean determineIsVariable(Token token) throws LexerException {
		return token.getType() == TokenType.IDENTIFIER
				&& !determineIsBoolean(token)
				&& !determineIsFunction(token);
	}
}


