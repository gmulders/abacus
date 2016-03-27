package org.gertje.abacus;

import org.junit.Test;

import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Test class.
 */
public class AbacusTest {


	private int testCount;

	/**
	 * All files containing test cases.
	 */
	public static String[] fileNames = {
			"test-add.json",
			"test-substract.json",
			"test-multiplication.json",
			"test-division.json",
			"test-modulo.json",
			"test-power.json",
			"test-positive.json",
			"test-negative.json",
			"test-not.json",
			"test-smaller.json",
			"test-smaller-equals.json",
			"test-equals.json",
			"test-not-equals.json",
			"test-greater-equals.json",
			"test-greater.json",
			"test-and.json",
			"test-or.json",
			"test-if.json",
			"test-assignment.json",

			"test-general.json"
	};

	@Test
	public void testAbacus() throws IOException {

		// Create a runner to test the evaluator and run the tests.
		runTestCaseRunner(new EvaluatorTestCaseRunner());
	}

	/**
	 * Runs the test case runner against all files.
	 * @param testCaseRunner The test case runner to run the test with.
	 * @throws FileNotFoundException
	 */
	protected void runTestCaseRunner(AbstractTestCaseRunner testCaseRunner) throws FileNotFoundException {
		testCount = 0;
		for (String fileName : fileNames) {
			testFile(fileName, testCaseRunner);
		}
		// System.out.println("Total count: " + testCount);
	}

	/**
	 * Runs all testcases in the given file.
	 * @param fileName The name of the file that contains the testcases.
	 * @param testCaseRunner The runner for the test cases.
	 * @throws FileNotFoundException
	 */
	private void testFile(String fileName, AbstractTestCaseRunner testCaseRunner) throws FileNotFoundException {
		// Get the reader that reads the list of test cases.
		AbacusTestFileReader atfr = new AbacusTestFileReader(fileName);

		// Test all separate test cases.
		for (AbacusTestCase abacusTestCase : atfr.getAbacusTestCaseList()) {
			testCount++;
			testCaseRunner.setAbacusTestCase(abacusTestCase);
			testCaseRunner.runTestCase();
		}
	}
}
