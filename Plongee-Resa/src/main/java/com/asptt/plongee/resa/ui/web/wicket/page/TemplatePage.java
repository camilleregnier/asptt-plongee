package com.asptt.plongee.resa.ui.web.wicket.page;

import org.apache.wicket.MarkupContainer;
import org.apache.wicket.ResourceReference;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.ImageButton;
import org.apache.wicket.markup.html.image.resource.DefaultButtonImageResource;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.link.ResourceLink;
import org.apache.wicket.model.Model;

import com.asptt.plongee.resa.ui.web.wicket.ResaSession;
import com.asptt.plongee.resa.ui.web.wicket.page.admin.AnnulerPlongee;
import com.asptt.plongee.resa.ui.web.wicket.page.admin.CreerAdherent;
import com.asptt.plongee.resa.ui.web.wicket.page.admin.CreerPlongee;
import com.asptt.plongee.resa.ui.web.wicket.page.admin.GererAdherents;
import com.asptt.plongee.resa.ui.web.wicket.page.admin.GererListeAttenteOne;
import com.asptt.plongee.resa.ui.web.wicket.page.admin.GererPlongeeAOuvrirOne;
import com.asptt.plongee.resa.ui.web.wicket.page.consultation.ConsulterPlongees;
import com.asptt.plongee.resa.ui.web.wicket.page.inscription.DeInscriptionPlongeePage;
import com.asptt.plongee.resa.ui.web.wicket.page.inscription.InscriptionPlongeePage;
import com.asptt.plongee.resa.ui.web.wicket.page.inscription.secretariat.DesInscriptionPlongeePage;
import com.asptt.plongee.resa.ui.web.wicket.page.inscription.secretariat.InscriptionAdherentPlongeePage;
import com.asptt.plongee.resa.ui.web.wicket.page.inscription.secretariat.InscriptionExterieurPlongeePage;

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
		//menu ADHERENT
		userMenus.add(addConsultLink());
		userMenus.add(addReservLink());
		userMenus.add(addDeInscrireLink());
		add(userMenus);
		//menu ADMIN
		adminMenus.add(addCreerPlongeeLink());
		adminMenus.add(addAnnulerPlongeeLink());
		adminMenus.add(addOuvrirPlongeeLink());
		adminMenus.add(addListeAttentePlongeeLink());
		adminMenus.add(addCreerAdherentLink());
		adminMenus.add(addGererAdherentLink());
		add(adminMenus);
		//menu SECRETARIAT
		secretariatMenus.add(addInscrireAdherentLink());
		secretariatMenus.add(addInscrireExterneLink());
		secretariatMenus.add(addSecretDeInscrirePlongeeLink());
		secretariatMenus.add(addSecretConsultPlongeeLink());
		add(secretariatMenus);
	}

	private Link addConsultLink() {
		Link link = new Link("consultPlongee") {
		//DefaultButtonImageResource def = new DefaultButtonImageResource("Cancel"){
			private static final long serialVersionUID = 1L;

			public void onClick() {
				setResponsePage(ConsulterPlongees.class);
			}
		};
		add(link);
		return link;
	}
	
	private Link addReservLink() {
		Link link = new Link("reservPlongee") {
		//DefaultButtonImageResource def = new DefaultButtonImageResource("Cancel"){
			private static final long serialVersionUID = 1L;

			public void onClick() {
				setResponsePage(InscriptionPlongeePage.class);
			}
		};
		//add(link);
		return link;
	}
	
	private Link addDeInscrireLink() {
		Link link = new Link("deInscrirePlongee") {
		//DefaultButtonImageResource def = new DefaultButtonImageResource("Cancel"){
			private static final long serialVersionUID = 1L;

			public void onClick() {
				setResponsePage(DeInscriptionPlongeePage.class);
			}
		};
		//add(link);
		return link;
	}
	
	private Link addCreerPlongeeLink() {
		Link link = new Link("creerPlongee") {
		//DefaultButtonImageResource def = new DefaultButtonImageResource("Cancel"){
			private static final long serialVersionUID = 1L;

			public void onClick() {
				setResponsePage(CreerPlongee.class);
			}
		};
		//add(link);
		return link;
	}
	
	private Link addAnnulerPlongeeLink() {
		Link link = new Link("annulerPlongee") {
		//DefaultButtonImageResource def = new DefaultButtonImageResource("Cancel"){
			private static final long serialVersionUID = 1L;

			public void onClick() {
				setResponsePage(AnnulerPlongee.class);
			}
		};
		add(link);
		return link;
	}
	
	private Link addOuvrirPlongeeLink() {
		Link link = new Link("ouvrirPlongee") {
		//DefaultButtonImageResource def = new DefaultButtonImageResource("Cancel"){
			private static final long serialVersionUID = 1L;

			public void onClick() {
				setResponsePage(GererPlongeeAOuvrirOne.class);
			}
		};
		add(link);
		return link;
	}
	
	private Link addListeAttentePlongeeLink() {
		Link link = new Link("listeAttentePlongee") {
		//DefaultButtonImageResource def = new DefaultButtonImageResource("Cancel"){
			private static final long serialVersionUID = 1L;

			public void onClick() {
				setResponsePage(GererListeAttenteOne.class);
			}
		};
		add(link);
		return link;
	}
	
	private Link addCreerAdherentLink() {
		Link link = new Link("creerAdherent") {
		//DefaultButtonImageResource def = new DefaultButtonImageResource("Cancel"){
			private static final long serialVersionUID = 1L;

			public void onClick() {
				setResponsePage(CreerAdherent.class);
			}
		};
		add(link);
		return link;
	}
	
	private Link addGererAdherentLink() {
		Link link = new Link("gererAdherent") {
		//DefaultButtonImageResource def = new DefaultButtonImageResource("Cancel"){
			private static final long serialVersionUID = 1L;

			public void onClick() {
				setResponsePage(GererAdherents.class);
			}
		};
		add(link);
		return link;
	}
	
	private Link addInscrireAdherentLink() {
		Link link = new Link("inscrireAdherent") {
		//DefaultButtonImageResource def = new DefaultButtonImageResource("Cancel"){
			private static final long serialVersionUID = 1L;

			public void onClick() {
				setResponsePage(InscriptionAdherentPlongeePage.class);
			}
		};
		add(link);
		return link;
	}
	
	private Link addInscrireExterneLink() {
		Link link = new Link("inscrireExterne") {
		//DefaultButtonImageResource def = new DefaultButtonImageResource("Cancel"){
			private static final long serialVersionUID = 1L;

			public void onClick() {
				setResponsePage(InscriptionExterieurPlongeePage.class);
			}
		};
		add(link);
		return link;
	}
	
	private Link addSecretDeInscrirePlongeeLink() {
		Link link = new Link("secretDeInscrirePlongee") {
		//DefaultButtonImageResource def = new DefaultButtonImageResource("Cancel"){
			private static final long serialVersionUID = 1L;

			public void onClick() {
				setResponsePage(DesInscriptionPlongeePage.class);
			}
		};
		add(link);
		return link;
	}
	
	private Link addSecretConsultPlongeeLink() {
		Link link = new Link("secretConsultPlongee") {
		//DefaultButtonImageResource def = new DefaultButtonImageResource("Cancel"){
			private static final long serialVersionUID = 1L;

			public void onClick() {
				setResponsePage(ConsulterPlongees.class);
			}
		};
		add(link);
		return link;
	}
	
	public ResaSession getResaSession(){
		return (ResaSession) getSession();
	}
}
