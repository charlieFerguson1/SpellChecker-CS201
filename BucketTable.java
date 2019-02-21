import java.util.ArrayList;
import java.util.*;

public class BucketTable{

	ArrayList<ArrayList<Word>> table = new ArrayList<ArrayList<Word>>();
	
	private int cutoff = 3; //used in quicksort
	private int shift = 65;
	private int size;
	
	public BucketTable(int sizeIn)
	{
		size = sizeIn;
		for(int i = 0; i < sizeIn; i++)
		{
			ArrayList<Word> temp = new ArrayList<Word>();
			table.add(temp);
		}
	}
	
	
	public void add(Word wordOB)
	{
		System.out.println("adding word to hash: " + wordOB);
		String word = wordOB.getWord();
		if (!checkLast(word)) //checking for punctuation
		{
			word = word.substring(0, word.length()-1);
		}
		
		char firstLetter = word.charAt(0);
		int index = firstLetter - shift;
		ArrayList<Word> tempList = table.get(index);
		if(tempList.contains(wordOB))
		{
			int i = tempList.indexOf(wordOB);
			tempList.get(i).addToLineNumberOccurs(wordOB.getLineNumber());
			System.out.println("adding word to hash: " + wordOB);
		}
		else
			tempList.add(wordOB);
		table.set(index, tempList);

	}
	

	public void sortBuckets()
	{
		for(int i = 0; i < table.size(); i++)
		{
			quickSort(i);
		}
	}
	
	
	/**
	 * 
	 * @param index
	 * is the Arraylist to get from the overall hash table
	 */
	public void quickSort(int index)
	{
		ArrayList<Word> list = table.get(index);
		if(list.size() < 0)
		{
			if(list.size() <= cutoff)
			{
				list = insertionSort(list, 0 , list.size()-1);
			}
		}
	}
	/**
	 * 
	 * @param list
	 * @return
	 */
	public ArrayList<Word> quicksort(ArrayList<Word> list, int front, int back)
	{
		if(front < back)
		{			
			int i = front;
			int j = back;
			Word pivot = (medianOf3(list, front, back));
			list = swapPivot(list, front, back);
			
			do
			{ 
				while( list.get(i).compareTo(pivot) < 0) //scan till there is an out of place element
					i++;
				while(pivot.compareTo(list.get(j)) < 0) //scan back till there is an out of place element
					j++;
				
				if(i<=j)						//if the two out of place elements are on the proper side, swap them
				{
					Word temp = list.get(i);
					list.set(i, list.get(j));
					list.set(j, temp);
					i++;				//
					j++;				//swap the elements
				}	
				
			} while(i <= j);
		}
		
		return list;
	}
	/**
	 * 
	 * @param list
	 * ArrayList of type Word
	 * @param front
	 * index of the front of the relevant part of the list
	 * @param back
	 * index of the back of the relevant part of the list

	 * @return
	 * an ArrayList with the pivot at the end of the Array list
	 */
	public ArrayList<Word> swapPivot(ArrayList<Word> list, int front, int back)
	{
		int center = (front + back)/2;
		Word pivot = medianOf3(list, front, back);
		int last = list.size()-1;
		Word temp = list.get(last);
		
		list.set(last, pivot);
		list.set(center, temp);
		return list;
	}
	
	
	/**
	 * medianOf3
	 * 
	 * @param list
	 * list being sorted
	 * @param left
	 * the left most relevant index
	 * @param right
	 * the right most relevant index
	 * @return
	 * the median of the three words at the given 
	 */
	public Word medianOf3(ArrayList<Word> list , int left, int right) {
	    int center = (left + right) / 2;
	    Word a = list.get(left);
	    Word b = list.get(center);
	    Word c = list.get(right);

	    
	    if (a.compareTo(b) > 0)
	    {
	    	swap(list, left , right);
	    }
	    if(a.compareTo(c) > 0)
	    {
	    	swap(list, left, right);
	    }
	    if(b.compareTo(c) > 0)
	    {
	    	swap(list, center, right);
	    }
	    
	    return list.get(center);
	  }
	
	public void swap(ArrayList<Word> list, int a, int b)
	{
		Word temp = list.get(a);
		list.set(a, list.get(b));
		list.set(b, temp);
	}
	
	
	/**
	 * 
	 * @param list array list to be sorted
	 * @return
	 */
	public ArrayList<Word> insertionSort(ArrayList<Word> list , int front, int back)
	{
		for(int i = front + 1; i <= back; i++)
		{
			Word temp = list.get(i);
			int j = i-1;
			while(list.get(j).compareTo(temp) > 0 && j>=0)
			{
				j--;
			}
			list.set(j,temp);
		}
		return list;
	}
	
	
	public void printHash()
	{
		char bucket = 'A';
		for(int i = 0; i < table.size(); i++)
		{
			System.out.println("bucket: " + bucket);
			for(int j = 0; j<table.get(i).size(); j++)
			{
				if (j!= table.get(i).size())
				System.out.print(table.get(i).get(j) + ", ");
				else
					System.out.print(table.get(i).get(j));
			}
			bucket++;
		}
	}
	
	
	
	public boolean checkLast(String word)
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
	
	public int getSize()
	{
		return size;
	}
	
	
	public String sortedString()
	{
		String sorted = "";
		sortBuckets();
		for(int i = 0; i < size; i ++)
		{
			String temp = "";
			for (int j = 0; j < table.get(i).size(); j++)
			{
				temp = temp + table.get(i).get(j).toString() + "\n";
			}
			sorted = sorted + temp;
		}
		return sorted;
	}
}
