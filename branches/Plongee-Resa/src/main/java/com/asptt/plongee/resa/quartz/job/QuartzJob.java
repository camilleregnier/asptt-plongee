package com.asptt.plongee.resa.quartz.job;

import org.apache.log4j.Logger;

import com.asptt.plongee.resa.dao.jdbc.AdherentJdbcDao;
import com.asptt.plongee.resa.exception.TechnicalException;
import com.asptt.plongee.resa.quartz.manager.BusinessManager;

public class QuartzJob {
	 private BusinessManager businessManager;
	 
	 private final Logger logger = Logger.getLogger(getClass());
	 
	 public void execute() throws TechnicalException{
	  logger.info("Classe QuartzJob : appel du businessManager.runAction()");
	  businessManager.runAction();
	 }
	 public void setBusinessManager(BusinessManager businessManager) {
	  this.businessManager = businessManager;
	 }
}

