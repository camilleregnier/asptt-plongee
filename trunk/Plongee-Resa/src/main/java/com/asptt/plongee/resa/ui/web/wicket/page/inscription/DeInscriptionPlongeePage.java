package com.asptt.plongee.resa.ui.web.wicket.page.inscription;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.mail.MessagingException;

import org.apache.commons.mail.Email;
import org.apache.commons.mail.SimpleEmail;
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
import com.asptt.plongee.resa.exception.ResaException;
import com.asptt.plongee.resa.exception.TechnicalException;
import com.asptt.plongee.resa.mail.PlongeeMail;
import com.asptt.plongee.resa.model.Adherent;
import com.asptt.plongee.resa.model.Plongee;
import com.asptt.plongee.resa.model.PlongeeDataProvider;
import com.asptt.plongee.resa.service.PlongeeService;
import com.asptt.plongee.resa.ui.web.wicket.page.ErreurTechniquePage;
import com.asptt.plongee.resa.ui.web.wicket.page.ErrorPage;
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
			
			//SI c'est un encadrant il faut verifier s'il en reste assez
			//et sinon envoyer un mail 
			if(adherent.getEncadrement() == null){	
				//Ce n'est pas un encadrant : on desinscrit
				getResaSession().getPlongeeService().deInscrireAdherent(
						plongee, 
						getResaSession().getAdherent());
				
				setResponsePage(new InscriptionConfirmationPlongeePage(plongee));
				//S'il y a des personnes en liste d'attente => mail
				if(getResaSession().getPlongeeService().rechercherListeAttente(plongee).size() > 0){
					//ENVOI d'un Mail
					Email eMail = new SimpleEmail();
					eMail.setSubject("gestion de file d'attente");
					eMail.setMsg("Des personnes sont en file d'attente sur la plongée du "+plongee.getDate().toString()+" de "+plongee.getType()+", et une place vient de se libérer");
					List<String> destis = new ArrayList<String>();
					destis.add("eric.simon28@orange.fr");
					destis.add("camille.regnier@gmail.com");
					
					PlongeeMail pMail = new PlongeeMail(eMail);
					pMail.sendMail(destis);
				}
				setResponsePage(new InscriptionConfirmationPlongeePage(plongee));
			} else {
				//C'est un encadrant : on regarde s'il en reste assez
				if(getResaSession().getPlongeeService().isEnoughEncadrant(plongee)){
					getResaSession().getPlongeeService().deInscrireAdherent(
							plongee, 
							getResaSession().getAdherent());
					//S'il y a des personnes en liste d'attente => mail
					if(getResaSession().getPlongeeService().rechercherListeAttente(plongee).size() > 0){
						//ENVOI d'un Mail
						Email eMail = new SimpleEmail();
						eMail.setSubject("gestion de file d'attente");
						eMail.setMsg("Des personnes sont en file d'attente sur la plongée du "+plongee.getDate().toString()+" de "+plongee.getType()+", et une place vient de se libérer");
						List<String> destis = new ArrayList<String>();
						destis.add("eric.simon28@orange.fr");
						destis.add("camille.regnier@gmail.com");
						
						PlongeeMail pMail = new PlongeeMail(eMail);
						pMail.sendMail(destis);
					}
					setResponsePage(new InscriptionConfirmationPlongeePage(plongee));
				} else {
					// Il en reste pas assez ; mail
					//TODO SI CONFIRMATION DESINSCRIRE + MAIL
					// On desinscrit
					getResaSession().getPlongeeService().deInscrireAdherent(
							plongee, 
							getResaSession().getAdherent());
					//Envoi du mail
					Email eMail = new SimpleEmail();
					eMail.setSubject("Encadrement de la plongée du : "+plongee.getDate().toString());
					eMail.setMsg("Un encadrant viens de se de-inscrire de la plongée du "+plongee.getDate().toString()+" de "+plongee.getType()+"\n" +
							"Il n'y a pas assez d'encadrant pour assurer la plongée");
					List<String> destis = new ArrayList<String>();
					destis.add("eric.simon28@orange.fr");
					destis.add("camille.regnier@gmail.com");

					PlongeeMail pMail = new PlongeeMail(eMail);
					pMail.sendMail(destis);
					setResponsePage(new InscriptionConfirmationPlongeePage(plongee));
				}
			}
		} catch (TechnicalException e) {
			e.printStackTrace();
			ErreurTechniquePage etp = new ErreurTechniquePage(e);
			setResponsePage(etp);
		}  catch (MessagingException e) {
			e.printStackTrace();
			ResaException re = new ResaException(e.getMessage());
			ErrorPage errPage = new ErrorPage(re);
			setResponsePage(errPage);
		} catch (ResaException e) {
			e.printStackTrace();
			ErrorPage errPage = new ErrorPage(e);
			setResponsePage(errPage);
		}
	}

}
