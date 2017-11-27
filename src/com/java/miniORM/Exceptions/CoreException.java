package com.java.miniORM.Exceptions;

/**
 * @author ${Suresh M Kumar}
 *
 * Aug 27, 2017
 */
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
