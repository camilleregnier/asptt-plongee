package com.asptt.plongee.resa.ui.web.wicket.page;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.PageParameters;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxLink;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.StringResourceModel;

import com.asptt.plongee.resa.exception.ResaException;
import com.asptt.plongee.resa.exception.TechnicalException;
import com.asptt.plongee.resa.model.Adherent;
import com.asptt.plongee.resa.model.Message;
import com.asptt.plongee.resa.model.MessageDataProvider;
import com.asptt.plongee.resa.model.Plongee;
import com.asptt.plongee.resa.model.PlongeeDataProvider;
//import com.asptt.plongee.resa.util.PlongeeMail;
import com.asptt.plongee.resa.ui.web.wicket.ResaSession;
import com.asptt.plongee.resa.util.CatalogueMessages;

@AuthorizeInstantiation({"USER","ADMIN","SECRETARIAT"})
public class AccueilPage extends TemplatePage {
	
	public AccueilPage() { 
		setPageTitle("Accueil");
		Adherent adh = getResaSession().getAdherent();
		
		// Si l'adhérent est identifié mais qu'il n'a pas changé son password (password = licence)
		if (getResaSession().getAdherent().getNumeroLicense().equalsIgnoreCase(getResaSession().getAdherent().getPassword())){
			setResponsePage(ModifPasswordPage.class);
		}
		
		IModel<Adherent> model = new Model<Adherent>(adh);
		add(new Label("hello",
	             new StringResourceModel(CatalogueMessages.ACCUEIL_BIENVENUE, this, model, 
	            		 new Object[]{new PropertyModel<Adherent>(model, "prenom"),calculerDateCourante()}
	             )
			)
		);
		
		try {
			List<Message> messages = getResaSession().getAdherentService().rechercherMessage();
			
			MessageDataProvider pDataProvider = new MessageDataProvider(messages);

			add(new DataView<Message>("simple", pDataProvider) {

				@Override
				protected void populateItem(final Item<Message> item) {
					final Message message = item.getModelObject();
					item.add(new Label("libelle", message.getLibelle()));
					
					item.add(new AttributeModifier("class", true,
							new AbstractReadOnlyModel<String>() {
								@Override
								public String getObject() {
									String cssClass;
									if (item.getIndex() % 2 == 1){
										cssClass = "even";
									} else {
										cssClass = "odd";
									}
									return cssClass;
								}
							}));
				}

			});
			//gestion du message pour le certificat medical perimé
			String msgCertificat ="";
			try {
				getResaSession().getPlongeeService().checkCertificatMedical(
						getResaSession().getAdherent(), null);
			} catch (ResaException e) {
				if(e.getKey().equalsIgnoreCase(CatalogueMessages.CM_PERIME)){
					StringResourceModel srm = new StringResourceModel(CatalogueMessages.CM_PERIME, this, null);
					msgCertificat=srm.getString();
				} else {
					String nbJour = e.getKey().substring(14);
					StringResourceModel srm = new StringResourceModel(CatalogueMessages.CM_A_RENOUVELER, this, model, 
						new Object[]{new PropertyModel<Adherent>(model, "prenom"),nbJour}
					);
					msgCertificat=srm.getString();
				}
			}
			add(new Label("certificat", msgCertificat));
			
			//gestion du message pour le cotisation non renouvellée
			try {
				getResaSession().getAdherentService().checkAnneeCotisation(getResaSession().getAdherent());
			} catch (ResaException e) {
				PageParameters pp = new PageParameters();
				StringResourceModel msgCotisation = new StringResourceModel(CatalogueMessages.ACCUEIL_COTISATION_PERIME,
						this, new Model<Adherent>(adh)); 
				pp.add("cotisation",msgCotisation.getString());
				setResponsePage(new LoginPage(pp));
			}


		}	catch (TechnicalException e) {
			e.printStackTrace();
			ErreurTechniquePage etp = new ErreurTechniquePage(e);
			setResponsePage(etp);
		}

	} 

	private String calculerDateCourante() {
		DateFormat sdf = new SimpleDateFormat("dd MMMM yyyy HH:mm:ss");
		return sdf.format(new Date());
	}

}
