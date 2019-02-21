import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;

public class SpellSorter {
	
		private static Scanner user = new Scanner (System.in);
		private static Scanner fr;
		private static BufferedWriter out;
		private static String fileName= "";

		private static boolean live = true;
		private static char[] alphabet = { 'a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z'};

		private static Queue<Word> missSpelledWords = new LinkedList<Word>();
		
		private static QuadraticProbingHashTable<Word> dictionary = new QuadraticProbingHashTable<Word>();
		private static QuadraticProbingHashTable<Word> miss = new QuadraticProbingHashTable<Word>();
		private static BucketTable missed = new BucketTable(58);
		
		public static void main(String[] args) throws FileNotFoundException, IOException
		{
			fillDictionary(new File(args[0]));
			System.out.println("Please enter a file name to be spell checked");
			
			fileName = user.next();
			File file = new File(fileName);
			readFile(file);
			missed.printHash();
			
			BufferedWriterOrder(makeOrder(),fileName);
			System.out.println("hello" + missed.sortedString());
			BufferedWriterSorted(missed.sortedString(), fileName);
		}
	
		
		
		/**
		 * fillDictionary:
		 * 
		 * This method reads through a file and calls the method addToTree() that 
		 * adds a string and filename to the tree in the form of a word object
		 * 
		 * @param 
		 * file a text file that will be read
		 * @throws FileNotFoundException
		 */
		public static void fillDictionary(File file) throws FileNotFoundException
		{
			fr = new Scanner (file);

			String tempS = "";
			while (fr.hasNext())
			{
				tempS = fr.next();
				Word tempW = generateWord(tempS);
				dictionary.insert(tempW);
			}
			dictionary.insert(new Word(" " ));
			dictionary.insert(new Word(""));
			System.out.println("Dictionary is full");
		}
		
		
		public static void readFile(File file) throws IOException
		{
			out = new BufferedWriter(new FileWriter(fileName+ "_corrected.txt"));
			fr = new Scanner (file);
			String tempS = "";
			String line = "";
			int lineNum = 0;
			while (fr.hasNextLine())
			{
				line = fr.nextLine();
				int count = line.length() - line.replace(" ", "").length();	//number of spaces in the line
				lineNum ++;
				int space;
				do {
					space = line.indexOf(" ");
					if(space > 0)
					{
						tempS = line.substring(0, space);
						line = line.substring(space+1);
					}
					else //if there are no spaces
					{
						tempS = line;
					}
					fileReaderHelper(tempS, lineNum);
					count--;	
				} while(count >= 0);
				out.write("\n");
			}
			out.close();
		}

		
		public static void fileReaderHelper(String tempS, int LineNum) throws IOException
		{
			Word tempW = generateWord(tempS);
			tempW.setLineNumber(LineNum);
			tempW = analyzeWord(tempW);
			
			writeCorrected(out,tempW); //writes the corrected file with the word tempW
		}

		public static void writeCorrected(BufferedWriter out, Word word) throws IOException
		{
			String temp = "";
			
			if(miss.contains(word) && miss.get(word).getRepSet())
			{
				temp = word.getReplacment() + word.getPunctuation() + " ";
			}
			else
			temp = word.getWord() + word.getPunctuation() + " ";
			out.write(temp);
			
		}
		/**
		 * analyze word:
		 * checks if the word is in the dictionary, then if it is in the 
		 * miss-spelled words table. 
		 * @param word
		 * @throws IOException 
		 */
		public static Word analyzeWord(Word word) throws IOException
		{
			if(live)
			{
				if(!(dictionary.contains(word)) && !word.getIgnore()) //if the word is miss spelled
				{
					missSpelledWords.add(word);
					if(!miss.contains(word))   // and the word is not in the miss spelled hash table
					{
						miss.insert(word);		//add to the hash
						dealWithCommand(word);  	//do what the user wants
					}
					else							//if the word is in the hash, apply the previous command
					{
						word = missContainsW(word); 
					}
					missed.add(word);
				}	
			}
			else
			{
				if(!dictionary.contains(word) && !word.getIgnore()) //if the word is miss spelled
				{
					if(miss.contains(word))   // and the word is not in the miss spelled hash table
					{				
						word = missContainsW(word); 
					}
					missed.add(word);
					
					System.out.println("added to queue");

					missSpelledWords.add(word);
					System.out.println("queue length: " + missSpelledWords.size());
				}
				

			}
			return word;
		}
		
