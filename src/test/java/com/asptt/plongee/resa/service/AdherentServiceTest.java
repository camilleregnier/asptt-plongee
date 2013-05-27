package com.asptt.plongee.resa.service;

import com.asptt.plongee.resa.exception.ResaException;
import com.asptt.plongee.resa.exception.TechnicalException;
import com.asptt.plongee.resa.mail.PlongeeMail;
import com.asptt.plongee.resa.model.Adherent;
import com.asptt.plongee.resa.model.ContactUrgent;
import com.asptt.plongee.resa.model.Plongee;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.mail.MessagingException;
import junit.framework.Assert;
import org.apache.log4j.Logger;
import org.junit.Test;

public class AdherentServiceTest extends AbstractServiceTest {
    private final Logger logger = Logger.getLogger(getClass());
	@Test
	public void testRechercherAdherentParIdentifiant() throws TechnicalException {
		
		// test de la fonction
		Adherent ericSearch = adherentService.rechercherAdherentParIdentifiant("096042");
		
		Assert.assertNotNull(ericSearch);
		Assert.assertTrue("eric".equalsIgnoreCase(ericSearch.getPrenom()));

		// recherche d'un inconnu
		Adherent inconnu = adherentService.rechercherAdherentParIdentifiant("identifiant n'existant pas");
		Assert.assertNull(inconnu);
	}
	
	@Test
	public void creerExterne() {
		// données pour le test
		Adherent ext = new Adherent();
		ext.setNom("EXTERNE");
		ext.setPrenom("toto");
		ext.setNiveau("P1");
		ext.setTelephone("0491707070");
		ext.setMail("ext@ext.ext");
                ext.setCommentaire("");
                try{
                    adherentService.creerExterne(ext);
                } catch (TechnicalException t){
                    logger.info("Impossible de creer l'externe : "+t.getMessage());
                }
		
	}

	@Test
	public void creerAdherent() {
		// données pour le test
		Adherent adh = new Adherent();
		adh.setNumeroLicense("989898");
		adh.setPassword("989898");
		adh.setAnneeCotisation(2011);
		adh.setNom("NOM_TEST");
		adh.setPrenom("PRENOM_TEST");
		adh.setEnumNiveau(com.asptt.plongee.resa.model.NiveauAutonomie.P3);
		adh.setTelephone("0491707070");
		adh.setMail("titi.toto@orange.fr");
		adh.setEnumEncadrement(null);
		adh.setPilote(false);
		Date dateCM = new Date();
		adh.setDateCM(dateCM);
                adh.setCommentaire("");
		
		List<String> roles = new ArrayList<String>();
		roles.add("ADMIN");
		roles.add("USER");
		adh.setRoles(roles);

		
		List<ContactUrgent> contacts = new ArrayList<ContactUrgent>();
		ContactUrgent contact = new ContactUrgent();
		contact.setTitre("Mr");
		contact.setNom("Nom Contact1");
		contact.setPrenom("Prenom Contact1");
		contact.setTelephone("1111111111");
		contact.setTelephtwo("2222222222");
		contacts.add(contact);
		contact = new ContactUrgent();
		contact.setTitre("Mme");
		contact.setNom("Nom Contact2");
		contact.setPrenom("Prenom Contact2");
		contact.setTelephone("9999999999");
		contact.setTelephtwo("8888888888");
		contacts.add(contact);
		adh.setContacts(contacts);
                try{
		adherentService.creerAdherent(adh);
                } catch (TechnicalException t){
                    logger.info("Impossible de creer l'adhrent : "+t.getMessage());
                }
		
	}

	@Test
	public void updateAdherent() throws TechnicalException {
		// données pour le test
		Adherent adh = new Adherent();
		adh.setNumeroLicense("989898");
		adh.setPassword("989898");
		adh.setAnneeCotisation(2011);
		adh.setNom("NOM_TEST");
		adh.setPrenom("PRENOM_TEST");
		adh.setEnumNiveau(com.asptt.plongee.resa.model.NiveauAutonomie.P3);
		adh.setTelephone("0491707070");
		adh.setMail("titi.toto@orange.fr");
		adh.setEnumEncadrement(null);
		adh.setPilote(false);
		Date dateCM = new Date();
		adh.setDateCM(dateCM);
                adh.setCommentaire("");
		
		List<String> roles = new ArrayList<String>();
		roles.add("ADMIN");
		roles.add("USER");
		adh.setRoles(roles);

		List<ContactUrgent> contacts = new ArrayList<ContactUrgent>();
		ContactUrgent contact = new ContactUrgent();
		contact.setTitre("Mr");
		contact.setNom("Nom Contact1 Update");
		contact.setPrenom("Prenom Contact1 Update");
		contact.setTelephone("7711111119");
		contact.setTelephtwo("7722222229");
		contacts.add(contact);
		contact = new ContactUrgent();
		contact.setTitre("Mme");
		contact.setNom("Nom Contact2 Update");
		contact.setPrenom("Prenom Contact2 Update");
		contact.setTelephone("7799999991");
		contact.setTelephtwo("7788888881");
		contacts.add(contact);
		adh.setContacts(contacts);
		try {
			adherentService.updateAdherent(adh, PlongeeMail.PAS_DE_MAIL);
		} catch (ResaException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

//	@Test
	public void envoyerMail() throws TechnicalException {
		// données pour le test
		try {
			Adherent adherent = adherentService.rechercherAdherentParIdentifiant("444444");
			Plongee plongee = plongeeService.rechercherPlongeeParId(26);
			
//			Email eMail = new SimpleEmail();
//			eMail.setSubject("gestion de file d'attente");
//			eMail.setMsg("Mille excuses...\n ca se voulait un test de liste de diffusion, mais ça n'aurait pas du partir...\n A bientot \n Eric");
			
//			List<String> destis = new ArrayList<String>();
//			destis.add("ericSearch.simon28@orange.fr");
			
			PlongeeMail pMail = new PlongeeMail(PlongeeMail.MAIL_INSCRIPTION_SUR_PLONGEE_FERMEE, plongee, adherent);
			pMail.sendMail("ADMIN");
		
		} catch (ResaException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MessagingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
	}

}
