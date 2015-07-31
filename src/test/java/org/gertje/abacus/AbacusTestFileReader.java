package org.gertje.abacus;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;

/**
 * Reads a list of test cases.
 */
public class AbacusTestFileReader {

	/**
	 * The list with testcases.
	 */
	private List<AbacusTestCase> abacusTestCaseList;

	/**
	 * Constructor.
	 * @param filename The filename to read from.
	 * @throws FileNotFoundException
	 */
	public AbacusTestFileReader(String filename) throws FileNotFoundException {
		// Get the input stream from the resource.
		InputStream in = getClass().getClassLoader().getResourceAsStream(filename);
		// Create the reader from the input stream.
		Reader reader = new BufferedReader(new InputStreamReader(in));

		final Gson gson=new GsonBuilder().create();
		abacusTestCaseList = gson.fromJson(reader, new TypeToken<List<AbacusTestCase>>() {}.getType());

		// Set the filename to all seperate testcases.
		for (AbacusTestCase abacusTestCase : abacusTestCaseList) {
			abacusTestCase.setFilename(filename);
		}
	}

	public List<AbacusTestCase> getAbacusTestCaseList() {
		return abacusTestCaseList;
	}
}
