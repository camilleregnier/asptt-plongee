package com.asptt.plongee.resa.service;

import junit.framework.Assert;

import org.junit.Test;
import org.springframework.dao.support.DaoSupport;

import com.asptt.plongee.resa.dao.TechnicalException;
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
}
