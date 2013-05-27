package com.asptt.plongee.resa.dao;


import com.asptt.plongee.resa.exception.TechnicalException;
import com.asptt.plongee.resa.model.Adherent;
import java.util.Date;
import junit.framework.Assert;
import org.apache.log4j.Logger;
import org.junit.Test;

public class AdherentDaoTest extends AbstractDaoTest {
	 private final Logger logger = Logger.getLogger(getClass());

    /**
     *
     * @throws TechnicalException
     */
    @Test
    public void testFindById() throws TechnicalException {
            // test de la fonction
            Adherent adh = adherentDao.findById("096042");

            Assert.assertNotNull(adh);
            Assert.assertTrue("eric".equalsIgnoreCase(adh.getPrenom()));

            // recherche d'un inconnu
            Adherent inconnu = adherentDao.findById("identifiant n'existant pas");
            Assert.assertNull(inconnu);
    }
	
	@Test
	public void testCreateKo() {
		Adherent adh = new Adherent();
		adh.setNumeroLicense("096042");
		adh.setNom("SIMON");
		adh.setPrenom("Eric");
		adh.setEnumNiveau(com.asptt.plongee.resa.model.NiveauAutonomie.P5);
		adh.setTelephone("0680327726");
		adh.setMail("eric.simon28@orange.fr");
		adh.setEnumEncadrement(null);
		adh.setPilote(false);
                adh.setDateCM(new Date());
		adh.setAnneeCotisation(new Integer("2013"));
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
		adh.setPilote(false);
                adh.setDateCM(new Date());
		adh.setAnneeCotisation(new Integer("2013"));
		
		try {
			adherentDao.create(adh);
			logger.info("On a bien une creation");
		} catch (TechnicalException e) {
                    Assert.assertNotNull("creation d'un adherent plantée : "+e.getMessage(), e);
			logger.info("creation d'un adherent plantée : "+e.getMessage());
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
		adh.setPilote(false);
		adh.setAnneeCotisation(new Integer("2013"));
		try {
			adherentDao.delete(adh);
			logger.info("On a bien une suppression");
		} catch (TechnicalException e) {
			Assert.fail("Suppression d'un adherent plantée");
			// La duplication a bien été controlée
		}
	}

}
