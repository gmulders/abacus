package org.gertje.abacus;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import org.gertje.abacus.types.Type;

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
	 * Private sub class that deserializes a type.
	 */
	private static class TypeDeserializer implements JsonDeserializer<Type> {
		@Override
		public Type deserialize(JsonElement json, java.lang.reflect.Type type, JsonDeserializationContext context)
				throws JsonParseException {

			if (json.isJsonNull()) {
				return null;
			}

			Type.BaseType baseType;
			int dimensionality = 0;

			if (json.isJsonObject()) {
				JsonObject jsonObject = json.getAsJsonObject();
				baseType = stringToBaseType(jsonObject.get("baseType").getAsString());
				dimensionality = jsonObject.get("dimensionality").getAsInt();
			} else {
				baseType = stringToBaseType(json.getAsString());
			}

			return Type.get(baseType, dimensionality);
		}

		private static Type.BaseType stringToBaseType(String baseType) {
			switch (baseType) {
				case "INTEGER" : return Type.BaseType.INTEGER;
				case "STRING" : return Type.BaseType.STRING;
				case "DECIMAL" : return Type.BaseType.DECIMAL;
				case "BOOLEAN" : return Type.BaseType.BOOLEAN;
				case "DATE" : return Type.BaseType.DATE;
			}

			throw new IllegalArgumentException("Unknown type '" + baseType + "'");
		}
	}

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

		final Gson gson = new GsonBuilder().registerTypeAdapter(Type.class, new TypeDeserializer()).create();
		abacusTestCaseList = gson.fromJson(reader, new TypeToken<List<AbacusTestCase>>() {}.getType());

		// Set the filename to all separate test cases.
		for (AbacusTestCase abacusTestCase : abacusTestCaseList) {
			abacusTestCase.setFilename(filename);
		}
	}

	public List<AbacusTestCase> getAbacusTestCaseList() {
		return abacusTestCaseList;
	}
}
