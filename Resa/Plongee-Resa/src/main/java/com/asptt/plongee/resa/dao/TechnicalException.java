package com.asptt.plongee.resa.dao;

public class TechnicalException extends Exception {

	// serialVersionUID
	private static final long serialVersionUID = 8154566252027772269L;

	public TechnicalException() {
		// default constructor
	}
	
	public TechnicalException(String message) {
		super(message);
	}

	public TechnicalException(Throwable e) {
		super(e);
	}

	public TechnicalException(String message, Throwable e) {
		super(message, e);
	}
}
