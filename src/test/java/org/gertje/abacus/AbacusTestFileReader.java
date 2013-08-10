package org.gertje.abacus;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

public class AbacusTestFileReader {

	BufferedReader reader;
	
	public AbacusTestFileReader(File file) throws FileNotFoundException {
		reader = new BufferedReader(new FileReader(file));
	}
	
	public AbacusTestFileReader(BufferedReader reader) throws FileNotFoundException {
		this.reader = reader;
	}
	
	public AbacusTestCase nextAbacusTestCase() throws IOException {
		String line = reader.readLine();
		while (line != null && (line.startsWith("#") || line.trim().isEmpty())) {
			line = reader.readLine();
		}

		if (line == null) {
			return null;
		}
		String[] tokens = line.split("@");
		
		return new AbacusTestCase(
				tokens[0], 
				getValue(tokens[1]), 
				getExpectedException(tokens[2]), 
				symbols(tokens.length > 3 ? tokens[3] : null), 
				symbols(tokens.length > 4 ? tokens[4] : null));
	}
	
	private Object getValue(String token) {
		Object value;
		
		if ("null".equals(token)) {
			return null;
		}
		
		String[] valueTokens = token.split("=");
		if ("Integer".equals(valueTokens[0])) {
			value = new BigInteger(valueTokens[1]);
		} else if ("Float".equals(valueTokens[0])) {
			value = new BigDecimal(valueTokens[1]);
		} else if ("Boolean".equals(valueTokens[0])) {
			value = new Boolean(valueTokens[1]);
		} else {
			value = valueTokens[1];			
		}

		return value;
	}
	
	private boolean getExpectedException(String token) {
		return (new Boolean(token)).booleanValue();		
	}
	
	private Map<String, Object> symbols(String token) {
		if (token == null || token.equals("")) {
			return new HashMap<String, Object>();
		}
		Map<String, Object> symbols = new HashMap<String, Object>();
		String[] s = token.split(":");

		for (String symbol : s) {
			String[] symbolTokens = symbol.split("=");
			Object value = null;
			if ("Integer".equals(symbolTokens[0])) {
				value = new BigInteger(symbolTokens[2]);
			} else if ("Float".equals(symbolTokens[0])) {
				value = new BigDecimal(symbolTokens[2]);
			} else if ("Boolean".equals(symbolTokens[0])) {
				value = new Boolean(symbolTokens[2]);
			} else {
				value = symbolTokens[2];				
			}
			
			symbols.put(symbolTokens[1], value);
		}
		
		return symbols;
	}
	
}
