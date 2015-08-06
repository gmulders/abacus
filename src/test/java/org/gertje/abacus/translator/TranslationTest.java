package org.gertje.abacus.translator;

import org.gertje.abacus.AbacusTest;
import org.junit.Test;

public class TranslationTest extends AbacusTest {

	@Test
	public void testTranslation() throws Exception {
		runTestCaseRunner(new TranslatorTestCaseRunner());
	}
}
