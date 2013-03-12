package com.dallinc.masstexter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import android.net.Uri;

public class Group {
	
	String groupName;
	ArrayList<CustomContact> contacts;

	public Group(String name){
		groupName = name;
		contacts = new ArrayList<CustomContact>();
	}
	
	public void add(String name, String number){
		contacts.add(new CustomContact(name, number));
		Collections.sort(contacts, new customComparator());
	}
	
	public void remove(String number){
		int index = -1;
		for(int i=0; i<contacts.size(); i++){
			if(contacts.get(i).number().equals(number)){
				index = i;
				break;
			}
		}
		contacts.remove(index);
		Collections.sort(contacts, new customComparator());
	}
	
	public void remove(int i){
		contacts.remove(i);
		Collections.sort(contacts, new customComparator());
	}
	
	public void clear(){
		contacts.clear();
	}
	
	public int size(){
		return contacts.size();
	}
	
	public String name(){
		return groupName;
	}
	
	public String toString(){
		String temp = "";
		for(int i=0; i<contacts.size(); i++){
			temp += contacts.get(i).number() + ", ";
		}
		return temp.substring(0, temp.length()-2);
	}
	
	public String numberAt(int i){
		return contacts.get(i).number();
	}
	
	public String nameAt(int i){
		return contacts.get(i).name();
	}
	
	public CustomContact get(int i){
		return contacts.get(i);
	}
	
	class customComparator implements Comparator<CustomContact> {
	    public int compare(CustomContact c1, CustomContact c2) {
	    	return c1.name().compareTo(c2.name());
	    }
	}

}
