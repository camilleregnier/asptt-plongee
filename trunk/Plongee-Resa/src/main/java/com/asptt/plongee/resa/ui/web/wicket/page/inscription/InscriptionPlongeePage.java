package com.asptt.plongee.resa.ui.web.wicket.page.inscription;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.mail.MessagingException;

import org.apache.commons.mail.Email;
import org.apache.commons.mail.SimpleEmail;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.IAjaxCallDecorator;
import org.apache.wicket.ajax.calldecorator.AjaxCallDecorator;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxLink;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;

import com.asptt.plongee.resa.exception.ResaException;
import com.asptt.plongee.resa.exception.TechnicalException;
import com.asptt.plongee.resa.mail.PlongeeMail;
import com.asptt.plongee.resa.model.Adherent;
import com.asptt.plongee.resa.model.Plongee;
import com.asptt.plongee.resa.model.PlongeeDataProvider;
import com.asptt.plongee.resa.ui.web.wicket.component.ConfirmAjaxLink;
import com.asptt.plongee.resa.ui.web.wicket.page.TemplatePage;
import com.asptt.plongee.resa.ui.web.wicket.page.admin.GererPlongeeAOuvrirTwo;
import com.asptt.plongee.resa.util.Parameters;

public class InscriptionPlongeePage extends TemplatePage {
	
	private Adherent adh = null;
	private List<Plongee> plongees = null;
	private FeedbackPanel feedback = new FeedbackPanel("feedback");
	private int typeMail;
	
	private ModalWindow modalConfirm;
	
	
	public InscriptionPlongeePage(){
		this.adh = getResaSession().getAdherent(); 
		add(new Label("message", adh.getPrenom() + ", ci-dessous, les plong\u00e9es auxquelles tu peux t'inscrire"));
		String libCM ="";
		try {
			 getResaSession().getPlongeeService().checkCertificatMedical(adh, null);
		} catch (TechnicalException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ResaException e) {
			libCM=e.getKey();
		}
		add(new Label("certificat", libCM));
		feedback.setOutputMarkupId(true);
		add(feedback);
		init();
	}
	
	public InscriptionPlongeePage(Adherent adherent) {
		this.adh = adherent;
		String libMesg = adh.getPrenom()+" "+ adh.getNom() + " peut s'inscrire aux plong\u00e9es suivantes";
		String libCM ="";
		try {
			getResaSession().getPlongeeService().checkCertificatMedical(adh, null);
		} catch (TechnicalException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ResaException e) {
			libCM=e.getKey();
		}
		add(new Label("message", libMesg));
		add(new Label("certificat", libCM));
		feedback.setOutputMarkupId(true);
		add(feedback);
		init();
	}
	

	
	private void init() {

		try{
			// Initialisation de la fenêtre modal de confirmation d'annulation
			add(modalConfirm = new ModalWindow("modalConfirm"));
			//modalConfirm.setPageMapName("modal-2");

			modalConfirm.setTitle("Confirmation");

			modalConfirm.setResizable(false);
			modalConfirm.setInitialWidth(30);
			modalConfirm.setInitialHeight(15);
			modalConfirm.setWidthUnit("em");
			modalConfirm.setHeightUnit("em");

			modalConfirm.setCssClassName(ModalWindow.CSS_CLASS_BLUE);
			
			
			plongees = getResaSession().getPlongeeService().rechercherPlongeePourInscriptionAdherent(adh);
		} catch (TechnicalException e) {
			e.printStackTrace();
			error(e.getKey());
		}
		
		PlongeeDataProvider pDataProvider = new PlongeeDataProvider(plongees);

		add(new DataView<Plongee>("simple", pDataProvider) {

			private static final long serialVersionUID = 4247578422439877902L;

			protected void populateItem(final Item<Plongee> item) {
				Plongee plongee = item.getModelObject();
				String nomDP = "Aucun";
				if (null != plongee.getDp()) {
					nomDP = plongee.getDp().getNom();
				}
				
				item.add(new ConfirmAjaxLink("select","Es-tu s\u00fbr(e) de vouloir r\u00e9server cette plong\u00e9e ?") {
					private static final long serialVersionUID = 4442484995694176106L;

					@Override
					public void onClick(AjaxRequestTarget target) {
						inscrire(target, item.getModel());
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
						private static final long serialVersionUID = 5259097512265622750L;

						@Override
						public String getObject() {
							return (item.getIndex() % 2 == 1) ? "even"
									: "odd";
						}
					})
				);
			}
		});
		
	}

