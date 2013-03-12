package com.dallinc.masstexter;

public class Template {
	
	String groupName;
	String message;

	public Template(String name, String m){
		groupName = name;
		message = m;
	}
	
	public String name(){
		return groupName;
	}
	
	public String message(){
		return message;
	}

}
