package com.asptt.plongee.resa.exception;

public class TechnicalException extends RuntimeException {
	private String key;
	// serialVersionUID
	private static final long serialVersionUID = 8154566252027772269L;

	public TechnicalException(String message) {
            super(message);
		this.key = message;
	}

	public TechnicalException(Throwable e) {
		super(e);
		this.key=e.getMessage();
	}

	public TechnicalException(String key, Throwable cause) {   
		super(cause);   
		this.key=key;
	}


	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	
}
