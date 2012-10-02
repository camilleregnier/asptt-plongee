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
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.StringResourceModel;

import com.asptt.plongee.resa.exception.ResaException;
import com.asptt.plongee.resa.exception.TechnicalException;
import com.asptt.plongee.resa.mail.PlongeeMail;
import com.asptt.plongee.resa.model.Adherent;
import com.asptt.plongee.resa.model.Message;
import com.asptt.plongee.resa.model.MessageDataProvider;
import com.asptt.plongee.resa.model.Plongee;
import com.asptt.plongee.resa.model.PlongeeDataProvider;
import com.asptt.plongee.resa.ui.web.wicket.ResaSession;
import com.asptt.plongee.resa.ui.web.wicket.component.ConfirmAjaxLink;
import com.asptt.plongee.resa.ui.web.wicket.page.AccueilPage;
import com.asptt.plongee.resa.ui.web.wicket.page.ErreurTechniquePage;
import com.asptt.plongee.resa.ui.web.wicket.page.ErrorPage;
import com.asptt.plongee.resa.ui.web.wicket.page.TemplatePage;
import com.asptt.plongee.resa.ui.web.wicket.page.admin.MessagePanel.MessageForm;
import com.asptt.plongee.resa.ui.web.wicket.page.inscription.InscriptionListeAttentePlongeePage;
import com.asptt.plongee.resa.util.CatalogueMessages;

public class GererMessage extends TemplatePage {
	
	private ModalWindow modalMessage;
	Label infoLabel;
	DataView<Message> dataMessages;
	

	public GererMessage() {
		
		setPageTitle("Gerer les messages");
		// Fenêtre modale d'informations sur la plongée à annuler
		modalMessage = new ModalWindow("modalMessage");
		modalMessage.setTitle("Informations sur le message \u00e0 afficher");
		//modalMessage.setUseInitialHeight(false);
		modalMessage.setWidthUnit("px");
		modalMessage.setInitialHeight(300);
		modalMessage.setInitialWidth(700);
		modalMessage.setResizable(true);
		add(modalMessage);
		
		infoLabel = new Label("infoLabel", new StringResourceModel(CatalogueMessages.GERER_MESSAGE_MSG, this, null));
		add(infoLabel);
		
		// TODO, voir si on ajoute pas cela dans un panel
		// pour une mise à jour dynamique lors de l'annulation de la plongée
		try {
			List<Message> messages = getResaSession().getAdherentService().rechercherMessagesTous();
			
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

					String rang = message.getRang().toString();

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
					item.add(new Label("rang", rang));
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
					
					item.add(new ConfirmAjaxLink("supprimer","Es-tu s\u00fbr(e) de vouloir supprimer ce message ?") {
						private static final long serialVersionUID = 4442484995694176106L;

						@Override
						public void onClick(AjaxRequestTarget target) {
							getResaSession().getAdherentService().deleteMessage(item.getModel().getObject());
							setResponsePage(new GererMessage());
						}
					});
					
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
		setResponsePage(AccueilPage.class);
	}
	
	private void onClickNo(AjaxRequestTarget target){
		modalMessage.close(target);
	}
	
}

