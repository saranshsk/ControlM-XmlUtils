package org.sgs.controlm;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum Status {
	
	OK("OK"),
	NOTOK("NOTOK");
	
	private static final Set<Pattern> OK_PATTERNS;
	private static final Set<Pattern> NOTOK_PATTERNS;
	static{
		OK_PATTERNS = new HashSet<Pattern>();		
		OK_PATTERNS.add(Pattern.compile("*<result>true*"));
		OK_PATTERNS.add(Pattern.compile("*Percent: 100  status: Ended OK*"));
		OK_PATTERNS.add(Pattern.compile("OK"));

		NOTOK_PATTERNS = new HashSet<Pattern>();		
		NOTOK_PATTERNS.add(Pattern.compile("NOTOK"));
		NOTOK_PATTERNS.add(Pattern.compile("RUNCOUNT = *"));
		NOTOK_PATTERNS.add(Pattern.compile("*soap:Fault*"));
		NOTOK_PATTERNS.add(Pattern.compile("*failed*"));
		NOTOK_PATTERNS.add(Pattern.compile("*Time Limit for file watching was exceeded*"));
		NOTOK_PATTERNS.add(Pattern.compile("*File does not exist*"));
	}
	
	
	private String name;
	
	private Status(String name){
		this.name = name;
	}
	
	public String getName(){
		return name;
	}
	
	public static Status getStatusByCode(String code){
		for(Pattern pattern : OK_PATTERNS){
			Matcher matcher = pattern.matcher(code);
			if(matcher.matches()){
				return Status.OK;
			}
		}
		for(Pattern pattern : NOTOK_PATTERNS){
			Matcher matcher = pattern.matcher(code);
			if(matcher.matches()){
				return Status.NOTOK;
			}
		}
		throw new RuntimeException(String.format("No Status found for unknown code: '%s'", code));
	}
}
