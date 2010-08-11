package com.asptt.plongee.resa.mail;

import javax.mail.MessagingException;

import org.apache.commons.mail.Email;
import org.apache.commons.mail.SimpleEmail;

import com.asptt.plongee.resa.model.Plongee;

public class PlongeeMail {

	private Email email = null;
	
	public PlongeeMail() throws Exception {
		
		email = new SimpleEmail();
		email.setDebug(true);
		email.setHostName("smtp.orange.fr");
		email.setFrom("camille.regnier@orange.fr");
		email.addTo("camille.regnier@gmail.com");
	}
	
	public void sendListeAttenteMail(Plongee plongee){
		try {
			email.setSubject("Attention : liste d'attente !");
			email.setMsg("des plongeurs sont en liste d'attente pour le plongée du : ");
			email.send();
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
