package org.gertje.testabacus;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;


public class AbacusTester {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		AbacusTester abacusTester = new AbacusTester();
		abacusTester.run();
	}

	private void run() throws IOException {
		AbacusTestFileReader atfr = new AbacusTestFileReader(new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("test1.txt"))));
		
		AbacusTest test;
		while ((test = atfr.nextAbacusTest()) != null) {
			test.run();
			test.printResult();
		}
	}

	private static void printOk(AbacusTest test) {
		System.out.println("OK: " + test.getExpression());
	}
	
	private static void printError(AbacusTest test) {
		System.out.println("Error: " + test.getExpression() + " " + test.getException());
	}
}
