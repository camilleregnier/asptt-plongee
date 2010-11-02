package com.asptt.plongee.resa.ui.web.wicket.page;

import org.apache.wicket.PageParameters;
import org.apache.wicket.authentication.pages.SignOutPage;
import org.apache.wicket.markup.html.basic.Label;

import com.asptt.plongee.resa.model.Adherent;

public class LogoutPage extends SignOutPage {

	public LogoutPage(Adherent a, String message) {
		super();
		String libelle = "";
		if(message.equalsIgnoreCase("toLate")){
			libelle = "D\u00e9sol\u00e9 : "+a.getPrenom()+", mais il est pas encore 9h00 ....";
		} else {
			libelle = "Au revoir : "+a.getPrenom()+".";
		}
		add(new Label("byebye", libelle));
		// TODO Auto-generated constructor stub
	}

	public LogoutPage() {
		super();
		add(new Label("byebye", ""));
	}

}
