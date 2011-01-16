package com.asptt.plongee.resa.ui.web.wicket.page;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxLink;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.AbstractReadOnlyModel;

import com.asptt.plongee.resa.exception.ResaException;
import com.asptt.plongee.resa.exception.TechnicalException;
import com.asptt.plongee.resa.model.Adherent;
import com.asptt.plongee.resa.model.Message;
import com.asptt.plongee.resa.model.MessageDataProvider;
import com.asptt.plongee.resa.model.Plongee;
import com.asptt.plongee.resa.model.PlongeeDataProvider;
//import com.asptt.plongee.resa.util.PlongeeMail;
import com.asptt.plongee.resa.ui.web.wicket.ResaSession;

@AuthorizeInstantiation({"USER","ADMIN","SECRETARIAT"})
public class AccueilPage extends TemplatePage {
	
	public AccueilPage() { 

		Adherent adh = getResaSession().getAdherent();
		
		// Si l'adhérent est identifié mais qu'il n'a pas changé son password (password = licence)
		if (getResaSession().getAdherent().getNumeroLicense().equalsIgnoreCase(getResaSession().getAdherent().getPassword())){
			setResponsePage(ModifPasswordPage.class);
		}
		
		String libMesg = "Bienvenue:"+adh.getPrenom()+", nous sommes le : " + calculerDateCourante();
	   
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
			String libCM ="";
			try {
				getResaSession().getPlongeeService().checkCertificatMedical(
						getResaSession().getAdherent());
			} catch (ResaException e) {
				libCM=e.getKey();
			}
			
			add(new Label("hello", libMesg));
			add(new Label("certificat", libCM));

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
