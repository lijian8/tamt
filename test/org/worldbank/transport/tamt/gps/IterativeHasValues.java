package org.worldbank.transport.tamt.gps;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TreeMap;
import java.util.regex.Pattern;

import org.worldbank.transport.tamt.shared.DefaultFlow;
import org.worldbank.transport.tamt.shared.GPRMCRecord;
import org.worldbank.transport.tamt.shared.TAMTPoint;

public class IterativeHasValues {

	static ArrayList<Integer> d = new ArrayList<Integer>();
	static ArrayList<Integer> s = new ArrayList<Integer>();
	static ArrayList<Integer> p = new ArrayList<Integer>();
	static boolean previous = true;
	static ArrayList<Boolean> hasValues = new ArrayList<Boolean>();
	static HashMap<Integer, Double> h = new HashMap<Integer, Double>();
	
	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		
		h.put(8, 4.0); // the second item would be an array of values
		h.put(9, 6.6);
		h.put(12, 1.33);
		h.put(14, 8.0);
		h.put(15, 2.73);
		h.put(16, 4.0);
		
		// simulate the hasValues in TrafficFlowReport
		
		hasValues.add(false);
		hasValues.add(false);
		hasValues.add(false);
		hasValues.add(false);
		hasValues.add(false);
		hasValues.add(false);
		hasValues.add(false);
		hasValues.add(false);
		hasValues.add(true);
		hasValues.add(true);
		hasValues.add(false);
		hasValues.add(false);
		hasValues.add(true);
		hasValues.add(false);
		hasValues.add(true);
		hasValues.add(true);
		hasValues.add(true);
		hasValues.add(false);
		hasValues.add(false);
		hasValues.add(false);
		hasValues.add(false);
		hasValues.add(false);
		hasValues.add(false);
		hasValues.add(false);
		
		// start at 0
		// recurse(hasValues, 0);
		populateHasValuesList();
		populateBeforeAfterList();
		populateBetweenList();
		populateDvaluesInH();
		populateSvaluesInH();
		
		System.out.println("use provided values:" + p);
		System.out.println("sub with default values:" + d);
		System.out.println("sub by interpolation:" + s);
		System.out.println("values:" + h);
		
		// findNext(s, 0);
		
		TreeMap map = new TreeMap(h);
		System.out.println("sorted values:" + map);
		Integer[] keys = (Integer[]) h.keySet().toArray(new Integer[0]);
		Arrays.sort(keys);
		for (int i = 0; i < keys.length; i++) {
			System.out.println("key:" + keys[i]);
		}
	}
	
	private static void populateDvaluesInH()
	{
		// fake it for now that all d values are 1.1
		for (int i = 0; i < d.size(); i++) {
			int d_item = d.get(i);
			h.put(d_item, 1.1);
		}
	}
	
	private static void populateSvaluesInH()
	{
		// insert all s values as null
		for (int i = 0; i < s.size(); i++) {
			int s_item = s.get(i);
			h.put(s_item, null);
		}
	}	
	
	private static double findNext(ArrayList<Integer> list, int position)
	{
		int v = list.get(position);
		System.out.println("working on " + v);
		int v1 = v + 1;
		System.out.println("\tlooking for " + v1);
		int n = position + 1;
		if( h.containsKey(v1))
		{
			System.out.println("\tinserting value for " + v1);
			h.put(v, h.get(v1));
			if( n < list.size())
			{
				findNext(list, n);
			}
			return h.get(v1);
		} else {
			System.out.println("\trecursing");
			double next = findNext(list, n);
			h.put(v, next);
			return next;
		}

	}
	
	private static void populateBetweenList()
	{
		for (int i = 0; i < p.size() - 1; i++) {
			int currentEntry = p.get(i);
			//System.out.println("currentEntry=" + currentEntry);
			int nextEntry = p.get(i+1);
			//System.out.println("nextEntry=" + nextEntry);
			int diff = nextEntry - currentEntry;
			//System.out.println("diff=" + diff);
			
			if( diff > 1)
			{
				//System.out.println("need to add("+(diff-1)+") starting from("+currentEntry+")");
				int start = currentEntry + 1;
				int end = currentEntry + diff;
				for (int j = start; j < end; j++) {
					//System.out.println("add("+j+")");
					s.add(j);
				}
			}
			
		}
	}
	
	private static void populateBeforeAfterList()
	{
		// before items
		int first = p.get(0);
		//System.out.println("first="+first);
		for (int i = 0; i < first; i++) {
			d.add(i);
		}
		
		int last = p.get( p.size() - 1 );
		//System.out.println("last="+last);
		for (int i = last + 1; i < hasValues.size(); i++) {
			d.add(i);
		}
		
	}
	
	private static void populateHasValuesList()
	{
		for (int i = 0; i < hasValues.size(); i++) {
			Boolean b = hasValues.get(i);
			if( b )
			{
				p.add(i);
			}
			
		}
	}
	
	

}
