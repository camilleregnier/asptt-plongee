package com.asptt.plongee.resa.mail;

import java.util.List;

import javax.mail.MessagingException;

import org.apache.commons.mail.Email;
import org.apache.commons.mail.SimpleEmail;

import com.asptt.plongee.resa.exception.MailException;
import com.asptt.plongee.resa.exception.ResaException;
import com.asptt.plongee.resa.model.Plongee;

public class PlongeeMail {

	private Email email = null;
	private String subject = null;
	private String msg = null;
	private List<String> destis = null;
	
	public PlongeeMail() throws MessagingException {
		email = new SimpleEmail();
		email.setDebug(true);
		email.setHostName("smtp.orange.fr");
		email.setFrom("eric.simon28@orange.fr");
	}
	
	public PlongeeMail(MailException me) throws ResaException {
		email = new SimpleEmail();
		email.setDebug(true);
		email.setHostName("smtp.orange.fr");
		email.setSubject(me.getSubject());
		try {
			email.setMsg(me.getMsg());
		} catch (MessagingException e) {
			e.printStackTrace();
			throw new ResaException("probleme avec le sujet du mail");
		}
		this.destis = me.getFroms();
//		email.setFrom("camille.regnier@orange.fr");
//		email.addTo("camille.regnier@gmail.com");
	}
	
	public void sendMail() throws ResaException{
//		try {
//			email.setSubject("Attention : liste d'attente !");
//			email.setMsg("des plongeurs sont en liste d'attente pour le plongée du : ");
			try {
				for (String desti : destis){
					email.addTo(desti);
					email.send();
				}
			} catch (MessagingException e) {
				e.printStackTrace();
				throw new ResaException("pb lors de l'envoi d'un mail");
			}
//		} catch (MessagingException e) {
//			e.printStackTrace();
//			throw new ResaException("Problème lors de l'envoie du mail");
//		}
	}

	public Email getEmail() {
		return email;
	}

	public void setEmail(Email email) {
		this.email = email;
	}

	public String getSubject() {
		return subject;
	}
	
	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public List<String> getDestis() {
		return destis;
	}

	public void setDestis(List<String> destis) {
		this.destis = destis;
	}
	
}
