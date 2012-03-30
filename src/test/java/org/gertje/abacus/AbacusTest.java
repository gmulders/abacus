package org.gertje.abacus;

import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.junit.Test;

public class AbacusTester {

	@Test
	public void testAbacus() throws IOException {
		AbacusTestFileReader atfr = new AbacusTestFileReader(new BufferedReader(new InputStreamReader(getClass().getClassLoader().getResourceAsStream("test1.txt"))));
		
		AbacusTest test;
		while ((test = atfr.nextAbacusTest()) != null) {
			test.run();
			assertTrue(test.getExpression(), test.printResult());
		}
	}
}
