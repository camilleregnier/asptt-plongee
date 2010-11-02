package com.asptt.plongee.resa.ui.web.wicket.page;

import org.apache.wicket.markup.html.basic.Label;

import com.asptt.plongee.resa.exception.ResaException;
import com.asptt.plongee.resa.model.Adherent;

public class ErrorPage extends TemplatePage {
	
	public ErrorPage(ResaException re) {
		Adherent a = getResaSession().getAdherent();
	    add(new Label("hello","MESSAGE : D\u00e9sol\u00e9 "+a.getPrenom()+" mais : "+re.getKey()));
	}

}
