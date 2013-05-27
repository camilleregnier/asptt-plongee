package com.asptt.plongee.resa.dao;


import com.asptt.plongee.resa.exception.TechnicalException;
import com.asptt.plongee.resa.model.Adherent;
import com.asptt.plongee.resa.model.Plongee;
import junit.framework.Assert;
import org.apache.log4j.Logger;
import org.junit.Test;

public class PlongeeDaoTest extends AbstractDaoTest {

	private static final Logger logger = Logger.getLogger(PlongeeDaoTest.class);
        
	@Test
	public void testInscrireAdherentToPlongee() {
//		Adherent adh = new Adherent();
//		adh.setNumeroLicense("111111");
//		
//		Plongee pl = new Plongee();
//		pl.setId(4);
//		try {
//			plongeeDao.inscrireAdherentPlongee(pl, adh);
//			logger.info("On a bien inscrit : "+adh.getNumeroLicense()+" a la plongee : "+pl.getId()+".");
//		} catch (TechnicalException e) {
//			Assert.fail("inscription d'un adherent plantée");
//			// La duplication a bien été controlée
//		}
	}

	@Test
	public void testDeInscrireAdherentToAttente() {
//		Adherent adh = new Adherent();
//		adh.setNumeroLicense("222222");
//		
//		Plongee pl = new Plongee();
//		pl.setId(6);
//		try {
//			plongeeDao.sortirAdherentAttente(pl, adh);
//			logger.info("On a bien supprimé : "+adh.getNumeroLicense()+" a de la liste d'attente de la plongee : "+pl.getId()+".");
//		} catch (TechnicalException e) {
//			Assert.fail("suppression liste attente d'un adherent plantée");
//			// La duplication a bien été controlée
//		}
	}
}
