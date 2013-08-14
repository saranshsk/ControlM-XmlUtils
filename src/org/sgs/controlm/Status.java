package org.sgs.controlm;

import java.util.HashSet;
import java.util.Set;

public enum Status {
	
	OK("OK"),
	NOTOK("NOTOK");
	
	private static final Set<String> OK_CODES;
	private static final Set<String> NOTOK_CODES;
	static{
		OK_CODES = new HashSet<String>();		
		OK_CODES.add("*<result>true*");
		OK_CODES.add("*Percent: 100  status: Ended OK*");
		OK_CODES.add("OK");
		OK_CODES.add("*File does not exist*");
		
		NOTOK_CODES = new HashSet<String>();		
		NOTOK_CODES.add("NOTOK");
		NOTOK_CODES.add("*soap:Fault*");
		NOTOK_CODES.add("*failed*");
		NOTOK_CODES.add("*Time Limit for file watching was exceeded*");
		NOTOK_CODES.add("*<result>false*");
		NOTOK_CODES.add("RUNCOUNT = 12");
		NOTOK_CODES.add("RUNCOUNT = 40");
		NOTOK_CODES.add("RUNCOUNT = 60");
		NOTOK_CODES.add("RUNCOUNT = 8");
	}
	
	
	private String name;
	
	private Status(String name){
		this.name = name;
	}
	
	public String getName(){
		return name;
	}
	
	public static Status getStatusByCode(String otherCode){
		for(String code : OK_CODES){
			if(code.equals(otherCode)){
				return Status.OK;
			}
		}
		for(String code : NOTOK_CODES){
			if(code.equals(otherCode)){
				return Status.NOTOK;
			}
		}
		throw new RuntimeException(String.format("No Status found for unknown code: '%s'", otherCode));
	}
}
