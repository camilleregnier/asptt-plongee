package com.asptt.plongee.resa.dao;


import junit.framework.Assert;

import org.apache.log4j.Logger;
import org.junit.Test;

import com.asptt.plongee.resa.exception.TechnicalException;
import com.asptt.plongee.resa.model.Adherent;
import com.asptt.plongee.resa.quartz.job.QuartzJob;

public class AdherentDaoTest extends AbstractDaoTest {
	 private final Logger logger = Logger.getLogger(getClass());

	@Test
	public void testFindById() throws TechnicalException {
		// test de la fonction
		Adherent adh = adherentDao.findById("111111");
		
		Assert.assertNotNull(adh);
		Assert.assertTrue("gilbert".equalsIgnoreCase(adh.getPrenom()));

		// recherche d'un inconnu
		Adherent inconnu = adherentDao.findById("identifiant n'existant pas");
		Assert.assertNull(inconnu);
	}
	
	@Test
	public void testCreateKo() {
		Adherent adh = new Adherent();
		adh.setNumeroLicense("111111");
		adh.setNom("DICOSTANZO");
		adh.setPrenom("Gilbert");
		adh.setEnumNiveau(com.asptt.plongee.resa.model.NiveauAutonomie.P5);
		adh.setTelephone("0491111111");
		adh.setMail("gilbert.costanzo@orange.fr");
		adh.setEnumEncadrement(Adherent.Encadrement.E4);
		adh.setPilote(true);
		
		try {
			adherentDao.create(adh);
			Assert.fail("duplication des adhérents non controlée");
		} catch (TechnicalException e) {
			logger.info("On a bien une exception");
			// La duplication a bien été controlée
		}
	}

	@Test
	public void testCreateOk() {
		Adherent adh = new Adherent();
		adh.setNumeroLicense("123456");
		adh.setNom("NomTEST");
		adh.setPrenom("PrenomTEST");
		adh.setEnumNiveau(com.asptt.plongee.resa.model.NiveauAutonomie.P3);
		adh.setTelephone("1111111111");
		adh.setMail("titi.toto@orange.fr");
		adh.setEncadrement(null);
		adh.setPilote(false);
		
		try {
			adherentDao.create(adh);
			logger.info("On a bien une creation");
		} catch (TechnicalException e) {
			Assert.fail("creation d'un adherent plantée");
			// La duplication a bien été controlée
		}
	}

	@Test
	public void testDelete() {
		Adherent adh = new Adherent();
		adh.setNumeroLicense("123456");
		adh.setNom("NomTEST");
		adh.setPrenom("PrenomTEST");
		adh.setEnumNiveau(com.asptt.plongee.resa.model.NiveauAutonomie.P3);
		adh.setTelephone("1111111111");
		adh.setMail("titi.toto@orange.fr");
		adh.setEncadrement(null);
		adh.setPilote(false);
		try {
			adherentDao.delete(adh);
			logger.info("On a bien une suppression");
		} catch (TechnicalException e) {
			Assert.fail("Suppression d'un adherent plantée");
			// La duplication a bien été controlée
		}
	}

}
