package com.asptt.plongee.resa.exception;

public class ResaException extends Exception {   
	  
	private String key;   
	  
	public ResaException(String key, Throwable cause) {   
		super(cause);   
		this.key=key;
	}

	public ResaException(Throwable cause) {   
		super(cause);   
		this.key=cause.getMessage();
	}

	public ResaException(String key) {   
		this.key=key;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

}
