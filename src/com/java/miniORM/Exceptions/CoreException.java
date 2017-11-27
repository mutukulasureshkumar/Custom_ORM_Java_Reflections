package com.java.miniORM.Exceptions;


@SuppressWarnings("serial")
public class CoreException extends Exception {
	private String message=null;
	public CoreException(String message){
		this.message=message;
	}
	
	public String toString(){
		return message;
	}
}
