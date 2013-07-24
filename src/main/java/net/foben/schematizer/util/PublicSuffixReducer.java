package net.foben.schematizer.util;

import java.io.IOException;
//import 

import jp.co.osstech.regdom4j.RegDomain;

public class PublicSuffixReducer implements IPLDReducer {
	
	//Set<String> suffixes
	RegDomain regdom;
	private long time;
	private int invocs;
	public PublicSuffixReducer() throws IOException{
		/*
		BufferedReader in = new BufferedReader(new FileReader("src/main/resources/publicsuffix_clean.txt"));
		String line;
		while((line = in.readLine()) != null){
			System.out.println(line);
		}
		in.close();
		*/
		regdom = new RegDomain();
		time = 0;
		invocs = 0;
		//String result = regdom.getRegisteredDomain("www.example.com");
	}
	
	@Override
	public String getPLD(String uri) {
		String result = uri;
		int httpoff, domainEnd;
		if      (result.startsWith("http://"))  httpoff = 7;
		else if (result.startsWith("https://")) httpoff = 8;
		else throw new IllegalArgumentException("Not a valid URI: " + result);
		//end of domain
	
		domainEnd = result.substring(httpoff).contains("/") ? result.indexOf("/", httpoff) : result.length();
		
		String trimmed = result.substring(httpoff, domainEnd);
		if(trimmed.contains(":")){
			trimmed = trimmed.substring(0, trimmed.indexOf(":"));
		}
		if(isIp(trimmed)){
			return trimmed;
		}
		long start = System.nanoTime();
		String res = regdom.getRegisteredDomain(trimmed);
		long dur = System.nanoTime() - start;
		time += dur;
		invocs++;
		return res;
	}
	
	private boolean isIp(String trimmed) {
		if(!trimmed.contains(".")) return false;
		String[] parts = trimmed.split("\\.");
		for(String str : parts){
			try{
				Integer.parseInt(str);
			} catch(NumberFormatException e){
				return false;
			}
		}
		return true;
	}

	public void printBench(){
		double avg = time / invocs;
		double avgsec = avg / 1000000000d;
		System.out.println("Average: " + avgsec);
		System.out.println("Per Mil: " + avgsec * 1000000 );
	}

}
