package com.asptt.plongee.resa.service.impl;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.TreeSet;

import javax.mail.MessagingException;

import com.asptt.plongee.resa.dao.AdherentDao;
import com.asptt.plongee.resa.exception.ResaException;
import com.asptt.plongee.resa.exception.TechnicalException;
import com.asptt.plongee.resa.mail.PlongeeMail;
import com.asptt.plongee.resa.model.Adherent;
import com.asptt.plongee.resa.model.AdherentComparatorNom;
import com.asptt.plongee.resa.model.ContactUrgent;
import com.asptt.plongee.resa.model.Message;
import com.asptt.plongee.resa.model.Plongee;
import com.asptt.plongee.resa.model.ResaConstants;
import com.asptt.plongee.resa.service.AdherentService;
import com.asptt.plongee.resa.util.CatalogueMessages;
import com.asptt.plongee.resa.util.Parameters;
import com.asptt.plongee.resa.util.ResaUtil;

public class AdherentServiceImpl implements AdherentService, Serializable {

	private static final long serialVersionUID = -8552502001819883624L;
	private AdherentDao adherentDao;

	public void setAdherentDao(AdherentDao adherentDao) {
		this.adherentDao = adherentDao;
	}

	@Override
	public void supprimerAdherent(Adherent adherent) throws TechnicalException{
		adherentDao.delete(adherent);
	}

	@Override
	public void reinitPwdAdherent(Adherent adherent) throws TechnicalException{
		adherentDao.initPwd(adherent);
	}

        @Override
	public Adherent authentifierAdherent(String id, String pwd) throws TechnicalException{
		return adherentDao.authenticateAdherent(id, pwd);
	}
	
	@Override
	public Adherent rechercherAdherentParIdentifiant(String id) throws TechnicalException{
			return adherentDao.findById(id);
	}
	
	@Override
	public Adherent rechercherAdherentParIdentifiantTous(String id) throws TechnicalException{
			return adherentDao.findByIdAll(id);
	}

	@Override
	public Adherent rechercherParrainParIdentifiantFilleul(String licenseFilleul, int idPlongee) throws TechnicalException{
		return adherentDao.getParrainById(licenseFilleul, idPlongee);
	}

	@Override
	public List<Adherent> rechercherPlongeurs() throws TechnicalException{
			return adherentDao.findAll();
	}

	@Override
	public List<Adherent> rechercherAdherentsTous() throws TechnicalException{
			return adherentDao.getAdherentsTous();
	}

	@Override
	public List<Adherent> rechercherAdherentsActifs() throws TechnicalException{
			return adherentDao.getAdherentsActifs();
	}

	@Override
	public List<Adherent> rechercherAdherentsInactifs() throws TechnicalException{
			return adherentDao.getAdherentsInactifs();
	}

	@Override
	public List<Adherent> rechercherExternes() throws TechnicalException{
			return adherentDao.getExternes();
	}

	/**
	 * Cette methode est appelée uniquement par l'AdherentDataProvider
	 * dans le seul cas ou il faut faire une recherche : normalement jamais!
	 */
	@Override
	public List<Adherent> rechercherAdherents(int first, int count) throws TechnicalException{
		TreeSet tAdh = new TreeSet(new AdherentComparatorNom());
		tAdh.addAll(rechercherAdherentsTous());
		
		List<Adherent> adhTrie = new ArrayList<Adherent>();
		adhTrie.addAll(tAdh);
		List<Adherent> sousAdhTrie = new ArrayList<Adherent>();
		for (int i = 0 ; i < count ; i++){
			if (first+i < adhTrie.size()){
				sousAdhTrie.add(adhTrie.get(first+i));
			}
		}
		return sousAdhTrie;
	}

	@Override
	public List<Adherent> rechercherDPs(List<Adherent> adherents) throws TechnicalException{
		List<Adherent> dps = new ArrayList<Adherent>();
		for(Adherent a : adherents){
			if(a.isDp()){
				dps.add(a);
			}
		}
		return dps;
	}

	@Override
	public List<Adherent> rechercherDPsNonInscrits(List<Adherent> adherents, Plongee plongee) throws TechnicalException{
		List<Adherent> dps = new ArrayList<Adherent>();
		HashMap<String,Adherent> dpInscrits = new HashMap<String,Adherent>();
		
		
		for(Adherent a : plongee.getParticipants()){
			if(a.isDp()){
				dpInscrits.put(a.getNumeroLicense(),a);
			}
		}
		
		for(Adherent a : adherents){
			if(a.isDp() && ! dpInscrits.containsKey(a.getNumeroLicense())){
				long nbJours = ResaUtil.checkDateCM(a.getDateCM(), plongee.getDate());
				if( nbJours > 0){
					dps.add(a);
				}
			}
		}
		return dps;
	}

	@Override
	public List<Adherent> rechercherPilotes(List<Adherent> adherents) throws TechnicalException{
		List<Adherent> pilotes = new ArrayList<Adherent>();
		for(Adherent a : adherents){
			if(a.isPilote()){
				pilotes.add(a);
			}
		}
		return pilotes;
	}

	@Override
	public List<Adherent> rechercherPilotesNonInscrits(List<Adherent> adherents, Plongee plongee) throws TechnicalException{
		List<Adherent> pilotes = new ArrayList<Adherent>();
		HashMap<String,Adherent> pilotesInscrits = new HashMap<String,Adherent>();
		
		for(Adherent a : plongee.getParticipants()){
			if(a.isPilote()){
				pilotesInscrits.put(a.getNumeroLicense(),a);
			}
		}
		
		for(Adherent a : adherents){
			if(a.isPilote() && ! pilotesInscrits.containsKey(a.getNumeroLicense())){
				long nbJours = ResaUtil.checkDateCM(a.getDateCM(), plongee.getDate());
				if(nbJours > 0){
					pilotes.add(a);
				}
			}
		}
		return pilotes;
	}

