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
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.StringResourceModel;

import com.asptt.plongee.resa.exception.ResaException;
import com.asptt.plongee.resa.exception.TechnicalException;
import com.asptt.plongee.resa.mail.PlongeeMail;
import com.asptt.plongee.resa.model.Adherent;
import com.asptt.plongee.resa.model.Plongee;
import com.asptt.plongee.resa.model.PlongeeDataProvider;
import com.asptt.plongee.resa.ui.web.wicket.component.ConfirmAjaxLink;
import com.asptt.plongee.resa.ui.web.wicket.page.TemplatePage;
import com.asptt.plongee.resa.ui.web.wicket.page.admin.GererPlongeeAOuvrirTwo;
import com.asptt.plongee.resa.util.CatalogueMessages;
import com.asptt.plongee.resa.util.Parameters;
import com.asptt.plongee.resa.util.ResaUtil;

public class InscriptionPlongeePage extends TemplatePage {
	
	private Adherent adh = null;
	private List<Plongee> plongees = null;
	private FeedbackPanel feedback = new FeedbackPanel("feedback");
	private int typeMail;
	
	private ModalWindow modalConfirm;
	
	
	public InscriptionPlongeePage(){
		setPageTitle("Inscription plongee");
		this.adh = getResaSession().getAdherent(); 
		
		add(new Label("message",new StringResourceModel(CatalogueMessages.INSCRIPTION_MSG_ADHERENT, this,new Model<Adherent>(adh))));
		
		//gestion message pour le certificat medical
		Label labelCertificat = new Label("certificat", "");
		try {
			 getResaSession().getPlongeeService().checkCertificatMedical(adh, null);
		} catch (TechnicalException e) {
			e.printStackTrace();
		} catch (ResaException e) {
			String msgCertificat=initMessageException(e.getKey(),null);
			labelCertificat = new Label("certificat",msgCertificat);
		}
		add(labelCertificat);
		feedback.setOutputMarkupId(true);
		add(feedback);
		init();
	}
	
