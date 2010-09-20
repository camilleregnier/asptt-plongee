package com.asptt.plongee.resa.mail;

import java.util.List;

import javax.mail.MessagingException;

import org.apache.commons.mail.Email;
import org.apache.commons.mail.SimpleEmail;

import com.asptt.plongee.resa.exception.MailException;
import com.asptt.plongee.resa.exception.ResaException;
import com.asptt.plongee.resa.model.Plongee;

public class PlongeeMail {


	private static PlongeeMail instance = null;
	private Email email = null;

	public PlongeeMail() throws MessagingException {
		super();
		this.email.setDebug(true);
		this.email.setHostName("smtp.orange.fr");
		this.email.setFrom("eric.simon28@orange.fr");
	}
	
	public PlongeeMail(Email email) throws MessagingException {
		this.email = email;
		this.email.setDebug(true);
		this.email.setHostName("smtp.orange.fr");
		this.email.setFrom("eric.simon28@orange.fr");
	}
	
	public PlongeeMail getInstance() throws MessagingException {
		if(null == instance){
			instance = new PlongeeMail();
		}
		return instance;
	}
	
	public void sendMail(List<String> destis) throws ResaException{
		try {
			for (String desti : destis){
				email.addTo(desti);
				email.send();
			}
		} catch (MessagingException e) {
			e.printStackTrace();
			throw new ResaException("pb lors de l'envoi d'un mail");
		}
	}

	public Email getEmail() {
		return email;
	}

	public void setEmail(Email email) {
		this.email = email;
	}

}
