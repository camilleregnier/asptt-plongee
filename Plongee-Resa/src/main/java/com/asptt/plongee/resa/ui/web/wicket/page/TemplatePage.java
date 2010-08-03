package com.asptt.plongee.resa.ui.web.wicket.page;

import org.apache.wicket.MarkupContainer;
import org.apache.wicket.markup.html.WebPage;

import com.asptt.plongee.resa.ui.web.wicket.ResaSession;

public abstract class TemplatePage extends WebPage {
	MarkupContainer userMenus = new MarkupContainer("menuUser"){  
		  
		  @Override  
		  public boolean isVisible() {     
		   return getResaSession().get().getRoles().hasRole("USER");  
		  }  
		   
	}; 
	
	MarkupContainer secretariatMenus = new MarkupContainer("menuSecretariat"){  
		  
		  @Override  
		  public boolean isVisible() {     
		   return getResaSession().get().getRoles().hasRole("SECRETARIAT");  
		  }  
		   
	};
	
	MarkupContainer adminMenus = new MarkupContainer("menuAdmin"){  
		  
		  @Override  
		  public boolean isVisible() {     
		   return getResaSession().get().getRoles().hasRole("ADMIN");  
		  }  
		   
	};

	public TemplatePage() {
		super();
		add(userMenus);
		add(secretariatMenus);
		add(adminMenus);
	}

	public ResaSession getResaSession(){
		return (ResaSession) getSession();
	}
}
