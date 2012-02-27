package org.gertje.abacus;

import org.gertje.abacus.nodes.AbstractNode;
import org.gertje.abacus.nodes.NodeFactoryInterface;
import org.gertje.abacus.symboltable.SymbolTableInterface;

public class Compiler {

	private SymbolTableInterface symbolTable;

	private NodeFactoryInterface nodeFactory;

	public Compiler(SymbolTableInterface symbolTable, NodeFactoryInterface nodeFactory) {
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
		Lexer lexer = new Lexer(expression);

		// dan maken we een parser aan die we de lexer meegeven,
		Parser parser = new Parser(lexer, nodeFactory);

		// daarna laten we de parser een AST opbouwen,
		AbstractNode tree = parser.buildAST();

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


