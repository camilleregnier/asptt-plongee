package com.asptt.plongee.resa.ui.web.wicket;

import org.apache.wicket.Request;
import org.apache.wicket.authentication.AuthenticatedWebSession;
import org.apache.wicket.authorization.strategies.role.Roles;
import org.springframework.context.ApplicationContext;

import com.asptt.plongee.resa.model.Adherent;
import com.asptt.plongee.resa.service.AdherentService;


public class ResaSession extends AuthenticatedWebSession {

	private static final long serialVersionUID = -807646178959365061L;

	private Adherent adherent;
	private ApplicationContext ctx;


	public ResaSession(Request request, ApplicationContext ctx) {
		super(request);
		
		this.ctx = ctx;
	}
	
	public Adherent getAdherent() {
		return adherent;
	}

	public AdherentService getAdherentService() {
		return (AdherentService) ctx.getBean("adherentService");
	}
	
	public Roles getRoles() {
		if (isSignedIn()) { // return profileRoles.get(user.getDroits());
			return new Roles("USER");
		}
		return null;
	}

	public boolean authenticate(String username, String password) {
		// FIXME a changer des que la clé primaire de l'adhérent aura été choisie
		adherent = getAdherentService().rechercherAdherentParIdentifiant(password);
		return (adherent != null);
	}
}