		/**
		 * executes the if the word is already in the table of miss-spelled words
		 * it adds the ignored words to the out file, or executes commands on the 
		 * not ignored words
		 * @param word
		 * @throws IOException
		 */
		public static Word missContainsW(Word word) throws IOException
		{
			Word fromHash = miss.get(word);
			if (fromHash.getRepSet())
			{
				word.setReplacement(fromHash.getReplacment());
			}
			else if(!fromHash.getIgnore() && live) //assuming that two words with the same text would be the same Word object
			{
				dealWithCommand(word);
			}
			return word;
		}
		
		
		/**
		 * dealWithCommand:
		 * 
		 * @param command
		 * command is what the user wants to do with the misspelled word
		 * @param word
		 * word is the word object that is to be dealt with
		 * @throws IOException 
		 */
		public static void dealWithCommand( Word word) throws IOException
		{
			String command = presentOptions(word);
			if((command.equals("q")) || command.equals("Q"))
			{
				quit();
			}
			else if(command.equals("i"))
			{
				ignore(word);
			}
			else if(command.equals("r"))
			{
				replace(word);
			}
			
			//if command is an n just skip that word, ie: do nothing 
		}
		
		/**
		 * presentOptions:
		 * 
		 * prompts the user to give a command
		 * @return
		 * the command
		 */
		public static String presentOptions(Word w)
		{
			System.out.println("--" + w + " "+ w.getLineNumber());
			System.out.println("Enter a command: \nignore all (i), replace all (r), next (n), or quit (q)");
			String command = user.next();
			return command;
		}
		
		/**
		 * quit:
		 * this method is called when the user decides to quit the spell checker.
		 * It does this by entering a new while loop to read through the rest of the 
		 * file still adding the misspelled words to the smallfile_order.txt file.
		 * 
		 * @throws IOException
		 */
		public static void quit() throws IOException 
		{
			live = false;
		
			user.close();
			System.out.println("Goodbye");
			
			
		}
		
		public static void ignore(Word word)
		{
			if(miss.contains(word))
			{
				miss.get(word).setIgnore(true);
			}
			word.setIgnore(true);word.setIgnore(true);
		}
		/**
		 * replace:
		 * 
		 * @param w
		 * word that has a miss-spelled text
		 * 
		 * calls the methods that provide potential alternate spellings
		 * if there are no alternate spellings then it tells the user
		 * otherwise it prints the options and has the user choose the 
		 * new spelling option and then uses that choice to reset the 
		 * words "word" value.
		 * @throws IOException
		 */
		public static void replace(Word w) throws IOException
		{
			replaceSwap(w);
			replaceDelete(w);
			replaceAdd(w);
			replaceSplit(w);
			replaceReplace(w);
			
			LinkedList<String> potentialWords = w.getPotRep();
			if(potentialWords.size() != 0)
			{
				System.out.println("Potential replacment spellings:\n");
				for(int i = 0; i < potentialWords.size(); i++)
				{
					System.out.print("(" + (i+1)+ ") " + potentialWords.get(i)+ " ");
				}
				System.out.println("or next(n), or quit(q)");
				String response = user.next(); // do I need a different file reader for the user input and the actual file reader
				if(response.equals("q"))
				{
					quit();
				}
				else if(!response.equals("n"))
				{
					int choice = Integer.parseInt(response);
					w.setReplacement(potentialWords.get(choice-1));
					w.setRepSet(true);
				}
			}
			else
			{
				System.out.println("no suggested spellings");	
				ignore(w);
			}
				
		}

		
		/**
		 * replaceSwap:
		 * 
		 * takes in a word that needs to be replaced
		 * this method takes each letter and exchanges the adjacent chars
		 * if the substitution results in a valid word, the text of that word
		 * is added to a linked list in the word object of potential replacements
		 * @param w
		 * 
		 *
		 */
		public static void replaceSwap(Word w)
		{
			String originalString = w.getWord();
			for(int i = 0; i < originalString.length()-1; i++ )
			{
				int j = i + 1;
				char[] c = originalString.toCharArray();
				char temp = c[i];
				c[i] = c[j];
				c[j] = temp;
				String swappedString = new String(c);
				addPotRep(swappedString , w);
			}
		}
		