	/**
	 * Verifie si l"année de cotisation est correcte
	 * @param Adherent adherent
	 * si nbMois =-1 => perimé
	 * si nbMois = 0 => Warning : dans le derniers mois 
	 * si nbMois = 1 => Warning : mois-1 avec le nombre de jour
	 * si nbMois = 2 => No soucie 
	 *  
	 */
	@Override
	public void checkAnneeCotisation(Adherent adherent) throws TechnicalException, ResaException{
		
		//la date du jour
		Date dateDuJour = new Date();
		//construction d'une date au 7/10/<annee de cotisation de l'adherent>
		Date dateCotisation = new Date();
		GregorianCalendar gc = new GregorianCalendar(adherent.getAnneeCotisation(), 9, 7);
		dateCotisation.setTime(gc.getTimeInMillis());
		
		
		if (dateDuJour.after(dateCotisation)){
			//A cette date la cotisation aurait du etre versée
			throw new ResaException(CatalogueMessages.ACCUEIL_COTISATION_PERIME);
		}
	}
	
	
	@Override
	public List<Adherent> rechercherAdherentsRole(String role) throws TechnicalException{
			return adherentDao.getAdherentsLikeRole(role);
	}
	
	@Override
	public void creerAdherent(Adherent adherent) throws TechnicalException{
        // On met le nom en majuscule et la premiere lettre du prenom
        String nom = adherent.getNom().toUpperCase();
        adherent.setNom(nom);
        String prenom = ResaUtil.capitalizeFirstLetter(adherent.getPrenom());
        adherent.setPrenom(prenom);
		adherentDao.create(adherent);
	}
	
	@Override
	public void updateAdherent(Adherent adherent, int typeMail) throws TechnicalException, ResaException{
		
		adherentDao.update(adherent);	
		if (typeMail == PlongeeMail.MAIL_MODIF_INFO_ADHERENT){
			PlongeeMail pMail;
			try {
				pMail = new PlongeeMail(PlongeeMail.MAIL_MODIF_INFO_ADHERENT,
						null, adherent);
				pMail.sendMail("ADMIN");
			} catch (MessagingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public void modifierExterne(Adherent externe) throws TechnicalException{
		
		adherentDao.updateExterne(externe);	
	}
	
	@Override
	public void updatePasswordAdherent(Adherent adherent) throws TechnicalException{
			adherentDao.updatePassword(adherent);
	}
	
	@Override
	public void creerExterne(Adherent adherent) throws TechnicalException{
            
            //Recherche si existe déjà
            List<Adherent> adherents = rechercherExternes();
            for(Adherent a : adherents){
                if(a.getNom().equalsIgnoreCase(adherent.getNom())
                        && a.getPrenom().equalsIgnoreCase(adherent.getPrenom())
                        ){
                    throw new TechnicalException("Cet externe existe déjà");
                }
            }
            
            // On met le nom en majuscule et la premiere lettre du prenom
            String nom = adherent.getNom().toUpperCase();
            adherent.setNom(nom);
            String prenom = ResaUtil.capitalizeFirstLetter(adherent.getPrenom());
            adherent.setPrenom(prenom);
            // Le Certificat Medical est forcé au 01/01/2030 pour eviter d'etre bloqué à l'inscription
            // L'exterieur doit apporté son CM avec lui
            Date dateDuJour = new Date();
            Calendar cal = Calendar.getInstance();
            cal.setTime(dateDuJour);
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            Date dateCM = new Date();
            try {
                    dateCM = sdf.parse("01/01/2030");
            } catch (ParseException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
            }
            // Recupere l'id Externe dans la table des sequences_ext
            Integer numExt = adherentDao.getIdExternes();
            adherent.setNumeroLicense(ResaConstants.LICENSE_EXTERNE.concat(numExt.toString()));
            adherent.setActifInt(2);
            adherent.setPilote(false);
            adherent.setTiv(false);
            adherent.setDp(false);
            adherent.setDateCM(dateCM);
            adherent.setAnneeCotisation(cal.get(Calendar.YEAR));
            adherentDao.create(adherent);
	}

	@Override
	public List<Message> rechercherMessagesTous() throws TechnicalException{
		return adherentDao.getAllMessages();
	}

	@Override
	public List<Message> rechercherMessage() throws TechnicalException{
		return adherentDao.getMessage();
	}

	@Override
	public Message updateMessage(Message message) throws TechnicalException{
		return adherentDao.updateMessage(message);
	}

	@Override
	public Message createMessage(Message message) throws TechnicalException{
		return adherentDao.createMessage(message);
	}

	@Override
	public void deleteMessage(Message message) throws TechnicalException{
		adherentDao.deleteMessage(message);
	}

	@Override
	public void modifContact(ContactUrgent contact) throws TechnicalException{
		adherentDao.updateContact(contact);
	}
	
	@Override
	public void creerContact(ContactUrgent contact, Adherent adherent) throws TechnicalException{
		adherentDao.createContact(contact, adherent);
	}
	
	@Override
	public void suppContact(ContactUrgent contact, Adherent adherent) throws TechnicalException{
		adherentDao.deleteContact(contact, adherent);
	}

	@Override
	public List<Adherent> rechercherAdherentInscrits(Plongee plongee) {
		return adherentDao.getAdherentsInscrits(plongee, null, null, null);
	}
}
