package org.gertje.abacus.translator;

import jdk.internal.org.objectweb.asm.ClassReader;
import jdk.internal.org.objectweb.asm.util.CheckClassAdapter;
import org.gertje.abacus.AbstractTestCaseRunner;
import org.gertje.abacus.context.AbacusContext;
import org.gertje.abacus.context.SimpleAbacusContext;
import org.gertje.abacus.exception.CompilerException;
import org.gertje.abacus.lexer.AbacusLexer;
import org.gertje.abacus.lexer.Lexer;
import org.gertje.abacus.nodes.AbacusNodeFactory;
import org.gertje.abacus.nodes.Node;
import org.gertje.abacus.nodes.NodeFactory;
import org.gertje.abacus.nodes.RootNode;
import org.gertje.abacus.nodevisitors.SemanticsChecker;
import org.gertje.abacus.nodevisitors.Simplifier;
import org.gertje.abacus.parser.Parser;
import org.gertje.abacus.symboltable.SymbolTable;
import org.gertje.abacus.translator.java.nodevisitors.ClassTranslator;
import org.gertje.abacus.translator.java.runtime.AbacusWrapper;
import org.junit.Assert;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.security.SecureClassLoader;

/**
 * Runs the test case for the JavaScript translator.
 */
public class ClassTranslatorTestCaseRunner extends AbstractTestCaseRunner {

	private static final String CLASS_NAME = "org.gertje.asmtest.Blater";

	@Override
	public void runTestCase() {
		// Maak een nieuwe symboltable en vul deze met wat waarden.
		SymbolTable sym = createSymbolTable();

		NodeFactory nodeFactory = new AbacusNodeFactory();

		Lexer lexer = new AbacusLexer(abacusTestCase.expression);
		Parser parser = new Parser(lexer, nodeFactory);

		RootNode rootNode;
		try {
			rootNode = parser.parse();
		} catch (CompilerException e) {
			if (!abacusTestCase.failsWithException) {
				Assert.fail(createMessage("Unexpected exception.", e));
			}
			return;
		}

		AbacusContext abacusContext = new SimpleAbacusContext(sym);
		SemanticsChecker semanticsChecker = new SemanticsChecker(sym);
		Simplifier simplifier = new Simplifier(abacusContext, nodeFactory);
		ClassTranslator translator = new ClassTranslator(abacusContext);

		byte[] byteCode;
		try {
			semanticsChecker.check(rootNode);

			if (!checkReturnType(rootNode.getType())) {
				Assert.fail(createMessage("Incorrect return type."));
			}

			Node node = simplifier.simplify(rootNode);

			byteCode = translator.translate(node, TestExpressionWrapper.class, CLASS_NAME);

//			checkClass(byteCode);
		} catch (Exception e) {
			if (!abacusTestCase.failsWithException) {
				Assert.fail(createMessage("Unexpected exception.", e));
			}
			return;
		}

		if (abacusTestCase.failsWithException) {
			Assert.fail(createMessage("Expected exception, but none was thrown."));
		}

		if (!checkReturnType(rootNode.getType())) {
			Assert.fail(createMessage("Incorrect return type."));
		}

		AbacusWrapper expressionWrapper;

		Object returnValue;
		try {
			Class<?> clazz = loadClass(CLASS_NAME, byteCode);
			expressionWrapper = (AbacusWrapper) clazz.newInstance();
			expressionWrapper.setAbacusContext(abacusContext);
			returnValue = expressionWrapper.f();
		} catch (Exception e) {
			Assert.fail(createMessage("Unexpected exception.", e));
			return;
		}

		if (!checkReturnValue(returnValue)) {
			Assert.fail(createMessage("Incorrect return value."));
		}

		if (!checkSymbolTable(sym)) {
			Assert.fail(createMessage("Incorrect symbol table."));
		}
	}

	private Class loadClass(String className, final byte[] byteCode) throws ClassNotFoundException {
		return new SecureClassLoader() {
			@Override
			protected Class<?> findClass(String name) throws ClassNotFoundException {
				return super.defineClass(name, byteCode, 0, byteCode.length);
			}
		}.loadClass(className);
	}

	/**
	 * Checks the generated class for errors. If one is found an exception is thrown.
	 * @param clazz A byte representation of the Class to be checked.
	 */
	private void checkClass(byte[] clazz) {
		ClassReader reader = new ClassReader(clazz);

		StringWriter writer = new StringWriter();
		PrintWriter printWriter = new PrintWriter(writer);

		Exception error = null;
		try {
			CheckClassAdapter.verify(reader, this.getClass().getClassLoader(), false, printWriter);
		} catch (Exception e) {
			error = e;
		}

		String contents = writer.toString();

		if (error != null || contents.length() > 0) {
			throw new RuntimeException("Generation error\nDump for " + reader.getClassName() + "\n" + writer.toString(), error);
		}
	}
}
