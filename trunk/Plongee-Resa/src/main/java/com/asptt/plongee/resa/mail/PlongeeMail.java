package com.asptt.plongee.resa.mail;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import javax.mail.MessagingException;

import org.apache.commons.mail.Email;
import org.apache.commons.mail.SimpleEmail;

import com.asptt.plongee.resa.exception.MailException;
import com.asptt.plongee.resa.exception.ResaException;
import com.asptt.plongee.resa.model.Adherent;
import com.asptt.plongee.resa.model.Plongee;

public final class PlongeeMail {


	public static final int MAIL_PLUS_ASSEZ_ENCADRANT = 0;
	public static final int MAIL_PAS_ASSEZ_ENCADRANT = 3;
	public static final int MAIL_INSCRIPTION_SUR_PLONGEE_FERMEE = 1;
	public static final int MAIL_PLACES_LIBRES = 2;
	public static final int MAIL_LISTE_ATTENTE_EXIST = 5;
	
	private Email email = null;
	private String hostName = null;
	private String from = null;
	private Plongee plongee = null;
	private Adherent adherent = null;
	
	public PlongeeMail(int type, Plongee plongee, Adherent adherent) throws MessagingException {
		this.plongee = plongee;
		this.adherent = adherent;
		this.email = new SimpleEmail();
		this.email.setDebug(true);
		this.email.setHostName(getHostName());
		this.email.setFrom(getFrom());
		this.email.setCharset(SimpleEmail.ISO_8859_1);
		setNotreMsg(type);
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
			}
			email.send();
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
        String admin = ParametersMail.getString("MAIL_DESTIS_ADMIN");
        StringTokenizer st = new StringTokenizer(admin, ";");
        List<String> destis = new ArrayList<String>();
        while (st.hasMoreElements()){
        	destis.add((String)st.nextElement());
        }
//		destis.add("camille.regnier@orange.fr");
//		destis.add("eric.simon28@orange.fr");
		return destis;
	}

	public List<String> getDestisEncadrant(){
		//récupération du mails des admins
        String admin = ParametersMail.getString("MAIL_DESTIS_ENCADRANT");
      StringTokenizer st = new StringTokenizer(admin, ";");
		List<String> destis = new ArrayList<String>();
	    while (st.hasMoreElements()){
	    	destis.add((String)st.nextElement());
	    }
//		destis.add("camille.regnier@orange.fr");
//		destis.add("eric.simon28@orange.fr");
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
			hostName = ParametersMail.getString("mail.smtp.host");
		}
		return hostName;
	}

	public void setHostName(String hostName) {
		this.hostName = hostName;
	}

	public String getFrom() {
		if(null == from){
			from = ParametersMail.getString("mail.smtp.from");
		}
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	private void setNotreMsg(int type) throws MessagingException{

		// Mise en forme de la date
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		String dateAffichee = sdf.format(plongee.getDate());
		StringBuffer sb = null;;

		switch (type) {
		case MAIL_INSCRIPTION_SUR_PLONGEE_FERMEE: 
			this.email.setSubject("Inscription sur la plong\u00e9e du : "+dateAffichee+" ("+plongee.getType()+") encore ferm\u00e9e");
			sb = new StringBuffer("Bonjour,\n");
			sb.append("L'encadrant / P4 "+adherent.getPrenom()+" "+adherent.getNom()+" \n");
			sb.append("viens de s'inscrire \u00e0 la plong\u00e9e du "+dateAffichee+" ("+plongee.getType()+")\n");
			sb.append("\n");
			sb.append("Cette plong\u00e9e est encore ferm\u00e9e.\n");
			sb.append("\n");
			sb.append("Pouvez-vous l'ouvrir en trouvant un DP et/ou un pilote ?\n");
			sb.append("Cordialement.\n");
			this.email.setMsg(sb.toString());
			break;
		case MAIL_PAS_ASSEZ_ENCADRANT: 
			this.email.setSubject("Manque d'encadrement - plong\u00e9e du : "+dateAffichee+" ("+plongee.getType()+")");
			sb = new StringBuffer("Bonjour,\n");
			sb.append("Nous avons un manque d'encadrant \u00e0 la plong\u00e9e du "+dateAffichee+" ("+plongee.getType()+")\n");
			sb.append("Si vous \u00eates disponible, votre inscription est la bienvenue.\n");
			sb.append("\n");
			sb.append("-----------------------------------------------------------\n");
			sb.append("ATTENTION : dans ce cas AVERTISSEZ les administrateurs \n");
			sb.append("pour qu'ils inscrivent les personnes en liste d'attente.\n");
			sb.append("-----------------------------------------------------------\n");
			sb.append("\n");
			sb.append("Cordialement.\n");
			this.email.setMsg(sb.toString());
			break;
		case MAIL_PLACES_LIBRES:
			this.email.setSubject("Gestion de file d'attente - plong\u00e9e du : "+dateAffichee+" ("+plongee.getType()+")");
			sb = new StringBuffer("Bonjour,\n");
			sb.append("Des personnes sont en file d'attente \u00e0 la plong\u00e9e du "+dateAffichee+" ("+plongee.getType()+")\n");
			sb.append("\n");
			sb.append("Une place vient de se lib\u00e9rer.\n");
			sb.append("\n");
			sb.append("Cordialement.\n");
			this.email.setMsg(sb.toString());
			break;
		case MAIL_PLUS_ASSEZ_ENCADRANT: 
			this.email.setSubject("Encadrement de la plong\u00e9e du : "+dateAffichee+" ("+plongee.getType()+")");
			sb = new StringBuffer("Bonjour,\n");
			sb.append("Un encadrant ("+adherent.getPrenom()+" "+adherent.getNom()+") vient de se d\u00e9sinscrire de la plong\u00e9e du "+dateAffichee+" ("+plongee.getType()+")\n");
			sb.append("\n");
			sb.append("Il n'y a plus assez d'encadrant pour assurer la plong\u00e9e.\n");
			sb.append("\n");
			sb.append("Cordialement.\n");
			this.email.setMsg(sb.toString());
			break;
		case MAIL_LISTE_ATTENTE_EXIST: 
			this.email.setSubject("Gestion de file d'attente : "+dateAffichee+" ("+plongee.getType()+")");
			sb = new StringBuffer("Bonjour,\n");
			sb.append("Un nouveau plongeur vient d'\u00eatre inscrit en liste d'attente \n");
			sb.append("\u00e0 la plong\u00e9e du "+dateAffichee+" ("+plongee.getType()+")\n");
			sb.append("\n");
			sb.append("Votre intervention est requise pour g\u00e9rer cette file d'attente.\n");
			sb.append("\n");
			sb.append("Cordialement.\n");
			this.email.setMsg(sb.toString());
			break;
		default:
			//???
			break;
		}
	}
}
