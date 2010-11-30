package com.asptt.plongee.resa.ui.web.wicket.page.admin;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxLink;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;

import com.asptt.plongee.resa.exception.ResaException;
import com.asptt.plongee.resa.exception.TechnicalException;
import com.asptt.plongee.resa.model.Adherent;
import com.asptt.plongee.resa.model.Message;
import com.asptt.plongee.resa.model.MessageDataProvider;
import com.asptt.plongee.resa.model.Plongee;
import com.asptt.plongee.resa.model.PlongeeDataProvider;
import com.asptt.plongee.resa.ui.web.wicket.ResaSession;
import com.asptt.plongee.resa.ui.web.wicket.page.AccueilPage;
import com.asptt.plongee.resa.ui.web.wicket.page.ErreurTechniquePage;
import com.asptt.plongee.resa.ui.web.wicket.page.ErrorPage;
import com.asptt.plongee.resa.ui.web.wicket.page.TemplatePage;
import com.asptt.plongee.resa.ui.web.wicket.page.admin.MessagePanel.MessageForm;

public class GererMessage extends TemplatePage {
	
	private ModalWindow modalMessage;
	Label infoLabel;
	DataView<Message> dataMessages;

	public GererMessage() {
		
		// Fenêtre modale d'informations sur la plongée à annuler
		modalMessage = new ModalWindow("modalMessage");
		modalMessage.setTitle("Informations sur le message \u00e0 afficher");
		modalMessage.setUseInitialHeight(false);
		modalMessage.setWidthUnit("px");
		add(modalMessage);
		
		infoLabel = new Label("infoLabel", "Choisissez la message \u00e0 modifier");
		add(infoLabel);
		
		// TODO, voir si on ajoute pas cela dans un panel
		// pour une mise à jour dynamique lors de l'annulation de la plongée
		try {
			List<Message> messages = getResaSession().getAdherentService().rechercherMessage();
			
			MessageDataProvider mDataProvider = new MessageDataProvider(messages);

			dataMessages = new DataView<Message>("simple", mDataProvider) {
				protected void populateItem(final Item<Message> item) {
					Message message = item.getModelObject();
					// le libelle
					String libelle = message.getLibelle();

					item.add(new IndicatingAjaxLink("modifier") {
						@Override
						public void onClick(AjaxRequestTarget target) {
							replaceModalWindow(target, item.getModel());
							modalMessage.show(target);
						}
					});

					// Mise en forme de la date de debut
					Calendar calDeb = Calendar.getInstance();
					calDeb.setTime(message.getDateDebut());
					String dateDebAffichee = calDeb.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.FRANCE) + " ";
					dateDebAffichee = dateDebAffichee + calDeb.get(Calendar.DAY_OF_MONTH) + " ";
					dateDebAffichee = dateDebAffichee + calDeb.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.FRANCE) + " ";
					dateDebAffichee = dateDebAffichee + calDeb.get(Calendar.YEAR);
					
					// Mise en forme de la date de debut
					String dateFinAffichee = null;
					if(message.getDateFin() != null){
						Calendar calFin = Calendar.getInstance();
						calFin.setTime(message.getDateFin());
						dateFinAffichee = calFin.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.FRANCE) + " ";
						dateFinAffichee = dateFinAffichee + calFin.get(Calendar.DAY_OF_MONTH) + " ";
						dateFinAffichee = dateFinAffichee + calFin.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.FRANCE) + " ";
						dateFinAffichee = dateFinAffichee + calFin.get(Calendar.YEAR);
					}else{
						dateFinAffichee = "Pas de date de fin";
					}
					item.add(new Label("libelle", libelle));
					item.add(new Label("dateDebut", dateDebAffichee));
					item.add(new Label("dateFin", dateFinAffichee));
					
					item.add(new AttributeModifier("class", true,
							new AbstractReadOnlyModel<String>() {
								@Override
								public String getObject() {
									return (item.getIndex() % 2 == 1) ? "even"
											: "odd";
								}
							}));
				}
			};
			dataMessages.setOutputMarkupId(true);
			add(dataMessages);

		} catch (TechnicalException e) {
			e.printStackTrace();
			ErreurTechniquePage etp = new ErreurTechniquePage(e);
			setResponsePage(etp);
		}
	}
	
	private void replaceModalWindow(AjaxRequestTarget target, IModel<Message> message) {
		modalMessage.setContent(new MessagePanel(modalMessage.getContentId(), message));
		
		// Pour éviter le message de disparition de la fenetre lors de la validation
		target.appendJavascript( "Wicket.Window.unloadConfirmation  = false;");
	}
	
	private void onClickMessagePanel(AjaxRequestTarget target, Message message){

		modalMessage.close(target);
		
		// appel du service d'annulation de la plongée;
//		getResaSession().getAdherentService().supprimerPlongee(message);
		
		setResponsePage(AccueilPage.class);

	}
	
	private void onClickNo(AjaxRequestTarget target){

		modalMessage.close(target);

	}
	
//	public  class MessagePanel extends Panel {
//
//		private static final long serialVersionUID = 8737443673255555616L;
//
//		public MessagePanel(String id, IModel<Message> messageModel) {
//			super(id, messageModel);
//			setOutputMarkupId(true);
//			setDefaultModel(messageModel);
//			
//			final Message message = messageModel.getObject();
//			
//			// Informations précisant la plongeur concerné et la plongée
//			// dans la fenêtre de confirmation de désinscription
//			add(new Label("infoPlongee", "Etes-vous s\u00fbr de vouloir annuler la plong\u00e9e du  " + message.getDateDebut() + " ?"));
////			int nbPlongeursInscrits = message.getParticipants().size();
////			add(new Label("infoPlongeurs", (nbPlongeursInscrits == 0) ? " " : " Il y a " + nbPlongeursInscrits + " plongeur(s) inscrits."));
//			
//			// Le lien qui va fermer la fenêtre de confirmation
//			// et appeler la méthode de désinscription de la page principale (DesInscriptionPlongeePage)
//			add(new IndicatingAjaxLink("yes")
//			{
//				private static final long serialVersionUID = 1L;
//
//				@Override
//				public void onClick(AjaxRequestTarget target)
//				{
//					onClickMessagePanel(target, message);
//				}
//			});
//			
//			add(new IndicatingAjaxLink("no")
//			{
//				private static final long serialVersionUID = 1L;
//
//				@Override
//				public void onClick(AjaxRequestTarget target)
//				{
//					onClickNo(target);
//				}
//			});
//		}
//		
//	}
}