		/**
		 * 
		 * @param w word with miss-spelled text
		 * creates a char array with the misspelled word, then for each index in the array, it runs through
		 * the alphabet and tries each character, for each new word created, the specific word's potential
		 * word linked list is added to.
		 */
		public static void replaceAdd(Word w)
		{
			String text = w.getWord();
			char[] add = toCharArraySpace(text); //a char array with a space at the front
			for(int i = 0; i < add.length; i++)
			{
				for(int j = 0; j < alphabet.length; j++)
				{
					add[i] = alphabet[j];
					Word temp = generateWord(new String(add));
					if(dictionary.contains(temp)) //should you be able to add a word that was previously ignored?
					{
						addPotRep(charArrToStri(add), w);
					}
				}
				if(i != add.length-1)   //if this was not the last char in the word, move i left one and create a blank space in its place
				{
					add[i] = add[i+1];
					add[i+1] = '0';
				}
			}
			
		}
		/**
		 * adds a space after each of the characters in the word except the last, if there is
		 * if the two resulting words are valid, they are added to the word that was miss-spelled
		 * linkedlist.
		 * @param w
		 */
		public static void replaceSplit(Word w)
		{
			String text =w.getWord();
			char[] add = toCharArraySpace(text);
			for(int i = 1; i < add.length-1; i++)
			{
				add[i-1] = add[i];
				String one =stringOne(add, i);
				String two = stringTwo(add,i);
				Word oneW = generateWord(one);
				Word twoW = generateWord(two);
				if(dictionary.contains(oneW) && dictionary.contains(twoW))
				{
					addPotRep(one, w);
					addPotRep(two,w);
				}
			}
		}
		/**
		 * stringOne:
		 * takes in a char array and an index and creates a word from the characters up to the index
		 * @param in
		 * @param i
		 * @return
		 */
		public static String stringOne(char[] in, int i )
		{
			String temp = "";
			int j = 0;
			while(j< i)
			{
				temp = temp + in[j];
				j++;
			}
			return temp;
		}
		
		/**
		 * stringTwo:
		 * takes in a char array and an index and creates a word from the characters after the index
		 * @param in
		 * @param i
		 * @return
		 */
		public static String stringTwo(char[] in, int i)
		{
			String temp = "";
			int j = i+1;
			while(j < in.length)
			{
				temp = temp + in[j];
				j++;
			}
			return temp;
		}
		
		public static void replaceReplace(Word w)
		{
			String text =w.getWord();
			if(!checkLast(text))
			{
				text = text.substring(0,text.length()-1);
			}	
			char[] add = text.toCharArray();
			for(int i = 0; i < add.length; i++)
			{
				for(int j = 0; j < alphabet.length; j++)
				{
					add[i] = alphabet[j];
					addPotRep(charArrToStri(add), w);
				}
				add=text.toCharArray();
			}
		}
		
		/**
		 * replaceDelete:
		 * First index in array must be "empty"
		 * creates potential replacement text by deleting each of the letters individually
		 * and seeing if any of them are valid words
		 * @param w
		 * 
		 */
		public static void replaceDelete(Word w)
		{
			if(w.getWord().length()>1)
			{
				String text = w.getWord();

				char[] textArr = text.toCharArray();
				char holder = textArr[0];

				for(int i = 0; i < textArr.length; i++)
				{
					if( i == 0)
						text = text.substring(1);
					else
					{
						textArr[i -1 ] = holder;
						holder = textArr[i];
						textArr[i] = '0'; 
						text = charArrToStri(textArr);
					}
					addPotRep(text,w);
				}
			}
		}
				
