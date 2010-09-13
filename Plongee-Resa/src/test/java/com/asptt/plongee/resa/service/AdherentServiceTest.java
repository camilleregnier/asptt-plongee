package com.asptt.plongee.resa.service;

import java.util.ArrayList;
import java.util.List;

import javax.mail.MessagingException;

import junit.framework.Assert;

import org.junit.Test;
import org.springframework.dao.support.DaoSupport;

import com.asptt.plongee.resa.exception.ResaException;
import com.asptt.plongee.resa.exception.TechnicalException;
import com.asptt.plongee.resa.mail.PlongeeMail;
import com.asptt.plongee.resa.model.Adherent;

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
		PlongeeMail pMail;
		try {
			pMail = new PlongeeMail();
		
			pMail.setSubject("gestion de file d'attente");
			pMail.setMsg("Des personnes sont en file d'attente sur la plongée du BlaBla de NUIT, et une place vient de se libérer");
			List<String> destis = new ArrayList<String>();
			destis.add("eric.simon28@orange.fr");
			pMail.setDestis(destis);
			pMail.sendMail();
		
		} catch (ResaException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MessagingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
	}

}
