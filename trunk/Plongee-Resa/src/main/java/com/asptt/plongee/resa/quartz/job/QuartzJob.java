package com.asptt.plongee.resa.quartz.job;

import com.asptt.plongee.resa.exception.TechnicalException;
import com.asptt.plongee.resa.quartz.manager.BusinessManager;

public class QuartzJob {
	 private BusinessManager businessManager;
	 public void execute() throws TechnicalException{
	  System.out.println("In quartz job, I call the business manager") ;
	  businessManager.runAction();
	 }
	 public void setBusinessManager(BusinessManager businessManager) {
	  this.businessManager = businessManager;
	 }
}

