package com.asptt.plongee.resa.ui.web.wicket.page.admin;

import java.util.Date;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.behavior.BehaviorsUtil;
import org.apache.wicket.datetime.StyleDateConverter;
import org.apache.wicket.datetime.markup.html.form.DateTextField;
import org.apache.wicket.extensions.yui.calendar.DatePicker;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.ListMultipleChoice;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.validation.validator.PatternValidator;
import org.apache.wicket.validation.validator.StringValidator.ExactLengthValidator;

import com.asptt.plongee.resa.exception.ResaException;
import com.asptt.plongee.resa.exception.TechnicalException;
import com.asptt.plongee.resa.model.Adherent;
import com.asptt.plongee.resa.model.Message;
import com.asptt.plongee.resa.model.Plongee;
import com.asptt.plongee.resa.ui.web.wicket.ResaSession;
import com.asptt.plongee.resa.ui.web.wicket.page.AccueilPage;
import com.asptt.plongee.resa.ui.web.wicket.page.ErreurTechniquePage;
import com.asptt.plongee.resa.util.CatalogueMessages;
import com.asptt.plongee.resa.util.ResaUtil;

public class MessagePanel extends Panel {
	
	private CompoundPropertyModel<Message> model;
	FeedbackPanel feedback = new FeedbackPanel("feedback");
	
	public MessagePanel(String id, IModel<Message> message) {
		super(id, message);
		setOutputMarkupId(true);

		feedback.setOutputMarkupId(true);
		add(feedback);

		add(new MessageForm("inputForm", message));

	}

	class MessageForm extends Form {

		private static final long serialVersionUID = 5374674730458593314L;

		public MessageForm(String id, IModel<Message> message) {
			super(id,message);
			model = new CompoundPropertyModel<Message>(message);
			setModel(model);
			
			TextArea textareaInput = new TextArea("libelle");
			add(textareaInput);
			
			DateTextField dateDebTextFiled = new DateTextField("dateDebut", new PropertyModel<Date>(model, "dateDebut"), new StyleDateConverter("S-", true));
			dateDebTextFiled.setRequired(true);
			add(dateDebTextFiled);
			dateDebTextFiled.add(new DatePicker());
			
			DateTextField dateFinTextFiled = new DateTextField("dateFin", new PropertyModel<Date>(model, "dateFin"), new StyleDateConverter("S-", true));
			dateFinTextFiled.setRequired(false);
			add(dateFinTextFiled);
			dateFinTextFiled.add(new DatePicker());
			
			add(new AjaxButton("validMessage") {
				@Override
				// La validation doit se faire en Ajax car le formulaire de la
				// fenêtre principal n'y a pas accés
				// http://yeswicket.com/index.php?post/2010/04/26/G%C3%A9rer-facilement-les-fen%C3%AAtres-modales-avec-Wicket
				protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
					Message message = (Message) form.getModelObject();
					
					// Mise à jour du message
					try {
						if(null != message.getDateFin()){
							if( message.getDateFin().before(message.getDateDebut()) ){
								throw new ResaException(CatalogueMessages.DATE_INCOMPATIBLE);
							}
						}
						ResaSession resaSession = (ResaSession) getApplication()
								.getSessionStore().lookup(getRequest());
						resaSession.getAdherentService().updateMessage(message);

						setResponsePage(AccueilPage.class);
						
					} catch (TechnicalException e) {
						e.printStackTrace();
						error(e.getKey());
					} catch (ResaException e) {
						e.printStackTrace();
						error(initMessageException(e.getKey()));
					}

				}
				
				// L'implémentation de cette méthode est nécessaire pour voir
				// les messages d'erreur dans le feedBackPanel
				protected void onError(AjaxRequestTarget target, Form<?> form) {
					target.addComponent(feedback);
				}

			});

			add(new AjaxButton("creerMessage") {
				@Override
				// La validation doit se faire en Ajax car le formulaire de la
				// fenêtre principal n'y a pas accés
				// http://yeswicket.com/index.php?post/2010/04/26/G%C3%A9rer-facilement-les-fen%C3%AAtres-modales-avec-Wicket
				protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
					Message message = (Message) form.getModelObject();
					
					// Mise à jour du message
					try {
						if(null != message.getDateFin()){
							if( message.getDateFin().before(message.getDateDebut())){
								throw new ResaException(CatalogueMessages.DATE_INCOMPATIBLE);
							}
						}
						ResaSession resaSession = (ResaSession) getApplication()
								.getSessionStore().lookup(getRequest());
						resaSession.getAdherentService().createMessage(message);

						setResponsePage(AccueilPage.class);
						
					} catch (TechnicalException e) {
						e.printStackTrace();
						error(e.getKey());
					} catch (ResaException e) {
						e.printStackTrace();
						error(initMessageException(e.getKey()));
					}

				}
				
				// L'implémentation de cette méthode est nécessaire pour voir
				// les messages d'erreur dans le feedBackPanel
				protected void onError(AjaxRequestTarget target, Form<?> form) {
					target.addComponent(feedback);
				}

			});

			add(new Link("cancel") {
				@Override
				public void onClick() {
					setResponsePage(GererMessage.class);
				}
			});
			//add(new Button("cancel", new ResourceModel("button.cancel")));
		}

	}
	private String initMessageException(String entreeCatalogue){
		String libError="";
		if(entreeCatalogue.equalsIgnoreCase(CatalogueMessages.DATE_INCOMPATIBLE)){
			StringResourceModel srm = new StringResourceModel(CatalogueMessages.DATE_INCOMPATIBLE, this, null);
			libError=srm.getString();
		}
		return libError;
	}
}
