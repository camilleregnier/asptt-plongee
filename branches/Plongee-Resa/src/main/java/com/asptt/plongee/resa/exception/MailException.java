package com.asptt.plongee.resa.exception;

import java.util.List;

public class MailException extends Exception {   
	  
	private String subject = null;
	private String msg = null;
	private List<String> froms = null;
	  
	public MailException(String sujet, List<String> destinataires) {   
		this.subject=sujet;
		this.froms=destinataires;
	}

//	public MailException(Throwable cause) {   
//		super(cause);   
//		this.subject=cause.getMessage();
//	}

	public MailException(String sujet) {   
		this.subject=sujet;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String sujet) {
		this.subject = sujet;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public List<String> getFroms() {
		return froms;
	}

	public void setFroms(List<String> froms) {
		this.froms = froms;
	}

}
