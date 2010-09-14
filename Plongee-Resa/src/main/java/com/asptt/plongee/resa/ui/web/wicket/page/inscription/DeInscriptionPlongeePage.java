package com.asptt.plongee.resa.ui.web.wicket.page.inscription;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.datetime.markup.html.basic.DateLabel;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxLink;
import org.apache.wicket.feedback.IFeedback;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;

import com.asptt.plongee.resa.exception.MailException;
import com.asptt.plongee.resa.exception.TechnicalException;
import com.asptt.plongee.resa.mail.PlongeeMail;
import com.asptt.plongee.resa.model.Adherent;
import com.asptt.plongee.resa.model.Plongee;
import com.asptt.plongee.resa.model.PlongeeDataProvider;
import com.asptt.plongee.resa.service.PlongeeService;
import com.asptt.plongee.resa.ui.web.wicket.page.ErreurTechniquePage;
import com.asptt.plongee.resa.ui.web.wicket.page.TemplatePage;

public class DeInscriptionPlongeePage extends TemplatePage {

	private Adherent adh = null;
	private List<Plongee> plongees;
	
	public DeInscriptionPlongeePage() {

		this.adh = getResaSession().getAdherent(); 
		
		add(new Label("message", adh.getPrenom() + ", voici les plongées auxquelles tu es inscrit(e)"));
		
		final FeedbackPanel feedback = new FeedbackPanel("feedback");
		add(feedback);
		init();
	}

	private void init() {
		try{
			plongees = getResaSession().getPlongeeService().rechercherPlongeesAdherentInscrit( 	
					getResaSession().getAdherent(), 24);
		} catch (TechnicalException e) {
			e.printStackTrace();
			ErreurTechniquePage etp = new ErreurTechniquePage(e);
			setResponsePage(etp);
		}
		
		PlongeeDataProvider pDataProvider = new PlongeeDataProvider(plongees);

		add(new DataView<Plongee>("simple", pDataProvider) {
			protected void populateItem(final Item<Plongee> item) {
				Plongee plongee = item.getModelObject();
				String nomDP = "Aucun";
				if (null != plongee.getDp()) {
					nomDP = plongee.getDp().getNom();
				}

				item.add(new IndicatingAjaxLink("select") {
					@Override
					public void onClick(AjaxRequestTarget target) {
//						replaceModalWindow(target, item.getModel());
//						modal2.show(target);
						deInscrire(target, item.getModel());
					}
				});

				// Mise en forme de la date
				Calendar cal = Calendar.getInstance();
				cal.setTime(plongee.getDate());
				String dateAffichee = cal.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.FRANCE) + " ";
				dateAffichee = dateAffichee + cal.get(Calendar.DAY_OF_MONTH) + " ";
				dateAffichee = dateAffichee + cal.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.FRANCE) + " ";
				dateAffichee = dateAffichee + cal.get(Calendar.YEAR);
				
				item.add(new Label("date", dateAffichee));
				item.add(new Label("dp", nomDP));
				item.add(new Label("type", plongee.getType()));
				item.add(new Label("niveauMini", plongee.getNiveauMinimum().toString()));
				
				// Places restantes
				item.add(new Label("placesRestantes", getResaSession().getPlongeeService().getNbPlaceRestante(plongee).toString()));

				item.add(new AttributeModifier("class", true,
						new AbstractReadOnlyModel<String>() {
							@Override
							public String getObject() {
								return (item.getIndex() % 2 == 1) ? "even"
										: "odd";
							}
						}));
			}
		});
		
	}

	public void deInscrire(AjaxRequestTarget target, IModel<Plongee> iModel) {
		
		Plongee plongee = iModel.getObject();
		try {
			Adherent adherent = getResaSession().getAdherent();
			
			//S'il y a des personnes en liste d'attente => mail
			if(getResaSession().getPlongeeService().rechercherListeAttente(plongee).size() > 0){
				//ENVOI d'un Mail
				try {
					PlongeeMail pMail = new PlongeeMail();
//					pMail.sendMail();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			//SI c'est un encadrant il faut verifier s'il en reste assez
			//et sinon envoyer un mail 
			if(adherent.getEncadrement() == null){						
				getResaSession().getPlongeeService().deInscrireAdherent(
						plongee, 
						getResaSession().getAdherent());
				setResponsePage(new InscriptionConfirmationPlongeePage(plongee));
			} else {
				if(getResaSession().getPlongeeService().isEnoughEncadrant(plongee)){
					getResaSession().getPlongeeService().deInscrireAdherent(
							plongee, 
							getResaSession().getAdherent());
					setResponsePage(new InscriptionConfirmationPlongeePage(plongee));
				} else {
					//TODO SI CONFIRMATION DESINSCRIRE + MAIL
				}
				
			}
		} catch (TechnicalException e) {
			e.printStackTrace();
			ErreurTechniquePage etp = new ErreurTechniquePage(e);
			setResponsePage(etp);
		}
	}

}
