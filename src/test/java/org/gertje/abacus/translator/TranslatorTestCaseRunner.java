package org.gertje.abacus.translator;

import org.gertje.abacus.AbstractTestCaseRunner;
import org.gertje.abacus.context.AbacusContext;
import org.gertje.abacus.context.SimpleAbacusContext;
import org.gertje.abacus.exception.CompilerException;
import org.gertje.abacus.lexer.AbacusLexer;
import org.gertje.abacus.lexer.Lexer;
import org.gertje.abacus.nodes.AbacusNodeFactory;
import org.gertje.abacus.nodes.Node;
import org.gertje.abacus.nodes.NodeFactory;
import org.gertje.abacus.nodevisitors.SemanticsChecker;
import org.gertje.abacus.nodevisitors.Simplifier;
import org.gertje.abacus.nodevisitors.VisitingException;
import org.gertje.abacus.parser.Parser;
import org.gertje.abacus.symboltable.SymbolTable;
import org.gertje.abacus.translator.java.nodevisitors.Translator;
import org.junit.Assert;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.FileObject;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.SimpleJavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.net.URI;
import java.security.SecureClassLoader;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Runs the test case for the JavaScript translator.
 */
public class TranslatorTestCaseRunner extends AbstractTestCaseRunner {


	@Override
	public void runTestCase() {
		// Maak een nieuwe symboltable en vul deze met wat waarden.
		SymbolTable sym = createSymbolTable();

		NodeFactory nodeFactory = new AbacusNodeFactory();

		Lexer lexer = new AbacusLexer(abacusTestCase.expression);
		Parser parser = new Parser(lexer, nodeFactory);

		Node tree;
		try {
			tree = parser.parse();
		} catch (CompilerException e) {
			if (!abacusTestCase.failsWithException) {
				Assert.fail(createMessage("Unexpected exception.", e));
			}
			return;
		}

		AbacusContext abacusContext = new SimpleAbacusContext(sym);
		SemanticsChecker semanticsChecker = new SemanticsChecker(sym);
		Simplifier simplifier = new Simplifier(abacusContext, nodeFactory);
		Translator translator = new Translator(abacusContext);

		String expression;
		try {
			semanticsChecker.check(tree);

			if (!checkReturnType(tree.getType())) {
				Assert.fail(createMessage("Incorrect return type."));
			}

			tree = simplifier.simplify(tree);

			expression = translator.translate(tree);
		} catch (VisitingException e) {
			if (!abacusTestCase.failsWithException) {
				Assert.fail(createMessage("Unexpected exception.", e));
			}
			return;
		}

		if (abacusTestCase.failsWithException) {
			Assert.fail(createMessage("Expected exception, but none was thrown."));
		}

		if (!checkReturnType(tree.getType())) {
			Assert.fail(createMessage("Incorrect return type."));
		}

		// System.out.println(expression);

		String javaSource = createJavaSource(expression);

		ExpressionWrapper expressionWrapper;

		Object returnValue;
		try {
			Class<?> clazz = compile("org.gertje.abacus.translator.Z", javaSource);
			expressionWrapper = (ExpressionWrapper)clazz.newInstance();
			expressionWrapper.setSymbolTable(sym);
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

	private String createJavaSource(String expression) {
		return "package org.gertje.abacus.translator;\n" +
					"import org.gertje.abacus.translator.java.runtime.*;\n" +
					"import org.gertje.abacus.util.*;\n" +
					"import org.gertje.abacus.types.*;\n" +
					"\n" +
					"\n" +
					"public class Z extends ExpressionWrapper<Object> {\n" +
					"\n" +
					"\tpublic Object f() throws Exception {\n" +
					"\t\treturn " + expression + ";\n" +
					"\t}\n\n" +
					"}\n"
				;
	}

	/**
	 * Compiles the given Java source code with the given class name.
	 * @param className The class name of the class in the source.
	 * @param javaSource The Java source code.
	 * @return The compiled and loaded class contained within the source code.
	 * @throws ClassNotFoundException
	 */
	private Class<?> compile(String className, final String javaSource) throws ClassNotFoundException {

		// Create a map that holds byte array output streams with byte code that can be referenced by the class name.
		final Map<String, ByteArrayOutputStream> classByteArrayOutputStreams = new HashMap<>();

		// Create the Java compiler.
		JavaCompiler javaCompiler = ToolProvider.getSystemJavaCompiler();

		DiagnosticCollector<JavaFileObject> diagnosticCollector = new DiagnosticCollector<>();

		// Get the StandardJavaFileManager from the Java compiler.
		StandardJavaFileManager standardJavaFileManager = javaCompiler.getStandardFileManager(diagnosticCollector, null,
				null);

		// Create a writer that the compiler can use to write additional output (that does not end up in the diagnostics
		// collector) to.
		StringWriter additionalOutputWriter = new StringWriter();

		// Create a forwarding file manager.
		JavaFileManager javaFileManager = new ForwardingJavaFileManager<JavaFileManager>(standardJavaFileManager) {

			@Override
			public JavaFileObject getJavaFileForOutput(Location location, String className, JavaFileObject.Kind kind,
			                                           FileObject sibling) throws IOException {

				final ByteArrayOutputStream buffer = new ByteArrayOutputStream();
				classByteArrayOutputStreams.put(className, buffer);

				// Return a new SimpleJavaFileObject that returns the new output stream.
				return new SimpleJavaFileObject(sibling.toUri(), kind) {
					@Override
					public OutputStream openOutputStream() throws IOException {
						return buffer;
					}
				};
			}

			@Override
			public ClassLoader getClassLoader(Location location) {
				return new SecureClassLoader() {
					@Override
					protected Class<?> findClass(String name) throws ClassNotFoundException {
						// Get the bytes from the outputstream from the map.
						byte[] byteCode = classByteArrayOutputStreams.get(name).toByteArray();
						// Define the class from the bytes.
						return super.defineClass(name, byteCode, 0, byteCode.length);
					}
				};
			}
		};

		// Create a URI that identifies the source.
		URI uri = URI.create("string:///" + className.replace('.', '/') + JavaFileObject.Kind.SOURCE.extension);

		// Create a SimpleJavaFileObject that returns the Java source.
		JavaFileObject javaFileObject = new SimpleJavaFileObject(uri, JavaFileObject.Kind.SOURCE) {
			@Override
			public CharSequence getCharContent(boolean ignoreEncodingErrors) {
				return javaSource;
			}
		};

		// Create a compilation task.
		JavaCompiler.CompilationTask compilationTask = javaCompiler.getTask(additionalOutputWriter, javaFileManager,
				diagnosticCollector, Collections.emptyList(), null, Collections.singletonList(javaFileObject));

		// Try to compile the sources, if it fails throw an exception.
		if (!compilationTask.call()) {
			StringBuilder buffer = new StringBuilder("Compilation failed.\n");

			String additionalOutput = additionalOutputWriter.toString();

			if (additionalOutput != null && additionalOutput.length() > 0) {
				buffer.append("Compiler output: ").append(additionalOutput);
			}

			for (Diagnostic diagnostic : diagnosticCollector.getDiagnostics()) {
				buffer.append(diagnostic).append('\n');
			}

			Assert.fail(buffer.toString());
		}

		// The compilation succeeded.

		// Load the class using the classloader from the file manager and return it.
		return javaFileManager.getClassLoader(null).loadClass(className);
	}
}
