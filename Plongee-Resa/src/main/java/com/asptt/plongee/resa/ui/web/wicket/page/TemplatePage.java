package com.asptt.plongee.resa.ui.web.wicket.page;

import org.apache.wicket.MarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.form.ImageButton;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.Model;

import com.asptt.plongee.resa.ui.web.wicket.ResaSession;
import com.asptt.plongee.resa.ui.web.wicket.page.consultation.ConsulterPlongees;

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
		//addConsultLink();
	}

	private void addConsultLink() {
		Link consultLink = new Link("lienConsult") {
			private static final long serialVersionUID = 1L;

			public void onClick() {
//				getSession().invalidate();
//				getRequestCycle().setRedirect(true);
				setResponsePage(ConsulterPlongees.class);
			}
		};
		add(consultLink);
	}
	

	public ResaSession getResaSession(){
		return (ResaSession) getSession();
	}
}
