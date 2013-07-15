package net.foben.schematizer.util;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;


import org.apache.commons.lang3.StringUtils;

public class RestrictingPLDReducer implements IPLDReducer {
	Set<String> restrictedSLDs;
	
	public RestrictingPLDReducer(){
		this(null);
	}
	
	public RestrictingPLDReducer(String filename){
		restrictedSLDs = new HashSet<String>();
		if(filename == null) return;
		try {
			BufferedReader br = new BufferedReader(new FileReader(filename));
			String line;
			while((line = br.readLine()) != null){
				this.addSLDRestriction(line);
			}
			br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public boolean addSLDRestriction(String sld){
		return restrictedSLDs.add(sld);
	}
	
	public boolean removeSLDRestriction(String sld){
		return restrictedSLDs.remove(sld);
	}
	
	public String getPLD(String uri){
		String result = uri;
		//String result = uri;
		int httpoff, domainEnd, dots, domainStart;
		if      (result.startsWith("http://"))  httpoff = 7;
		else if (result.startsWith("https://")) httpoff = 8;
		else throw new IllegalArgumentException("Not a valid URI: " + result);
		//end of domain
	
		domainEnd = result.substring(httpoff).contains("/") ? result.indexOf("/", httpoff) : result.length();
		//no of dots in domain 
		//System.out.println(domainEnd + "  " + uri);
		dots = StringUtils.countMatches(result.substring(0, domainEnd), ".");
		//Only the hostname
		String hostname = result.substring(0, domainEnd);
		String restricted;
		try{
			if(dots <= 1) domainStart = httpoff;
			else{
				restricted = containsRestricted(result.substring(0, domainEnd));
				//get second-to-last . within hostname
				if(restricted == null) domainStart = hostname.substring(0, hostname.lastIndexOf('.')).lastIndexOf('.') + 1;
				else domainStart = Math.max(httpoff, hostname.substring(0, hostname.length() - restricted.length()).lastIndexOf('.') + 1);
				
			}
			result = result.substring(domainStart, domainEnd);
		} catch(StringIndexOutOfBoundsException e){
			throw new IllegalArgumentException("[Not a valid URI: " + result);
		}
	
		
		return result;
	}
	
	private String containsRestricted(String url){
		String result = null;
		for(String restricted : restrictedSLDs){
			if(url.contains(restricted)){
				result = restricted;
				break;
			}
		}
		return result;
	}
}
