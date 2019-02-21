import java.util.LinkedList;

public class Word implements Comparable<Word>{ 
	
	private String word; //the string associate with this Word object
	private int lineNumber;
	private boolean ignore = false;
	private String replacment;
	private String tempReplacment;
	private boolean tempRepSet = false;

	private LinkedList<Integer> lineNumberOccurs = new LinkedList<Integer>();
	private boolean repSet = false;
	private String punctuation = "";
	
	private LinkedList<String> potRep = new LinkedList<String>();

	public Word(String w)
	{
		word = w;
	}
	
	public Word(String w, String p)
	{
		word = w;
		punctuation = p;
	}
	
	public void setWord(String w)
	{
		word = w;
	}
	
	public String getWord()
	{
		return word;
	}
	

	public int getLineNumber()
	{
		return lineNumber;
	}
	
	public boolean getRepSet()
	{
		return repSet;
	}
	
	public void setRepSet(boolean in )
	{
		repSet = in;
	}
	
	public boolean getTempRepSet()
	{
		return tempRepSet;
	}
	
	public void setTempRepSet(boolean in )
	{
		tempRepSet = in;
	}
	public void setLineNumber(int in)
	{
		lineNumber = in;
		lineNumberOccurs.add(in);
	}
	public void addToLineNumberOccurs(int in)
	{
		lineNumberOccurs.add(in);
	}
	
	public boolean getIgnore()
	{
		return ignore;
	}
	
	public void setIgnore(boolean in)
	{
		ignore = in;
	}
	
	public void setReplacement(String w)
	{
		replacment = w;
	}
	public void setTempReplacement(String w)
	{
		tempReplacment = w;
	}
	
	public String getPunctuation()
	{
		return punctuation;
	}
	public void setPunctuation(String w)
	{
		punctuation = w;
	}
	
	public String getTempReplacment()
	{
		return tempReplacment;
	}
	public String getReplacment()
	{
		return replacment;
	}
	
	public LinkedList<String> addAndReturn(String s)
	{
		potRep.add(s);
		return potRep;
	}
	public void add(String s)
	{
		potRep.add(s);
	}
	
	public LinkedList<String> removeAndReturn(String s)
	{
		potRep.remove(s);
		return potRep;
	}
	
	public boolean contains(String s)
	{
		if(potRep.contains(s))
		{
			return true;
		}
		return false;
	}
	
	public void remove(String s)
	{
		potRep.remove(s);
	}
	
	public LinkedList<String> getPotRep()
	{
		return potRep;
	}
	
	
	public String swap(String s, int first, int second)
	{
		char[] temp = s.toCharArray();
		char a = temp[first-1];
		char b = temp[second-1];
		temp[first-1] = b;
		temp[second-1]= a;
		
		return temp.toString();

	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
	
		final int prime = 31;
		int result = 1;
		result = prime * result + ((word == null) ? 0 : word.hashCode());
		return result;
	}
	
	/*
	 * equals method that compares two words
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Word other = (Word) obj;
		if (word == null) {
			if (other.word != null)
				return false;
		} else if (!word.equals(other.word))
			return false;
		return true;
	}	
	
	
	public String toString()
	{
		return word;
	}
	
	/**
	 * compareTo:
	 * compares two words using their text
	 * 
	 * @param otherIn
	 * @return
	 * - 1 if this word comes before the argument, 0 if they are the same and 1 if this word comes after
	 * 
	 */
	public int compareTo(Word otherIn)
	{
		String thisIs = this.getWord();
		String other = otherIn.getWord();
		int i = 0;
		if(other.compareTo(thisIs) < 0)   
		{
			i = -1;
		}
		else if ((other.compareTo(thisIs) > 0))
		{
			i = 1;
		}
		
		return i;
		
	}

}