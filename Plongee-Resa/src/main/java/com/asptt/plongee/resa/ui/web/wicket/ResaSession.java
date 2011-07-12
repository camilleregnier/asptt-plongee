package com.asptt.plongee.resa.ui.web.wicket;

import org.apache.wicket.Request;
import org.apache.wicket.Session;
import org.apache.wicket.authentication.AuthenticatedWebSession;
import org.apache.wicket.authorization.strategies.role.Roles;
import org.springframework.context.ApplicationContext;

import org.apache.log4j.Logger;

import com.asptt.plongee.resa.model.Adherent;
import com.asptt.plongee.resa.model.Plongee;
import com.asptt.plongee.resa.service.AdherentService;
import com.asptt.plongee.resa.service.PlongeeService;


public class ResaSession extends AuthenticatedWebSession {

	private static final long serialVersionUID = -807646178959365061L;

	private Adherent adherent;
	private Plongee plongee;
	private ApplicationContext ctx;
   
	private final Logger logger = Logger.getLogger(getClass().getName());

	public ResaSession(Request request, ApplicationContext ctx) {
		super(request);
		
		this.ctx = ctx;
	}
	
	public static ResaSession get() {
		return (ResaSession) Session.get();
		}
	
	public Adherent getAdherent() {
		return adherent;
	}

	public Plongee getPlongee() {
		return plongee;
	}

	public AdherentService getAdherentService() {
		return (AdherentService) ctx.getBean("adherentService");
	}
	
	public PlongeeService getPlongeeService() {
		return (PlongeeService) ctx.getBean("plongeeService");
	}
	
	public Roles getRoles() {
		if (isSignedIn()) {
			return adherent.getRoles();
		}
		return null;
	}

	public boolean authenticate(String username, String password) {
      logger.info("avant l'appel du service");
		// FIXME a changer des que la clé primaire de l'adhérent aura été choisie
		adherent = getAdherentService().authentifierAdherent(username, password);
      logger.info("après l'appel du service");
		if (null == adherent){
      logger.info("adherent null");
			return false;
		}else{
      logger.info("adherent non null");
			return true;
		}
	}
}
