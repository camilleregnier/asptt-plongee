package com.asptt.plongee.resa.dao;


import junit.framework.Assert;

import org.junit.Test;

import com.asptt.plongee.resa.model.Adherent;

public class AdherentDaoTest extends AbstractDaoTest {

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
	public void testCreate() {
		Adherent adh = new Adherent();
		adh.setNumeroLicense("111111");
		adh.setNom("DICOSTANZO");
		adh.setPrenom("Gilbert");
		adh.setNiveau(com.asptt.plongee.resa.model.NiveauAutonomie.P5);
		adh.setTelephone("0491111111");
		adh.setMail("gilbert.costanzo@orange.fr");
		adh.setEncadrement(Adherent.Encadrement.E4);
		adh.setPilote(true);
		
		try {
			adherentDao.create(adh);
			Assert.fail("duplication des adhérents non controlée");
		} catch (TechnicalException e) {
			// La duplication a bien été controlée
		}
	}
}
