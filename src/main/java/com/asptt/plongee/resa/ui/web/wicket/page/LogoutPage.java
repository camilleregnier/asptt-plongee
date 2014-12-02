package com.asptt.plongee.resa.ui.web.wicket.page;

import org.apache.log4j.Logger;
import org.apache.wicket.PageParameters;
import org.apache.wicket.Request;
import org.apache.wicket.Response;
import org.apache.wicket.Session;
import org.apache.wicket.authentication.pages.SignOutPage;
import org.apache.wicket.markup.html.basic.Label;

import com.asptt.plongee.resa.dao.jdbc.AdherentJdbcDao;
import com.asptt.plongee.resa.model.Adherent;
import com.asptt.plongee.resa.ui.web.wicket.ResaSession;

public class LogoutPage extends SignOutPage {

	private final Logger logger = Logger.getLogger(getClass());
	
	
	public LogoutPage() {
		super();
		ResaSession sess = (ResaSession) getSession();
		
		add(new Label("byebye", ""));
		if (null != sess.getAdherent()){
			logger.info("L'adherent "+sess.getAdherent().getNom()+" / "+sess.getAdherent().getPrenom()+" vient de se deconnecter");
		}
	}

}
