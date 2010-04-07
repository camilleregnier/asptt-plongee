package com.asptt.plongee.resa.service;

import junit.framework.Assert;

import org.junit.Test;

import com.asptt.plongee.resa.model.Adherent;

public class AdherentServiceTest extends AbstractServiceTest {

	@Test
	public void testRechercherAdherentParIdentifiant() {
		Adherent eric = adherentService.rechercherAdherentParIdentifiant("111111");
		
		Assert.assertNotNull(eric);
		Assert.assertTrue("gilbert".equalsIgnoreCase(eric.getPrenom()));

		// recherche d'un inconnu
		Adherent inconnu = adherentService.rechercherAdherentParIdentifiant("identifiant n'existant pas");
		Assert.assertNull(inconnu);
	}
}