	public void inscrire(AjaxRequestTarget target, IModel<Plongee> iModel) {
		
		Plongee plongee = iModel.getObject();
		// Si l'adhérent est inscrit par le secrétariat
		// on ne prend pas l'adhérent en session
		try {
			//Verification de l'heure d'ouverture
			Date dateDuJour = new Date();
			Calendar cal = Calendar.getInstance();
			cal.setTime(dateDuJour);
			int heureCourante = cal.get(Calendar.HOUR_OF_DAY);
			int heureOuverture = getHeureOuverture(cal, adh);
			//test actif == 1 ==> ok c un adherent
			// sinon c un externe donc inscription par secretatiat
			if ( adh.isVesteRouge() && adh.getActifInt()==1 ){
				//C'est un encadrant un Dp ou un pilote
				//L'inscription est ouverte à tous moments
			} else {
				//On regarde l'heure avant de donner accès à l'inscription
				if( heureCourante < heureOuverture ){
					throw new ResaException("D\u00e9sol\u00e9 "+adh.getPrenom()+" mais l'inscription ouvre \u00e0 partir de : "+heureOuverture+" heures.");
				}
			}
			//Appel au service de verification des RGs pour inscrire le plongeur
			int response = getResaSession().getPlongeeService().isOkForResa(
					plongee, 
					adh != null ? adh : getResaSession().getAdherent());
			//Analyse le retour de service
			switch (response) {
			case 0://on inscrit l'adherent en liste d'attente sans envoi de mail : car plongee complete  
				typeMail=PlongeeMail.PAS_DE_MAIL;
				if(getResaSession().getPlongeeService().isOkForListeAttente(
						plongee, 
						getResaSession().getAdherent())){
					// On demande confirmation pour l'inscriptions en liste attente
					replaceModalWindow(target, plongee);
					modalConfirm.show(target);
				}
				break;
			case 4: //on inscrit l'adherent en liste d'attente avec envoi d'un mail : pas assez d'encadrant
				typeMail=PlongeeMail.MAIL_PAS_ASSEZ_ENCADRANT;
				if(getResaSession().getPlongeeService().isOkForListeAttente(
						plongee, 
						getResaSession().getAdherent())){
					
					// On demande confirmation pour l'inscription en liste d'attente
					replaceModalWindow(target, plongee);
					modalConfirm.show(target);
				}
				break;
			case 5: //on inscrit l'adherent en liste d'attente avec envoi d'un mail : Liste d'attente déjà ouverte
				typeMail=PlongeeMail.MAIL_LISTE_ATTENTE_EXIST;
				if(getResaSession().getPlongeeService().isOkForListeAttente(
						plongee, 
						getResaSession().getAdherent())){
					
					// On demande confirmation pour l'inscription en liste d'attente
					replaceModalWindow(target, plongee);
					modalConfirm.show(target);
				}
				break;
			case 3: //on inscrit un pilote ou un dp sur une plongée fermée avec envoi d'un mail aux admins
				typeMail=PlongeeMail.MAIL_INSCRIPTION_SUR_PLONGEE_FERMEE;
				getResaSession().getPlongeeService().inscrireAdherent(
						plongee, 
						adh != null ?  adh : getResaSession().getAdherent(), PlongeeMail.MAIL_INSCRIPTION_SUR_PLONGEE_FERMEE);
					
					setResponsePage(new InscriptionConfirmationPlongeePage(plongee));
				break;
			case 1: //on peut inscrire l'adherent à la plongee
				typeMail=PlongeeMail.PAS_DE_MAIL;
				getResaSession().getPlongeeService().inscrireAdherent(
						plongee, 
						adh != null ?  adh : getResaSession().getAdherent(), PlongeeMail.PAS_DE_MAIL);
				setResponsePage(new InscriptionConfirmationPlongeePage(plongee));
				break;

			case 2: //le plongeur est DP et Pilote => ouvrir plongee
				typeMail=PlongeeMail.PAS_DE_MAIL;
				setResponsePage(new GererPlongeeAOuvrirTwo(plongee));
				break;
			}

		} catch (ResaException e) {
			e.printStackTrace();
			error(e.getKey());
		} catch (TechnicalException e) {
			e.printStackTrace();
			error(e.getKey());
		}  finally {
			target.addComponent(feedback);
		}
	}
	
