/*
 * Copyright (c) 2018, Peter Anderson <peter.anderson@anu.edu.au>
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

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import edu.anu.spice.TupleSet.Count;
import edu.cmu.meteor.aligner.SynonymDictionary;
import edu.cmu.meteor.util.Constants;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashSet;

public class SpiceUnitTest {
	
	private SemanticConcept blue;
	private SemanticConcept alsoBlue;
	private SemanticConcept notBlue;	
	private SemanticConcept azure;
	private SemanticConcept red;
	private SemanticConcept color;
	
	private SemanticConcept dog;
	private SemanticConcept cat;	
	
	@Before
	public void setup(){
		blue = new SemanticConcept("blue", new HashSet<Integer>(Arrays.asList(1)));
		alsoBlue = new SemanticConcept("blue", new HashSet<Integer>(Arrays.asList(1)));
		notBlue = new SemanticConcept("blue", new HashSet<Integer>(Arrays.asList(2)));	
		azure = new SemanticConcept("azure", new HashSet<Integer>(Arrays.asList(1)));
		red = new SemanticConcept("red", new HashSet<Integer>(Arrays.asList(2)));
		color = new SemanticConcept("color", new HashSet<Integer>(Arrays.asList(1,2,3)));
		
		dog = new SemanticConcept("dog", new HashSet<Integer>(Arrays.asList(10)));
		cat = new SemanticConcept("cat", new HashSet<Integer>(Arrays.asList(11)));	
	}
	
	@Test
	public void testSemanticConcept() {
		assertTrue(blue.equals(alsoBlue));
		assertFalse(blue.equals(notBlue));
		assertFalse(blue.equals(azure));
		assertFalse(blue.equals(red));
		assertFalse(blue.equals(color));
		assertFalse(red.equals(color));
		
		assertTrue(blue.similarTo(alsoBlue));
		assertTrue(blue.similarTo(notBlue));
		assertTrue(blue.similarTo(azure));
		assertFalse(blue.similarTo(red));
		assertTrue(blue.similarTo(color));
		assertTrue(red.similarTo(color));		
		
		SemanticConcept dogCat = new SemanticConcept("dog", new HashSet<Integer>(Arrays.asList(10)));
		assertFalse(dogCat.merge(dog));
		assertTrue(dogCat.equals(dog));
		assertTrue(dogCat.merge(cat));
		assertTrue(dogCat.concepts.equals(new HashSet<String>(Arrays.asList("dog","cat"))));
		assertTrue(dogCat.synsets.equals(new HashSet<Integer>(Arrays.asList(10,11))));
	}
	
	@Test
	public void testSemanticTuple() {
		SemanticTuple blueDog = new SemanticTuple(dog,blue);
		assertTrue(blueDog.size() == 2);
		SemanticTuple alsoBlueDog = new SemanticTuple(dog,blue);
		SemanticTuple anotherBlueDog = new SemanticTuple(dog,alsoBlue);
		SemanticTuple azureDog = new SemanticTuple(dog,azure);
		SemanticTuple redDog = new SemanticTuple(dog,red);
		SemanticTuple blueCat = new SemanticTuple(cat,blue);

		assertTrue(blueDog.matchesTo(alsoBlueDog));
		assertTrue(blueDog.matchesTo(anotherBlueDog));
		assertFalse(blueDog.matchesTo(azureDog));
		assertFalse(blueDog.matchesTo(redDog));
		assertFalse(blueDog.matchesTo(blueCat));
		
		assertTrue(blueDog.similarTo(alsoBlueDog));
		assertTrue(blueDog.similarTo(anotherBlueDog));
		assertTrue(blueDog.similarTo(azureDog));
		assertFalse(blueDog.matchesTo(redDog));
		assertFalse(blueDog.matchesTo(blueCat));
		
		redDog.merge(blueDog);
		assertTrue(redDog.size() == 2);
		SemanticConcept object = redDog.tuple.get(0);
		SemanticConcept attr = redDog.tuple.get(1);
		assertTrue(object.concepts.equals(new HashSet<String>(Arrays.asList("dog"))));
		assertTrue(object.synsets.equals(new HashSet<Integer>(Arrays.asList(10))));
		assertTrue(attr.concepts.equals(new HashSet<String>(Arrays.asList("blue","red"))));
		assertTrue(attr.synsets.equals(new HashSet<Integer>(Arrays.asList(1,2))));
	}
	
	@Test
	public void testTupleSet() {
		
		SynonymDictionary synonyms;
		URL synDirURL = Constants.DEFAULT_SYN_DIR_URL;
		try {
			URL excFileURL = new URL(synDirURL.toString() + "/english.exceptions");
			URL synFileURL = new URL(synDirURL.toString() + "/english.synsets");
			URL relFileURL = new URL(synDirURL.toString() + "/english.relations");
			synonyms = new SynonymDictionary(excFileURL, synFileURL, relFileURL);
		} catch (IOException ex) {
			throw new RuntimeException("Error: Synonym dictionary could not be loaded (" + synDirURL.toString() + ")");
		}

		boolean[] allowMerge = {true,false};
		for (int i=0; i<allowMerge.length; i++){
			SceneGraph can_sg = new SceneGraph(synonyms, allowMerge[i]);
			can_sg.addObject("bus");
			can_sg.addAttribute("bus", "red");
			can_sg.addRelation("bus", "street", "on");
			can_sg.addAttribute("bus", "double-decker");
			can_sg.addObject("street");
			can_sg.addObject("bus");
			can_sg.addAttribute("bus", "double-decker");
			can_sg.addRelation("bus", "street", "on");
			can_sg.addObject("bus");
			//System.out.println(can_sg.toReadableString());			
			TupleSet can = new TupleSet(can_sg);
			
			SceneGraph ref_sg = new SceneGraph(synonyms, allowMerge[i]);
			ref_sg.addObject("bus");
			ref_sg.addAttribute("bus", "red");
			ref_sg.addAttribute("bus", "double-decker");
			ref_sg.addObject("street");
			ref_sg.addRelation("bus", "street", "on");
			//System.out.println(ref_sg.toReadableString());
			TupleSet ref = new TupleSet(ref_sg);
			
			Count count = can.match_exact(ref);
			assertTrue(count.n == can.size());
			assertTrue(count.n == 5);
		}
	}
}
