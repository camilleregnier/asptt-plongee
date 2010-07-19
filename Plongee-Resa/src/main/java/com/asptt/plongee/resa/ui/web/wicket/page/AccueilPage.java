package com.asptt.plongee.resa.ui.web.wicket.page;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.wicket.MarkupContainer;
import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.basic.Label;

import com.asptt.plongee.resa.model.Adherent;

@AuthorizeInstantiation({"USER","ADMIN","SECRETARIAT"})
public class AccueilPage extends TemplatePage {
	
	MarkupContainer adminMenus = new MarkupContainer("menuAdmin"){  
		  
		  @Override  
		  public boolean isVisible() {     
		   return getResaSession().get().getRoles().hasRole("ADMIN");  
		  }  
		   
	};

	public AccueilPage() { 
		Adherent a = getResaSession().getAdherent();
	    add(new Label("hello", "Bienvenue:"+a.getPrenom()+", il est : " + calculerDateCourante()));
	    add(adminMenus);
	} 

	private String calculerDateCourante() {
		DateFormat sdf = new SimpleDateFormat("dd MMMM yyyy HH:mm:ss");
	
		return sdf.format(new Date());

	}
	

}