	public int getHeureOuverture(Calendar cal, Adherent adh){

		int numJour = cal.get(Calendar.DAY_OF_WEEK);			
		int heure;
		switch (numJour) {
			case 1: //Dimanche
				//test actif == 1 ==> ok c un adherent
				// sinon c un externe donc inscription par secretatiat
				if(adh.getActifInt()==1){
					heure=Parameters.getInt("ouverture.dimanche.adh");
				} else {
					heure=Parameters.getInt("ouverture.dimanche.sct");
				}
				break;
			case 2: //Lundi
				if(adh.getActifInt()== 1){
					heure=Parameters.getInt("ouverture.lundi.adh");
				} else {
					heure=Parameters.getInt("ouverture.lundi.sct");
				}
				break;
			case 3: //Mardi
				if(adh.getActifInt()== 1){
					heure=Parameters.getInt("ouverture.mardi.adh");
				} else {
					heure=Parameters.getInt("ouverture.mardi.sct");
				}
				break;
			case 4: //Mercredi 
				if(adh.getActifInt()== 1){
				heure=Parameters.getInt("ouverture.mercredi.adh");
				} else {
					heure=Parameters.getInt("ouverture.mercredi.sct");
				}
				break;
			case 5: //Jeudi
				if(adh.getActifInt()== 1){
				heure=Parameters.getInt("ouverture.jeudi.adh");
				} else {
					heure=Parameters.getInt("ouverture.jeudi.sct");
				}
				break;
			case 6: //Vendredi
				if(adh.getActifInt()== 1){
				heure=Parameters.getInt("ouverture.vendredi.adh");
				} else {
					heure=Parameters.getInt("ouverture.vendredi.sct");
				}
				break;
			case 7: //Samedi
				if(adh.getActifInt()== 1){
					heure=Parameters.getInt("ouverture.samedi.adh");
				} else {
					heure=Parameters.getInt("ouverture.samedi.sct");
				}
				break;
			default:
				heure=0;
				break;
		}
		return heure;
	}

	private void replaceModalWindow(AjaxRequestTarget target, Plongee plongee) {
		modalConfirm.setContent(new ConfirmSelectionModal(modalConfirm.getContentId(), plongee));
		modalConfirm.setTitle("Confirmation d'inscription en liste d'attente");
		modalConfirm.setUseInitialHeight(true);
		
		// Pour éviter le message de disparition de la fenetre lors de la validation
		target.appendJavascript( "Wicket.Window.unloadConfirmation  = false;");
	}
	
	public class ConfirmSelectionModal extends Panel
	{
		private static final long serialVersionUID = 196724625616748115L;

		String message = "La plong\u00e9e est compl\u00e8te";
		
		@SuppressWarnings("unchecked")
		public ConfirmSelectionModal(String id, final Plongee plongee)
		{
			super(id);
			
			if(typeMail == PlongeeMail.MAIL_PAS_ASSEZ_ENCADRANT){
				message = "Il n'y a pas assez d'encadrant";
			}else if(typeMail == PlongeeMail.MAIL_LISTE_ATTENTE_EXIST){
				message = "Cette plong\u00e9e demande l'intervention d'un administrateur pour la gestion des palanqu\u00e9es";
			} 
			// Informations précisant que le plongeur est en liste d'attente
			add(new Label("infoPlongeur", message+"\n Vous allez \u00eatres en liste d'attente en position " + (plongee.getParticipantsEnAttente().size()+1) + "."));
			add(new Label("infoPlongee", " Confirmez-vous votre inscription pour la plong\u00e9e du " + plongee.getDate() + " " + plongee.getType() + " ?"));
			
			// Le lien qui va fermer la fenêtre de confirmation
			// et appeler la méthode de d'inscription en liste d'attente si nécessaire
			add(new IndicatingAjaxLink("yes")
			{
				private static final long serialVersionUID = 1L;

				@Override
				public void onClick(AjaxRequestTarget target)
				{
					try{
						// On inscrit en liste d'attente
						getResaSession().getPlongeeService().inscrireAdherentEnListeAttente(
						plongee, 
						adh != null ?  adh : getResaSession().getAdherent(), typeMail);
					
						setResponsePage(new InscriptionListeAttentePlongeePage(plongee,message));

					} catch (ResaException e) {
						e.printStackTrace();
						error(e.getKey());
						setResponsePage(new InscriptionListeAttentePlongeePage(plongee,message));
					} finally {
						target.addComponent(feedback);
					}

				}
			});
			
			// Le lien qui va juste fermer la fenêtre de confirmation
			add(new IndicatingAjaxLink("no")
			{
				private static final long serialVersionUID = 1L;

				@Override
				public void onClick(AjaxRequestTarget target)
				{
					modalConfirm.close(target);
				}
			});

		}

	}

		
}