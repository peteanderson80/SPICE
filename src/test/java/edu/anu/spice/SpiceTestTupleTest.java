/*
 * Copyright (c) 2016, Peter Anderson <peter.anderson@anu.edu.au>
 *
 * This file is part of Semantic Propositional Image Caption Evaluation
 * (SPICE).
 * 
 * SPICE is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.

 * SPICE is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.

 * You should have received a copy of the GNU Affero General Public
 * License along with SPICE.  If not, see <http://www.gnu.org/licenses/>.
 * 
 */

package edu.anu.spice;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import static org.junit.Assert.*;

public class SpiceTestTupleTest extends SpiceTest {

	protected void compare(JSONArray expected, JSONArray actual) {
		assertEquals("Output wrong number of results", expected.size(), actual.size());
		for (int i = 0; i < expectedOutput.size(); i++){
			JSONObject expectedItem = (JSONObject) expected.get(i);
			JSONObject actualItem = (JSONObject) actual.get(i);
			
			// Image id
			assertEquals("Incorrect image id", expectedItem.get("image_id"), actualItem.get("image_id"));
			
			// Test tuples
			JSONArray expectedTestTuples = (JSONArray) expectedItem.get("test_tuples");
			JSONArray actualTestTuples = (JSONArray) actualItem.get("test_tuples");
			compareTuples(expectedTestTuples, actualTestTuples, "test", expectedItem.get("image_id"));
		}
	}


}
