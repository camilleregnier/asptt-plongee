package com.asptt.plongee.resa.ui.web.wicket.page.inscription;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
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
import com.asptt.plongee.resa.model.InscriptionFilleul;
import com.asptt.plongee.resa.model.Plongee;
import com.asptt.plongee.resa.model.PlongeeDataProvider;
import com.asptt.plongee.resa.model.PlongeeFilleulDataProvider;
import com.asptt.plongee.resa.ui.web.wicket.component.ConfirmAjaxLink;
import com.asptt.plongee.resa.ui.web.wicket.page.ErreurTechniquePage;
import com.asptt.plongee.resa.ui.web.wicket.page.TemplatePage;
import com.asptt.plongee.resa.util.CatalogueMessages;
import com.asptt.plongee.resa.util.Parameters;
import com.asptt.plongee.resa.util.ResaUtil;

public class DesinscriptionFilleulPage extends TemplatePage {

	private List<InscriptionFilleul> plongeesDesFilleuls = null;
	private Adherent parrain = null;
	private Adherent filleul = null;
	private List<Plongee> plongees;
	private final FeedbackPanel feedback;
	
	private ModalWindow modalConfirm;
	
	public DesinscriptionFilleulPage() {

		this.parrain = getResaSession().getAdherent(); 
		
		setPageTitle("Desinscription Externe plongee");
		add(new Label("message",new StringResourceModel(CatalogueMessages.DESINSCRIPTION_FILLEUL_MSG_ADHERENT, this,new Model<Adherent>(parrain))));
		
		feedback = new FeedbackPanel("feedback");
		feedback.setOutputMarkupId(true);
		add(feedback);
		
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
		
		init();
	}