	/**
	 * Ce constructeur est appelé par le secretariat
	 * @param adherent
	 */
	public InscriptionPlongeePage(Adherent adherent) {
		setPageTitle("Inscription plongee");
		this.adh = adherent;

		add(new Label("message",new StringResourceModel(CatalogueMessages.INSCRIPTION_MSG_SECRETARIAT, this,new Model<Adherent>(adh))));

		//gestion message pour le certificat medical
		Label labelCertificat = new Label("certificat", "");
		try {
			 getResaSession().getPlongeeService().checkCertificatMedical(adh, null);
		} catch (TechnicalException e) {
			e.printStackTrace();
		} catch (ResaException e) {
			String msgCertificat=initMessageException(e.getKey(),null);
			labelCertificat = new Label("certificat",msgCertificat);
		}
		add(labelCertificat);
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
				final Plongee plongee = item.getModelObject();
				String nomDP = "Aucun";
				if (null != plongee.getDp()) {
					nomDP = plongee.getDp().getNom();
				}
				//preparation du message de confirmation
				IModel<Plongee> model = new Model<Plongee>(plongee);
				StringResourceModel srm = new StringResourceModel(CatalogueMessages.INSCRIPTION_CONFIRM_RESA, this, model, 
					new Object[]{new PropertyModel<Plongee>(model, "getType"),ResaUtil.getDateString(plongee.getDateVisible())}
	            );
				
				item.add(new ConfirmAjaxLink("select",srm.getString()) 
					{
						private static final long serialVersionUID = 4442484995694176106L;
	
						@Override
						public void onClick(AjaxRequestTarget target) {
							inscrire(target, item.getModel());
						}
					}
				);

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
							String cssClass;
							if (item.getIndex() % 2 == 1){
								cssClass = "even";
							} else {
								cssClass = "odd";
							}
							if(!plongee.isNbMiniAtteint(Parameters.getInt("nb.plongeur.mini"))){
								cssClass = cssClass + " minimumPlongeur";
							}
							return cssClass;
						}
					})
				);
			}//fin du populaItem
		});//fin du Dataview plongee
		
	}

	public void inscrire(AjaxRequestTarget target, IModel<Plongee> iModel) {
		
		Plongee plongee = iModel.getObject();
		try {
			//On met à jour la liste des participant pour verifier si l'adherent n'est pas déjà inscrit
			//cette verification se fera dans la methode isOkForResa
			List<Adherent> participants = getResaSession().getAdherentService().rechercherAdherentInscrits(plongee);
			plongee.setParticipants(participants);
			
			int response = getResaSession().getPlongeeService().isOkForResa(plongee, adh);
			
			//Analyse le retour de service
			switch (response) {
			case 0://on inscrit l'adherent en liste d'attente sans envoi de mail : car plongee complete  
				typeMail=PlongeeMail.PAS_DE_MAIL;
				if(getResaSession().getPlongeeService().isOkForListeAttente(
						plongee, adh )){ //getResaSession().getAdherent()
					// On demande confirmation pour l'inscriptions en liste attente
					replaceModalWindow(target, plongee);
					modalConfirm.show(target);
				}
				break;
				
			case 4: //on inscrit l'adherent en liste d'attente avec envoi d'un mail : pas assez d'encadrant
				typeMail=PlongeeMail.MAIL_PAS_ASSEZ_ENCADRANT;
				if(getResaSession().getPlongeeService().isOkForListeAttente(
						plongee,  adh )){ //getResaSession().getAdherent()
					
					// On demande confirmation pour l'inscription en liste d'attente
					replaceModalWindow(target, plongee);
					modalConfirm.show(target);
				}
				break;
				
			case 5: //on inscrit l'adherent en liste d'attente avec envoi d'un mail : Liste d'attente déjà ouverte
				typeMail=PlongeeMail.MAIL_LISTE_ATTENTE_EXIST;
				if(getResaSession().getPlongeeService().isOkForListeAttente(
						plongee,  adh )){ //getResaSession().getAdherent()
					
					// On demande confirmation pour l'inscription en liste d'attente
					replaceModalWindow(target, plongee);
					modalConfirm.show(target);
				}
				break;
				
			case 3: //on inscrit un pilote ou un dp sur une plongée fermée avec envoi d'un mail aux admins
				typeMail=PlongeeMail.MAIL_INSCRIPTION_SUR_PLONGEE_FERMEE;
					
				getResaSession().getPlongeeService().inscrireAdherent(plongee, adh, PlongeeMail.MAIL_INSCRIPTION_SUR_PLONGEE_FERMEE);
					setResponsePage(new InscriptionConfirmationPlongeePage(plongee));
				break;
				
			case 1: //on peut inscrire l'adherent à la plongee
				typeMail=PlongeeMail.PAS_DE_MAIL;

				getResaSession().getPlongeeService().inscrireAdherent(plongee,adh, PlongeeMail.PAS_DE_MAIL);
				setResponsePage(new InscriptionConfirmationPlongeePage(plongee));
				break;

			case 2: //le plongeur est DP et Pilote => ouvrir plongee
				typeMail=PlongeeMail.PAS_DE_MAIL;
				setResponsePage(new GererPlongeeAOuvrirTwo(plongee));
				break;

			}

		} catch (ResaException e) {
			String libRetour = "";
			libRetour=initMessageException(e.getKey(), plongee);
			error(libRetour);
		} catch (TechnicalException e) {
			e.printStackTrace();
			error(e.getKey());
		}  finally {
			target.addComponent(feedback);
		}
	}
	
	private String initMessageException(String entreeCatalogue, Plongee plongee){
		String libRetour="";
		if(entreeCatalogue.startsWith(CatalogueMessages.INSCRIPTION_ATTENDRE_HO)){
			String ho = entreeCatalogue.substring(12);
			IModel<Adherent> model = new Model<Adherent>(adh);
			StringResourceModel srm = new StringResourceModel(CatalogueMessages.INSCRIPTION_ATTENDRE_HO, this, model, 
				new Object[]{new PropertyModel<Adherent>(model, "prenom"),ResaUtil.getDateString(plongee.getDateVisible()),ho}
            );
			libRetour=srm.getString();
		} else if(entreeCatalogue.startsWith(CatalogueMessages.INSCRIPTION_ATTENDRE_J_HO)){
			String ho = entreeCatalogue.substring(14);
			IModel<Adherent> model = new Model<Adherent>(adh);
			StringResourceModel srm = new StringResourceModel(CatalogueMessages.INSCRIPTION_ATTENDRE_J_HO, this, model, 
				new Object[]{new PropertyModel<Adherent>(model, "prenom"),ho}
            );
			libRetour=srm.getString();
		} else if(entreeCatalogue.equalsIgnoreCase(CatalogueMessages.CM_PERIME)){
			StringResourceModel srm = new StringResourceModel(CatalogueMessages.CM_PERIME, this, null);
			libRetour=srm.getString();
		} else if(entreeCatalogue.startsWith(CatalogueMessages.CM_A_RENOUVELER)){
			String nbJour = entreeCatalogue.substring(14);
			IModel<Adherent> model = new Model<Adherent>(adh);
			StringResourceModel srm = new StringResourceModel(CatalogueMessages.CM_A_RENOUVELER, this, model, 
					new Object[]{new PropertyModel<Adherent>(model, "prenom"),nbJour}
				);
			libRetour=srm.getString();
		} else if(entreeCatalogue.equalsIgnoreCase(CatalogueMessages.INSCRIPTION_KO_PLONGEE_FERMEE)){
			IModel<Plongee> model = new Model<Plongee>(plongee);
			StringResourceModel srm = new StringResourceModel(CatalogueMessages.INSCRIPTION_KO_PLONGEE_FERMEE, this, model, 
				new Object[]{new PropertyModel<Plongee>(model, "getType"),ResaUtil.getDateString(plongee.getDateVisible())}
       		);
			libRetour=srm.getString();
		} else if(entreeCatalogue.equalsIgnoreCase(CatalogueMessages.INSCRIPTION_KO_NB_MAX_PLONGEUR)){
			StringResourceModel srm = new StringResourceModel(CatalogueMessages.INSCRIPTION_KO_NB_MAX_PLONGEUR, this,new Model<Plongee>(plongee));
			libRetour=srm.getString();
		} else if(entreeCatalogue.equalsIgnoreCase(CatalogueMessages.INSCRIPTION_KO_DP_P5)){
			IModel<Plongee> model = new Model<Plongee>(plongee);
			StringResourceModel srm = new StringResourceModel(CatalogueMessages.INSCRIPTION_KO_DP_P5, this, model, 
				new Object[]{new PropertyModel<Plongee>(model, "getType"),ResaUtil.getDateString(plongee.getDateVisible())}
            );
			libRetour=srm.getString();
		} else if(entreeCatalogue.equalsIgnoreCase(CatalogueMessages.INSCRIPTION_KO_NIVEAU_MINI)){
			IModel<Plongee> model = new Model<Plongee>(plongee);
			StringResourceModel srm = new StringResourceModel(CatalogueMessages.INSCRIPTION_KO_NIVEAU_MINI, this, model, 
				new Object[]{new PropertyModel<Plongee>(model, "getType"),ResaUtil.getDateString(plongee.getDateVisible()),
							new PropertyModel<Plongee>(model, "getNiveauMinimum")}
            );
			libRetour=srm.getString();
		} else if(entreeCatalogue.equalsIgnoreCase(CatalogueMessages.INSCRIPTION_KO_DEJA_EN_ATTENTE)){
			IModel<Plongee> model = new Model<Plongee>(plongee);
			StringResourceModel srm = new StringResourceModel(CatalogueMessages.INSCRIPTION_KO_DEJA_EN_ATTENTE, this, model, 
				new Object[]{new PropertyModel<Plongee>(model, "getType"),ResaUtil.getDateString(plongee.getDateVisible())}
            );
			libRetour=srm.getString();
		} else if(entreeCatalogue.equalsIgnoreCase(CatalogueMessages.INSCRIPTION_KO_DEJA_INSCRIT)){
			StringResourceModel srm = new StringResourceModel(CatalogueMessages.INSCRIPTION_KO_DEJA_INSCRIT, this,new Model<Adherent>(adh)); 
			libRetour=srm.getString();
		} else if(entreeCatalogue.equalsIgnoreCase(CatalogueMessages.INSCRIPTION_LISTE_ATTENTE_KO)){
			IModel<Plongee> model = new Model<Plongee>(plongee);
			StringResourceModel srm = new StringResourceModel(CatalogueMessages.INSCRIPTION_LISTE_ATTENTE_KO, this, model, 
				new Object[]{new PropertyModel<Plongee>(model, "getType"),ResaUtil.getDateString(plongee.getDateVisible())}
            );
			libRetour=srm.getString();
		} else if(entreeCatalogue.startsWith(CatalogueMessages.INSCRIPTION_ATTENDRE_VR_HO)){
			String ho = entreeCatalogue.substring(15);
			IModel<Adherent> model = new Model<Adherent>(adh);
			StringResourceModel srm = new StringResourceModel(CatalogueMessages.INSCRIPTION_ATTENDRE_VR_HO, this, model, 
				new Object[]{new PropertyModel<Adherent>(model, "prenom"),ResaUtil.getDateString(plongee.getDateVisible()),ho}
            );
			libRetour=srm.getString();
		} else if(entreeCatalogue.startsWith(CatalogueMessages.INSCRIPTION_ATTENDRE_VR_J_HO)){
			String ho = entreeCatalogue.substring(14);
			IModel<Adherent> model = new Model<Adherent>(adh);
			StringResourceModel srm = new StringResourceModel(CatalogueMessages.INSCRIPTION_ATTENDRE_VR_J_HO, this, model, 
				new Object[]{new PropertyModel<Adherent>(model, "prenom"),ho}
            );
			libRetour=srm.getString();
		} else {
			libRetour="......";
		}
		return libRetour;
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

		StringResourceModel srm = new StringResourceModel(CatalogueMessages.INSCRIPTION_ATT_MAX_ATTEINT, this,null); 
		String message = srm.getString();

		@SuppressWarnings("unchecked")
		public ConfirmSelectionModal(String id, final Plongee plongee)
		{
			super(id);
			
			if(typeMail == PlongeeMail.MAIL_PAS_ASSEZ_ENCADRANT){
				srm = new StringResourceModel(CatalogueMessages.INSCRIPTION_ATT_MANQUE_ENCADRANT, this,null); 
				message = srm.getString();
			}else if(typeMail == PlongeeMail.MAIL_LISTE_ATTENTE_EXIST){
				srm = new StringResourceModel(CatalogueMessages.INSCRIPTION_ATT_A_GERER, this,null); 
				message = srm.getString();
			} 
			// Informations précisant que le plongeur est en liste d'attente
			IModel<Plongee> model = new Model<Plongee>(plongee);
			StringResourceModel srmPlongeur = new StringResourceModel(CatalogueMessages.INSCRIPTION_ATT_INFO_PLONGEUR, this,null); 
			
			add(new Label("infoPlongeur", message+"\n"+srmPlongeur.getString()+" "+plongee.getParticipantsEnAttente().size()+1+"."));
			
			StringResourceModel srmPlongee = new StringResourceModel(CatalogueMessages.INSCRIPTION_ATT_INFO_PLONGEE, this, model, 
					new Object[]{ResaUtil.getDateString(plongee.getDateVisible()),new PropertyModel<Plongee>(model, "getType")}
       		);
			add(new Label("infoPlongee", srmPlongee.getString()));
			
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
								plongee, adh != null ?  adh : getResaSession().getAdherent(), typeMail);
					
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

		}//fin constructeur de la fenetre modal de confirmation

	}// fin de la classe de la fenetre modal de confirmation

		
}