		public static String charArrToStri(char[] arr)
		{
			String temp = "";

			for(int i = 0; i < arr.length; i++)
			{
				if(arr[i]!='0')
				{
					temp = temp + arr[i];
				}
				
			}
			return temp;
		}
		/**
		 * toCharArraySpaces:
		 * 
		 * 
		 * @param s string to be in the char array
		 * 
		 * @return
		 * a char array with a space at the front 
		 * 	
		 */
		public static char[] toCharArraySpace(String s)
		{
			int length = s.length();
			char[] newArr = new char[length+1];
			int k = 0; 
			while(k < length)
			{
				newArr[k+1] =s.charAt(k);
				k++;
			}
		return newArr;
		}
		
		/**
		 * addPotRep:
		 * 
		 * @param s 
		 * A potential replacment text
		 * @param w 
		 * the word with the miss-spelled text
		 */
		public static boolean addPotRep(String s, Word w)
		{
			Word potRep = generateWord(s);
			if(dictionary.contains(potRep)&& !w.contains(s))
			{
				w.add(s);
				return true;
			}
			return false;
		}

		/**
		 * generateWord:
		 * 
		 * @param text
		 * text to be made into a Word
		 * @return
		 * a word that has no punctuation and contains the input text
		 * @throws FileNotFoundException
		 */
		public static Word generateWord(String text) 
		{
			String punct = "";
				if(!checkLast(text)) 
				{
					punct = text.substring(text.length()-1);
					text = text.substring(0,text.length()-1);
				}
				Word tempW = new Word(text, punct);
			return tempW;
		}
		
		
		public static void printArray(char[] arr)
		{
			System.out.println("array:");
			
			for(int i = 0; i < arr.length; i++)
			{
				System.out.print("arr[" + i + "] = "+ arr[i] + " ");
			}
		}
		/**
		 * Check last checks the last char in the word and tells if it is a letter or not.
		 * 
		 * @param word
		 * 		is a string that is some kind of word, potentially with some 
		 * 		sort of punctuation at the end
		 * @return
		 * 		a boolean, true if the last char in the word is a letter, false if not
		 */
		public static boolean checkLast(String word)
		{
			boolean isLetter = true;
			int last = word.length() - 1;
			if (last > 0)
			{
				Character c = word.substring(word.length() - 1).charAt(0);
				if(!(Character.isLetter(c) || Character.isDigit(c))) 
				{
					isLetter = false;
				}
			}
			return isLetter;
		}
		/**
		 * order creates a string to be put into the file 
		 * @return
		 */
		public static String makeOrder()
		{

		
			System.out.println("queue length in make order: " + missSpelledWords.size());
			String order = "";
			while(!missSpelledWords.isEmpty())
			{
				Word temp = missSpelledWords.remove();
				order = order + temp.getWord() + " " + temp.getLineNumber() + " \n";
			}
			return order;
		}
	
		
		public static void BufferedWriterOrder(String s, String fileName) throws IOException 
		{ //writes order file
			File file = new File(fileName.substring(0,fileName.length()-4) + "_order" + fileName.substring(fileName.length()-4 ));
			BufferedWriter buffer = new BufferedWriter(new FileWriter(file));
			buffer.write(s);
			buffer.close();
		}
		
		public static void BufferedWriterSorted(String s, String fileName) throws IOException 
		{ //writes order file
			File file = new File(fileName.substring(0,fileName.length()-4) + "_sorted" + fileName.substring(fileName.length()-4 ));
			BufferedWriter buffer2 = new BufferedWriter(new FileWriter(file));
			buffer2.write(s);
			buffer2.close();
		}
		
		
}


