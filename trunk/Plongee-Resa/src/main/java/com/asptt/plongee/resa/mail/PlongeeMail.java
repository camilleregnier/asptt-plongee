package com.asptt.plongee.resa.mail;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import javax.mail.MessagingException;

import org.apache.commons.mail.Email;
import org.apache.commons.mail.SimpleEmail;

import com.asptt.plongee.resa.exception.MailException;
import com.asptt.plongee.resa.exception.ResaException;
import com.asptt.plongee.resa.model.Plongee;
import com.asptt.plongee.resa.util.Parameters;

public final class PlongeeMail {


	public static int MAIL_PLUS_ASSEZ_ENCADRANT = 0;
	public static int MAIL_PAS_ASSEZ_ENCADRANT = 3;
	public static int MAIL_INSCRIPTION_SUR_PLONGEE_FERMEE = 1;
	public static int MAIL_PLACES_LIBRES = 2;
	
	private static PlongeeMail instance = null;
	private Email email = null;
	private String hostName = null;
	private String from = null;
	
	public PlongeeMail() throws MessagingException {
		super();
		this.email.setDebug(true);
		this.email.setHostName(getHostName());
		this.email.setFrom(getFrom());
	}
	
	public PlongeeMail(Email email) throws MessagingException {
		this.email = email;
		this.email.setDebug(true);
		this.email.setHostName(getHostName());
		this.email.setFrom(getFrom());
	}
	
	public PlongeeMail getInstance() throws MessagingException {
		if(null == instance){
			instance = new PlongeeMail();
		}
		return instance;
	}
	
	public static void setInstance(PlongeeMail instance){
		PlongeeMail.instance = instance;
	}

	public void sendMail(String destinataire) throws ResaException{
		List<String> destis = null;
		try {
			if(destinataire.equalsIgnoreCase("ADMIN")){
				destis = getDestisAdmin();
			} else if (destinataire.equalsIgnoreCase("ENCADRANT")){
				destis = getDestisEncadrant();
			}
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

	public List<String> getDestisAdmin(){
		//récupération du mails des admins
        String admin = Parameters.getString("MAIL_DESTIS_ADMIN");
        StringTokenizer st = new StringTokenizer(admin, ";");
        List<String> destis = new ArrayList<String>();
//        while (st.hasMoreElements()){
//        	destis.add((String)st.nextElement());
//        }
//		destis.add("camille.regnier@orange.fr");
		destis.add("eric.simon28@orange.fr");
		return destis;
	}

	public List<String> getDestisEncadrant(){
		//récupération du mails des admins
        String admin = Parameters.getString("MAIL_DESTIS_ENCADRANT");
        StringTokenizer st = new StringTokenizer(admin, ";");
		List<String> destis = new ArrayList<String>();
//	    while (st.hasMoreElements()){
//	    	destis.add((String)st.nextElement());
//	    }
//		destis.add("camille.regnier@orange.fr");
		destis.add("eric.simon28@orange.fr");
//		destis.add("gilbert.dicostanzo@orange.fr");
//		destis.add("michel-a.marchetti@orange.fr");
//		destis.add("phanie.laurent@wanadoo.fr");//BAUDOIN
//		//destis.add("boulet@boulet.com");//BOULET
//		destis.add("viviane.valerian@orange.fr");//CANTO
//		destis.add("daniel.cavenati@free.fr");//CAVENATI
//		destis.add("cyana83@wanadoo.fr");//DANGLEANT
//		destis.add("benoit.delagarde@free.fr");//DELAGARDE
//		destis.add("philippe.fremy@orange.fr");//FREMY
//		destis.add("jpgallice@hotmail.com");//GALLICE
//		destis.add("germanique.jean-claude@neuf.fr");//GERMANIQUE
//		destis.add("gomez348@free.fr");//GOMEZ
//		destis.add("blue.bibi@free.fr");//HAMAIDE
//		destis.add("jeanpierre.maures@dekra.com");//MAURES
//		destis.add("mautalen1@aliceadsl.fr");//MAUTALEN
//		destis.add("picber@club-internet.fr");//PICCOLO
//		destis.add("gisele.marseille@wanadoo.fr");//PUGET
//		destis.add("philippe.rojas@gmail.com");//ROJAS
//		destis.add("jteffene@orange.fr");//TEFFENE
//		destis.add("hamou.zerrifi@orange-ftgroup.com");//HAMOU
		return destis;
	}

	public String getHostName() {
		if (null == hostName){
			hostName = Parameters.getString("mail.smtp.host");
		}
		return hostName;
	}

	public void setHostName(String hostName) {
		this.hostName = hostName;
	}

	public String getFrom() {
		if(null == from){
			from = Parameters.getString("mail.smtp.from");
		}
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

}
