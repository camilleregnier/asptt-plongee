package com.asptt.plongee.resa.service;

import java.util.ArrayList;
import java.util.List;

import javax.mail.MessagingException;

import junit.framework.Assert;

import org.apache.commons.mail.Email;
import org.apache.commons.mail.SimpleEmail;
import org.junit.Test;
import org.springframework.dao.support.DaoSupport;

import com.asptt.plongee.resa.exception.ResaException;
import com.asptt.plongee.resa.exception.TechnicalException;
import com.asptt.plongee.resa.mail.PlongeeMail;
import com.asptt.plongee.resa.model.Adherent;
import com.asptt.plongee.resa.model.Plongee;

public class AdherentServiceTest extends AbstractServiceTest {

	@Test
	public void testRechercherAdherentParIdentifiant() throws TechnicalException {
		// données pour le test
		Adherent ericGilbert = new Adherent();
		ericGilbert.setNumeroLicense("111111");
		ericGilbert.setPrenom("gilbert");
		//adherentDao.create(ericGilbert);
		
		// test de la fonction
		Adherent eric = adherentService.rechercherAdherentParIdentifiant("111111");
		
		Assert.assertNotNull(eric);
		Assert.assertTrue("gilbert".equalsIgnoreCase(eric.getPrenom()));

		// recherche d'un inconnu
		Adherent inconnu = adherentService.rechercherAdherentParIdentifiant("identifiant n'existant pas");
		Assert.assertNull(inconnu);
	}
	
	@Test
	public void creerExterne() throws TechnicalException {
		// données pour le test
		Adherent ext = new Adherent();
		ext.setNom("EXTERNE");
		ext.setPrenom("toto");
		ext.setNiveau("P1");
		ext.setTelephone("0491707070");
		ext.setMail("ext@ext.ext");
		adherentService.creerExterne(ext);
		
	}

	@Test
	public void envoyerMail() throws TechnicalException {
		// données pour le test
		try {
			Adherent adherent = adherentService.rechercherAdherentParIdentifiant("444444");
			Plongee plongee = plongeeService.rechercherPlongeeParId(26);
			
//			Email eMail = new SimpleEmail();
//			eMail.setSubject("gestion de file d'attente");
//			eMail.setMsg("Mille excuses...\n ca se voulait un test de liste de diffusion, mais ça n'aurait pas du partir...\n A bientot \n Eric");
			
//			List<String> destis = new ArrayList<String>();
//			destis.add("eric.simon28@orange.fr");
			
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