	private void init() {
		try{
			plongeesDesFilleuls = getResaSession().getPlongeeService().rechercherPlongeesFilleulInscrit( 	
					parrain, Parameters.getInt("desincription.nb.heure"));
		} catch (TechnicalException e) {
			e.printStackTrace();
			ErreurTechniquePage etp = new ErreurTechniquePage(e);
			setResponsePage(etp);
		}
		
		PlongeeFilleulDataProvider pDataProvider = new PlongeeFilleulDataProvider(plongeesDesFilleuls);

		add(new DataView<InscriptionFilleul>("simple", pDataProvider) {

			private static final long serialVersionUID = 2877768852318892774L;

			protected void populateItem(final Item<InscriptionFilleul> item) {
				final InscriptionFilleul inscription = item.getModelObject();
				final Plongee plongee = inscription.getPlongeeInscrit();
				final Adherent filleul = inscription.getFilleul();
				String nomDP = "Aucun";
				if (null != plongee.getDp()) {
					nomDP = plongee.getDp().getNom();
				}
				//preparation du message de confirmation
				IModel<Plongee> model = new Model<Plongee>(plongee);
				StringResourceModel srm = new StringResourceModel(CatalogueMessages.DESINSCRIPTION_FILLEUL_CONFIRMATION, this, model, 
					new Object[]{new PropertyModel<Plongee>(model, "getType"),ResaUtil.getDateString(plongee.getDate()), filleul.getNom(), filleul.getPrenom()}
	            );
				
				item.add(new ConfirmAjaxLink("select",srm.getString()) 
				{
					private static final long serialVersionUID = 1771547719792642196L;
					@Override
					public void onClick(AjaxRequestTarget target) {
						desinscrire(target, plongee, filleul);
					}
				});
				
				String nomFilleul = filleul.getNom();
				String prenomFilleul = filleul.getPrenom();

				// Mise en forme de la date
				Calendar cal = Calendar.getInstance();
				cal.setTime(plongee.getDate());
				String dateAffichee = cal.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.FRANCE) + " ";
				dateAffichee = dateAffichee + cal.get(Calendar.DAY_OF_MONTH) + " ";
				dateAffichee = dateAffichee + cal.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.FRANCE) + " ";
				dateAffichee = dateAffichee + cal.get(Calendar.YEAR);
				
				item.add(new Label("nom", nomFilleul));
				item.add(new Label("prenom", prenomFilleul));
				item.add(new Label("date", dateAffichee));
				item.add(new Label("type", plongee.getType()));

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
							boolean isInscrit = false;
							for (Adherent adherent : plongee.getParticipants()){
								if (adherent.getNumeroLicense().equals(parrain.getNumeroLicense())){
									cssClass = cssClass + " inscrit";
									isInscrit = true;
								}
							}
							if(!plongee.isNbMiniAtteint(Parameters.getInt("nb.plongeur.mini"))){
								if (isInscrit){
									cssClass = cssClass + "MinimumPlongeur";
								} else {
									cssClass = cssClass + " minimumPlongeur";
								}
							}
							return cssClass;
						}
					})
				);
			}
		});
		
	}

	public void desinscrire(AjaxRequestTarget target, Plongee plongee, Adherent filleul) {
		
		//final Plongee plongee = iModel.getObject();
		try {
			//On considere que les Filleuls etants des externes
			//ne sont donc pas des encadrants
			// Dons pas de test sur l'apppartenance à l'encadrement
			//S'il y a des personnes en liste d'attente => mail aux ADMIN
			if(getResaSession().getPlongeeService().rechercherListeAttente(plongee).size() > 0){
				getResaSession().getPlongeeService().deInscrireAdherent(
						plongee, filleul, PlongeeMail.MAIL_PLACES_LIBRES);
				
			} else {
				getResaSession().getPlongeeService().deInscrireAdherent(
						plongee, filleul, PlongeeMail.PAS_DE_MAIL);
			}
			setResponsePage(new InscriptionConfirmationPlongeePage(plongee));
		} catch (TechnicalException e) {
			e.printStackTrace();
			error(e.getKey());
		} catch (ResaException e) {
			String libRetour = "";
			if(e.getKey().startsWith(CatalogueMessages.DESINSCRIPTION_IMPOSSIBLE)){
				String nbHeure = e.getKey().substring(23);
				IModel<Adherent> model = new Model<Adherent>(parrain);
				StringResourceModel srm = new StringResourceModel(CatalogueMessages.DESINSCRIPTION_IMPOSSIBLE, this, model, 
					new Object[]{new PropertyModel<Adherent>(model, "prenom"),nbHeure}
	            );
				libRetour=srm.getString();
			}
			error(libRetour);
		} finally {
			target.addComponent(feedback);
		}
	}
	
	private void replaceModalWindow(AjaxRequestTarget target, Plongee plongee) {
		modalConfirm.setContent(new ConfirmSelectionModal(modalConfirm.getContentId(), plongee));
		modalConfirm.setTitle("Confirmation de d\u00e9sinscription \u00e0 une plong\u00e9e");
		modalConfirm.setUseInitialHeight(true);
		
		// Pour éviter le message de disparition de la fenetre lors de la validation
		target.appendJavascript( "Wicket.Window.unloadConfirmation  = false;");
	}

	public class ConfirmSelectionModal extends Panel
	{
		private static final long serialVersionUID = 6479726881300748390L;

		public ConfirmSelectionModal(String id, final Plongee plongee)
		{
			super(id);
			
			// Informations précisant la plongeur concerné et la plongée
			// dans la fenêtre de confirmation de désinscription
			IModel<Plongee> model = new Model<Plongee>(plongee);
			StringResourceModel srmPlongeur = new StringResourceModel(CatalogueMessages.DESINSCRIPTION_CONFIRMATION_PLONGEUR, this,null); 		
			add(new Label("infoPlongeur", srmPlongeur));
			
			StringResourceModel srmPlongee = new StringResourceModel(CatalogueMessages.DESINSCRIPTION_CONFIRMATION_PLONGEE, this, model, 
					new Object[]{ResaUtil.getDateString(plongee.getDate()),new PropertyModel<Plongee>(model, "getType")}
       		);
			add(new Label("infoPlongee", srmPlongee));
			
			// Le lien qui va fermer la fenêtre de confirmation
			// et appeler la méthode de désinscription de la page principale (DesInscriptionPlongeePage)
			add(new IndicatingAjaxLink("yes")
			{
				private static final long serialVersionUID = 1L;

				@Override
				public void onClick(AjaxRequestTarget target)
				{
					// On desinscrit
					try{
						getResaSession().getPlongeeService().deInscrireAdherent(
								plongee, 
								getResaSession().getAdherent(), PlongeeMail.MAIL_PLUS_ASSEZ_ENCADRANT);
						
						setResponsePage(DesinscriptionFilleulPage.class);
					} catch (ResaException e) {
						String libRetour = "";
						if(e.getKey().startsWith(CatalogueMessages.DESINSCRIPTION_IMPOSSIBLE)){
							String nbHeure = e.getKey().substring(23);
							IModel<Adherent> model = new Model<Adherent>(parrain);
							StringResourceModel srm = new StringResourceModel(CatalogueMessages.DESINSCRIPTION_IMPOSSIBLE, this, model, 
								new Object[]{new PropertyModel<Adherent>(model, "prenom"),nbHeure}
				            );
							libRetour=srm.getString();
						}
						error(libRetour);
					} finally {
						target.addComponent(feedback);
					}

				}
			});
			
			add(new IndicatingAjaxLink("no")
			{
				@Override
				public void onClick(AjaxRequestTarget target)
				{
					modalConfirm.close(target);
				}
			});

		}

	}


}
