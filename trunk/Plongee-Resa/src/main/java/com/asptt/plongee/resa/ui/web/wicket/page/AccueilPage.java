package com.asptt.plongee.resa.ui.web.wicket.page;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.basic.Label;

import com.asptt.plongee.resa.model.Adherent;
//import com.asptt.plongee.resa.util.PlongeeMail;
import com.asptt.plongee.resa.ui.web.wicket.ResaSession;

@AuthorizeInstantiation({"USER","ADMIN","SECRETARIAT"})
public class AccueilPage extends TemplatePage {
	
	public AccueilPage() { 

		Adherent a = getResaSession().getAdherent();
		
		// Si l'adhérent est identifié mais qu'il n'a pas changé son password (password = licence)
		if (getResaSession().getAdherent().getNumeroLicense().equalsIgnoreCase(getResaSession().getAdherent().getPassword())){
			setResponsePage(ModifPasswordPage.class);
		}
		
		add(new Label("hello", "Bienvenue:"+a.getPrenom()+", nous sommes le : " + calculerDateCourante()));
	   
	} 

	private String calculerDateCourante() {
		DateFormat sdf = new SimpleDateFormat("dd MMMM yyyy HH:mm:ss");
	
		return sdf.format(new Date());

	}
	

}
