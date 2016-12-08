package org.gertje.abacus.translator.javascript;

import org.gertje.abacus.AbacusTest;
import org.junit.Test;

import java.io.FileNotFoundException;

public class JavaScriptTranslatorTest extends AbacusTest {

	@Test
	public void testTranslator() throws FileNotFoundException {
		runTestCaseRunner(new TranslatorTestCaseRunner());
	}
}
