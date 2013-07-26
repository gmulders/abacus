package org.gertje.abacus;

import org.gertje.abacus.nodes.AbstractNode;
import org.gertje.abacus.nodes.NodeFactory;
import org.gertje.abacus.symboltable.SymbolTable;

public class Compiler {

	private SymbolTable symbolTable;

	private NodeFactory nodeFactory;

	public Compiler(SymbolTable symbolTable, NodeFactory nodeFactory) {
		this.symbolTable = symbolTable;
		this.nodeFactory = nodeFactory;
	}

	/**
	 * Compileert de expressie.
	 *
	 * @return een AbstractSyntaxTree.
	 * @throws CompilerException
	 */
	public AbstractNode compile(String expression, Class<?> allowedReturnType) throws CompilerException {
		// Compileren bestaat uit een aantal stappen;
		// eerst maken we een lexer aan,
		Lexer lexer = new AbacusLexer(expression);

		// dan maken we een parser aan die we de lexer meegeven,
		Parser parser = new Parser(lexer, nodeFactory);

		// daarna laten we de parser een AST opbouwen,
		AbstractNode tree = parser.parse();

		// dan laten we de boom zichzelf analyseren, hierbij vereenvoudigt de boom zichzelf en controleert de boom of
		// de types kloppen, hierbij heeft de boom de symbolTable nodig.
		tree = tree.analyse(symbolTable);

		// Wanneer een returnType is meegegeven aan de methode moeten de returnTypes hetzelfde zijn.
		if (allowedReturnType != null && !allowedReturnType.equals(tree.getType())) {
			throw new CompilerException("The expression returns a " + tree.getType().toString()
					+ ". (expected " + allowedReturnType.toString() + ")", 0, 0);
		}

		// Geef als laatste de boom terug.
		return tree;
	}
}


