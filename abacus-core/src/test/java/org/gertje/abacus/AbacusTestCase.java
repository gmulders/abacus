package org.gertje.abacus;


import com.google.gson.Gson;
import org.gertje.abacus.types.Type;

import java.util.List;

/**
 * Represents a single test case.
 */
public class AbacusTestCase {

	/**
	 * Represents the return value.
	 */
	public static class ReturnValue {
		public Type type;
		public Object value;
	}

	/**
	 * Represents a value in the symboltable.
	 */
	public static class Value {
		public String name;
		public Type type;
		public Object value;
	}

	public String expression;
	public ReturnValue returnValue;
	public boolean failsWithException;
	public List<Value> variableListBefore;
	public List<Value> variableListAfter;

	private String filename;

	@Override
	public String toString() {
		Gson gson = new Gson();
		return gson.toJson(this);
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}
}
