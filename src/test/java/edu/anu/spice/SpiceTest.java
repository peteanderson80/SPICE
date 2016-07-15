package edu.anu.spice;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;

import javax.script.ScriptException;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.BeforeClass;
import org.junit.Test;

public abstract class SpiceTest {
	
	private static boolean initialize = true;
	protected static JSONArray expectedOutput;
	private static String inFile = "test_input.json";
	private static String refFile = "test_reference.json";
	private static String outFileNoCache = "test_output_no_cache.json";
	private static String outFilePreCache = "test_output_pre_cache.json";
	private static String outFilePostCache = "test_output_post_cache.json";

	private static String testDataPath(String fileName) {
		File resourcesDirectory = new File("src/test/data");
		return resourcesDirectory.getAbsolutePath() + "/" + fileName;
	}

	private static JSONArray loadJSONArray(String fileName) throws IOException, ParseException, FileNotFoundException {	
		JSONParser parser = new JSONParser();
		return (JSONArray) parser.parse(new FileReader(testDataPath(fileName)));
	}
	
	protected static void compareTuples(JSONArray expectedTuples, JSONArray actualTuples, String type, Object image_id){
		assertEquals(String.format("Incorrect number of %s tuples for image_id %s", type, image_id), 
				expectedTuples.size(), actualTuples.size());		
		HashMap<String, Boolean> expectedTupleMap = new HashMap<String, Boolean>();
		for (int j = 0; j < expectedTuples.size(); j++){
			JSONObject tuple = (JSONObject) expectedTuples.get(j);
			String tupleString = tuple.get("tuple").toString();
			expectedTupleMap.put(tupleString, (Boolean)tuple.get("truth_value"));
		}
		
		HashSet<String> actualTupleSet = new HashSet<String>();
		for (int j = 0; j <actualTuples.size(); j++){
			JSONObject tuple = (JSONObject) actualTuples.get(j);
			String tupleString = tuple.get("tuple").toString();
			// Check no duplicates
			assertFalse(String.format("Duplicated %s tuple found for image id %s: %s", type, image_id, tupleString),
					actualTupleSet.contains(tupleString));
			actualTupleSet.add(tupleString);
			// Check matches the expected tuple
			assertTrue(String.format("Found tuple %s, not expected in %s tuples for image id %s", tupleString, type, image_id), 
					expectedTupleMap.containsKey(tupleString));
			// Check truth value
			Boolean truthValue = (Boolean)tuple.get("truth_value");		
			assertEquals(String.format("Found %s tuple %s with incorrect truth value for image id %s", type, 
					tupleString, image_id), expectedTupleMap.get(tupleString), truthValue);
		}
	}
	
	private static void runNoCache() throws IOException, ScriptException {
		SpiceArguments args = new SpiceArguments();
		args.inputPath = testDataPath(inFile);
		args.outputPath = testDataPath(outFileNoCache);
		args.detailed = true;
		SpiceScorer scorer = new SpiceScorer();
		scorer.scoreBatch(args);		
	}
	
	private static void runWithCache() throws IOException, ScriptException {
		SpiceArguments args = new SpiceArguments();
		args.inputPath = testDataPath(inFile);
		args.outputPath = testDataPath(outFilePreCache);
		args.detailed = true;
		args.cache = testDataPath("");
		SpiceScorer scorer = new SpiceScorer();
		// Iteration to build cache
		scorer.scoreBatch(args);
		// Iteration to use cache
		args.outputPath = testDataPath(outFilePostCache);
		scorer.scoreBatch(args);
	}
	
	protected static void cleanDir() throws IOException {
		// Delete any existing cache
		Files.deleteIfExists(Paths.get(testDataPath("data.mdb")));
		Files.deleteIfExists(Paths.get(testDataPath("lock.mdb")));
		// Delete existing output
		Files.deleteIfExists(Paths.get(testDataPath(outFileNoCache)));
		Files.deleteIfExists(Paths.get(testDataPath(outFilePreCache)));
		Files.deleteIfExists(Paths.get(testDataPath(outFilePostCache)));	
	}

	@BeforeClass
	public static void loadExpectedOutput() throws IOException, ParseException, FileNotFoundException, ScriptException {
		expectedOutput = loadJSONArray(refFile);
		// Initialize once only using global variable
		if (!initialize) {
			return;
		}
	    initialize = false; 
	    cleanDir();
		// Generate fresh test outputs
		runNoCache();
		runWithCache();
	}
	
	protected abstract void compare(JSONArray expected, JSONArray actual);

	@Test
	public void testNoCache() throws IOException, ParseException, FileNotFoundException {
		compare(expectedOutput, loadJSONArray(outFileNoCache));		
	}

	@Test
	public void testPreCache() throws IOException, ParseException, FileNotFoundException {
		compare(expectedOutput, loadJSONArray(outFilePreCache));
	}
	
	@Test
	public void testPostCache() throws IOException, ParseException, FileNotFoundException {
		compare(expectedOutput, loadJSONArray(outFilePostCache));
	}

}