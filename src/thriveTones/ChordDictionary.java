package thriveTones;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * "ThriveTones" Song Generator
 * Copyright © 2014 Brianna Shade
 * bshade@pdx.edu
 *
 * ChordDictionary.java
 * This class represents a chord dictionary object, storing unique Chords and chord histories up to 4 Chords
 */

@SuppressWarnings("serial")
public class ChordDictionary extends HashMap<LinkedList<Chord>, ArrayList<Chord>>{
	private Random random_generator = new Random();
	private static final int MAX_HISTORY_LENGTH = 3;

	/**
	 * Overloaded with native HashMap put
	 * @param sequence : history string, hash key
	 * @param chord : next chord possibility, hash value
	 */
	public void put(List<Chord> sequence, Chord chord){
		if(sequence == null) sequence = new LinkedList<Chord>();
		// shallow copy the sequence for key storage
		LinkedList<Chord> key = new LinkedList<Chord>(sequence);

		//reduce repetition in the dictionary
		boolean same = false;
		if(key.size() > 2){
			for(int i = key.size() - 1; i > 0; i--){
				same = key.get(i).equals(key.get(i-1));
				if(!same) break;
			}
		}
		//add only if we haven't seen 3 of the same chord before
		if(!same || !chord.equals(key.get(key.size() - 1))){
			// add in this chord (even if already present, for probability)
			ArrayList<Chord> available_chords = this.get(key);
			if(available_chords == null){
				// first chord at this key
				available_chords = new ArrayList<Chord>();
				this.put(key, available_chords);
			}

			available_chords.add(chord);								//available chords will change value component in dictionary

			//add entire history
			if(!key.isEmpty())
				put(key.subList(1, key.size()), chord);
		}
	}

	/**
	 * Given a Chord history set, returns a randomized next chord,
	 * recursively looking at shorter histories if the current one
	 * doesn't have a valid key value in the hash
	 * @param sequence : Chord history to look up
	 * @param debug : optional debug mode
	 * @return : a valid next Chord
	 */
	public Chord getANextChord(List<Chord> sequence, boolean debug){
		if(sequence == null)
			sequence = new LinkedList<Chord>();

		ArrayList<Chord> available_chords = this.get(sequence);
		if(available_chords == null){
			// history had nothing
			assert(sequence.size() > 0);
			// shorten the history by one and try again
			return getANextChord(sequence.subList(1, sequence.size()), debug);
		}
		//pull new random chord from the history choices
		int index = random_generator.nextInt(available_chords.size());
		if(debug){
			System.out.println("Available chords: " + available_chords.toString());
			int occurrences = Collections.frequency(available_chords, available_chords.get(index));
			System.out.println("Selected: " + available_chords.get(index)
					+ "; Selection probability: " + MessageFormat.format("{0,number,#.##%}", (occurrences*1.0/available_chords.size())));
		}
		return available_chords.get(index);
	}

	/**
	 * Retrieves the maximum history length
	 * @return : the maximum history length
	 */
	public int getMaxHistoryLength(){
		return MAX_HISTORY_LENGTH;
	}
}
