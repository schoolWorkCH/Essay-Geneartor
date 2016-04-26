import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;

/**
 * name: Chen Huang
 * 
 * A short design description of your solution here (5-10 lines)
 * 
 * 
 *
 */
public class Project6 {
	
	private static int LINE_LENGTH_MAX = 150;
	private static int WORDS_PER_PARAGRAPH_MIN = 150;
	
	private HashMap<String, HashSet<String>> wordMap;
	private ArrayList<String> wordList;
	
	/**
	 * Read the file and populate the data structures
	 * 
	 * @param filename
	 */
	public Project6(String filename) {
		wordList = new ArrayList<>();
		wordMap = loadWordMap(filename);
	}
	
	/**
	 * Make an essay with the given number of paragraphs.
	 * 
	 * @param numParagraphs
	 * @return
	 */
	public StringBuilder makeEssay(int numParagraphs) {
		StringBuilder sb = new StringBuilder();
		Scanner s = null;
		for (int i = 0; i < numParagraphs; i++) {
			String sentence = makeParagraph();
			 s = new Scanner(sentence);
			
			StringBuilder temp = new StringBuilder();
			temp.append("      "); //tab for each paragraph
			String lastWord = null;
			while (s.hasNext()) { 
				String currentWord = s.next();
				
				//add the word if it does not repeat the last one
				if (!currentWord.equals(lastWord)) {
					temp.append(currentWord + " ");
				}
				
				if (temp.length() > LINE_LENGTH_MAX) { //if temp is long enough
					sb.append(temp.toString() + "\n"); //add it in and add break line
					temp.setLength(0); //clear temp 
				}
				lastWord = currentWord;
			}
			
			sb.append(temp + "\n"); //break line to end current paragraph
			
			sb.append("\n"); //break line for the next paragraph to jump in
		}
		
		s.close();
		return sb;
	}

	// TODO: All your private methods here
	
	//load the entire txt file into my ADT
	private HashMap<String, HashSet<String>> loadWordMap(String filename) {
		//initialize
		HashMap<String, HashSet<String>> wordMap = new HashMap<>();
		
		//read file and dump into the ADT
		try (Scanner s = new Scanner(new File(filename))) {
			String previousWord = null;
			
			while (s.hasNext()) {
				String currentWord = s.next();
				//if this word has a previous word to link to
				if (previousWord != null) {
					
					//if the previous word not in the keyset, add it, and also add it to the wordList because it is bound to be unique
					if (!wordMap.containsKey(previousWord)) {
						wordMap.put(previousWord, new HashSet<String>());
						wordList.add(previousWord);
					}
					
					wordMap.get(previousWord).add(currentWord);
				}
				
				//update previous word 
				previousWord = currentWord;
			}
			
		} catch (FileNotFoundException e) {
			System.err.println("could not find file " + filename);
			return null;
		}
		
		return wordMap;
	}
	
	private String makeParagraph() {
		StringBuilder sb = new StringBuilder();
		
		int count = 0;
		String word = getRandomEligibleWord();
		
		while (true) {
			
			sb.append(word);
			
			
			
			if (count > WORDS_PER_PARAGRAPH_MIN && word.charAt(word.length() - 1) == '.') {
				break;
			}
			
			count++;
			
			word = getNextEligibleWord(word);
			sb.append(" ");
		}
		
		sb.setCharAt(0,  Character.toUpperCase(sb.charAt(0))); //uppercase the first letter
		
		String result = sb.toString();
		
		result = result.replaceAll("_", "");
		result = result.replaceAll("\"", "");
		
		return result;
	}
	
	private String getRandomEligibleWord() {
		String word;
		do {
			word = wordList.get((int)(Math.random() * wordList.size()));
		} while (!isEligible(word));
		
		return word;
	}
	
	private String getNextEligibleWord(String previousWord) {
		String nextWord;
		int loopCount = 0;
		
		//keep picking if the word is not eligible
		do {
			loopCount++;
			
			nextWord = getNextWord(previousWord);
			
			if (loopCount > 1000) { //if loop too much, then it means the corresponding set does not contain an eligible word, then pick from the word list randomly
				nextWord = getRandomEligibleWord();
				break;
			}
		} while (!isEligible(nextWord)); //as soon as one eligible word is found, break out
		
		return nextWord;
	}
	
	private String getNextWord(String previousWord) {
		HashSet<String> possibleNextWords = wordMap.get(previousWord); //find the set corresponding to the word
		
		if (possibleNextWords.size() == 0) {
			return getRandomEligibleWord();
		}
		
		int randomIndex = (int)(Math.random() * possibleNextWords.size()); //find a random index
		String nextWord = null;
		
		int count = 0;
		for (String word: possibleNextWords) { //pick a random word from set 
			if (count == randomIndex) {
				nextWord = word;
				break;
			}
			count++;
		}
		
		return nextWord;
	}
	
	private boolean isEligible(String word) {
		//count caps
		int upperCount = 0;
		for (int i = 0; i < word.length(); i++) {
			if (Character.isUpperCase(word.charAt(i))) {
				upperCount++;
			}
		}
		
		if (upperCount > 1) { //if has more than 1 cap, then disqaulify
			return false;
		}
		
		 for (int i = 0; i <word.length(); i++) {
			 char c= word.charAt(i);
			 if (!(Character.isAlphabetic(c) || c == ';' || c == '-' || c == ',' || c == '.')) { //if one of the character is not one of these, disqaulify
				 return false;
			 }
		 }
		 
		 return true;
	}
	
	public static void main(String[] args) {
		/*
		 * Can get the text file from the link below.
		 * http://www.gutenberg.org/files/135/135-0.txt
		 */
		Project6 essayMaker = new Project6("LesMiserables.txt");
		System.out.println(essayMaker.makeEssay(5));
	}
}
