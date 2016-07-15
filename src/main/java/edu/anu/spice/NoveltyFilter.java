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

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class NoveltyFilter implements TupleFilter {

	protected HashSet<String> trainingTuples;

	public NoveltyFilter(SpiceParser parser, String cocoFile) throws IOException, ParseException {
		this.trainingTuples = new HashSet<String>();
		
		// Load training data
		JSONParser json = new JSONParser();
		List<String> captions = new ArrayList<String>();
		URL url = NoveltyFilter.class.getResource(cocoFile);
		JSONObject input = (JSONObject) json.parse(new InputStreamReader(url.openStream()));
		JSONArray anns = (JSONArray) input.get("annotations");
		for (Object o : anns) {
		    JSONObject item = (JSONObject) o;
		    captions.add((String)item.get("caption"));
		}
		
		// Parse training data into unmerged tuples
		System.err.println(String.format("Building TupleFilter from %s",cocoFile));
		Map<String, ArrayList<ArrayList<String>>> tuples = parser.loadTuples(captions);
		for (Entry<String, ArrayList<ArrayList<String>>> entry : tuples.entrySet()) {
			for (ArrayList<String> tuple : entry.getValue()){
				this.trainingTuples.add(tuple.toString());
			}
		}
		System.err.println(String.format("Found %d tuples",this.trainingTuples.size()));
	}

	@Override
	public boolean operation(SemanticTuple tuple) {
		ArrayList<ArrayList<String>> enumeratedTuples = tuple.enumerateTuples();
		for (ArrayList<String> t: enumeratedTuples){
			if (this.trainingTuples.contains(t.toString())){
				return false;
			}
		}
		return true;
	}

	public void add(NoveltyFilter other) {
		this.trainingTuples.addAll(other.trainingTuples);
	}

}
