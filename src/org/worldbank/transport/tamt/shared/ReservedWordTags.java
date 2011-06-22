package org.worldbank.transport.tamt.shared;



public class ReservedWordTags {

	public static boolean isReservedTag(TagDetails tagDetails) {
			
			// check the #<description>
			if( tagDetails.getDescription().equalsIgnoreCase("#RES") || 
					tagDetails.getDescription().equalsIgnoreCase("#COM") || 
						tagDetails.getDescription().equalsIgnoreCase("#IND"))
			{
				return true;
			}
			
			// also check the name
			if( tagDetails.getName().equalsIgnoreCase("Residential") || 
					tagDetails.getName().equalsIgnoreCase("Commercial") || 
						tagDetails.getName().equalsIgnoreCase("Industrial"))
			{
				return true;
			}
			
			return false;
		}
}
