package org.worldbank.transport.tamt.gps;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.regex.Pattern;

import org.worldbank.transport.tamt.shared.DefaultFlow;
import org.worldbank.transport.tamt.shared.GPRMCRecord;
import org.worldbank.transport.tamt.shared.TAMTPoint;

public class ValidateNumeric {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		
		System.out.println("testing...");
		if( !validNumericFields())
		{
			throw new Exception("All traffic flow values must be integers");
		}
		System.out.println("passed...");
		
	}
	
	private static boolean validNumericFields() {

		try {
			String v1 = "";
			validateNumericValue(v1);
			
			String v2 = "aa";
			//validateNumericValue(v2);
			
			String v3 = "3";
			validateNumericValue(v3);

			String v4 = "3.1";
			//validateNumericValue(v4);

			String v5 = null;
			validateNumericValue(v5);
			
			return true;
		} catch (NumberFormatException e)
		{
			return false;
		}

	}
	
	private static void validateNumericValue(String value) throws NumberFormatException
	{
		if( value != null && !value.equalsIgnoreCase(""))
		{
			Integer.parseInt(value);
		}
	}

}
