package org.gertje.abacus;

import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.junit.Test;

public class AbacusTest {

	@Test
	public void testAbacus() throws IOException {
		InputStream in = getClass().getClassLoader().getResourceAsStream("test1.txt");
		AbacusTestFileReader atfr = new AbacusTestFileReader(
				new BufferedReader(
						new InputStreamReader(
								in)));
		
		AbacusTestCase testCase;
		while ((testCase = atfr.nextAbacusTestCase()) != null) {
			testCase.run();
			assertTrue(testCase.getExpression(), testCase.printResult());
		}
	}
}
