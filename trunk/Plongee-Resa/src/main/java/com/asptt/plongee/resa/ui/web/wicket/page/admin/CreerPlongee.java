package com.asptt.plongee.resa.ui.web.wicket.page.admin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.datetime.StyleDateConverter;
import org.apache.wicket.datetime.markup.html.form.DateTextField;
import org.apache.wicket.extensions.yui.calendar.DatePicker;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.validation.validator.MinimumValidator;

import com.asptt.plongee.resa.exception.ResaException;
import com.asptt.plongee.resa.exception.TechnicalException;
import com.asptt.plongee.resa.model.NiveauAutonomie;
import com.asptt.plongee.resa.model.Plongee;
import com.asptt.plongee.resa.ui.web.wicket.ResaSession;
import com.asptt.plongee.resa.ui.web.wicket.page.AccueilPage;
import com.asptt.plongee.resa.ui.web.wicket.page.ErreurTechniquePage;
import com.asptt.plongee.resa.ui.web.wicket.page.ErrorPage;
import com.asptt.plongee.resa.ui.web.wicket.page.TemplatePage;

public class CreerPlongee extends TemplatePage {
	
	FeedbackPanel feedback = new FeedbackPanel("feedback");

	public CreerPlongee() {
		// Constructeur du formulaire et du feedback panel pour renvoyer des messages sur la page
		feedback.setOutputMarkupId(true);
		add(feedback);
		add(new PlongeeForm("inputForm"));
	}

	public class PlongeeForm extends Form<Plongee> {

		private static final long serialVersionUID = 5374674730458593314L;

		public PlongeeForm(String id) {
			super(id);

			CompoundPropertyModel<Plongee> model = new CompoundPropertyModel<Plongee>(new Plongee());
			setModel(model);
			
			DateTextField dateTextFiled = new DateTextField("date", new PropertyModel<Date>(model, "date"), new StyleDateConverter("S-", true));
			dateTextFiled.setRequired(true);
			add(dateTextFiled);
			dateTextFiled.add(new DatePicker());
			
			add(new RequiredTextField<Integer>("nbMaxPlaces", Integer.class).add(new MinimumValidator<Integer>(4)));
			
			
			// Ajout de la liste des niveaux
			List<String> niveaux = new ArrayList<String>();
			for (NiveauAutonomie n : NiveauAutonomie.values()){
				niveaux.add(n.toString());
			}
			add(new DropDownChoice("niveauMinimum", niveaux));
			
			//Ajout du type de plongee
			List<String> type = Arrays.asList(new String[] { "MATIN", "APRES_MIDI", "SOIR", "NUIT" });
			add(new DropDownChoice<String>("type", type).setRequired(true));
			
			add(new AjaxButton("validPlongee") {

				@Override
				protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
					Plongee plongee = (Plongee) form.getModelObject();
					try {
						getResaSession().getPlongeeService().creerPlongee(plongee);

						setResponsePage(AccueilPage.class);
					} catch (ResaException e) {
						e.printStackTrace();
						error(e.getKey());
					} catch (TechnicalException e) {
						e.printStackTrace();
						error(e.getKey());
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
					setResponsePage(AccueilPage.class);
				}
			});

		}

	}

}
