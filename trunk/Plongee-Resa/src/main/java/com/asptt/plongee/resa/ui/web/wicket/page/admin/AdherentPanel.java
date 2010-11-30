package com.asptt.plongee.resa.ui.web.wicket.page.admin;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.behavior.BehaviorsUtil;
import org.apache.wicket.datetime.StyleDateConverter;
import org.apache.wicket.datetime.markup.html.form.DateTextField;
import org.apache.wicket.extensions.yui.calendar.DatePicker;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.ListMultipleChoice;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.validation.validator.EmailAddressValidator;
import org.apache.wicket.validation.validator.MinimumValidator;
import org.apache.wicket.validation.validator.PatternValidator;
import org.apache.wicket.validation.validator.RangeValidator;
import org.apache.wicket.validation.validator.StringValidator.ExactLengthValidator;

import com.asptt.plongee.resa.exception.TechnicalException;
import com.asptt.plongee.resa.model.Adherent;
import com.asptt.plongee.resa.model.NiveauAutonomie;
import com.asptt.plongee.resa.model.Plongee;
import com.asptt.plongee.resa.ui.web.wicket.ResaSession;
import com.asptt.plongee.resa.ui.web.wicket.page.AccueilPage;
import com.asptt.plongee.resa.ui.web.wicket.page.ErreurTechniquePage;

public class AdherentPanel extends Panel {
	
	private CompoundPropertyModel<Adherent> model;
	
	public AdherentPanel(String id, IModel<Adherent> adherent) {
		super(id, adherent);
		setOutputMarkupId(true);

		add(new AdherentForm("inputForm", adherent));

	}

	class AdherentForm extends Form {

		private static final long serialVersionUID = 5374674730458593314L;

		public AdherentForm(String id, IModel<Adherent> adherent) {
			super(id,adherent);
			model = new CompoundPropertyModel<Adherent>(adherent);
			setModel(model);
			
			final FeedbackPanel feedback = new FeedbackPanel("feedback");
			feedback.setOutputMarkupId(true);
			add(feedback);
			add(new RequiredTextField<String>("nom"));
			add(new RequiredTextField<String>("prenom"));
			add(new Label("numeroLicense",adherent.getObject().getNumeroLicense()));

			// numéro de téléphone au bon format (10 caractères numériques)
			RequiredTextField<String> telephone = new RequiredTextField<String>("telephone", String.class);
			telephone.add(ExactLengthValidator.exactLength(10));
			telephone.add(new PatternValidator("\\d{10}"));
			add(telephone);
			
			add(new RequiredTextField<String>("mail").add(EmailAddressValidator
					.getInstance()));

			// Ajout de la liste des niveaux
			List<String> niveaux = new ArrayList<String>();
			for (NiveauAutonomie n : NiveauAutonomie.values()) {
				niveaux.add(n.toString());
			}
			add(new DropDownChoice("niveau", niveaux));
			
			// Ajout de la checkbox pilote
			add(new CheckBox("pilote"));
			
			// Ajout de la checkbox membre actif (ou pas)
			add(new CheckBox("actif"));

			// Ajout de la liste des niveaux d'encadrement
			List<String> encadrement = new ArrayList<String>();
			for (Adherent.Encadrement e : Adherent.Encadrement.values()) {
				encadrement.add(e.toString());
			}
			DropDownChoice encadrt = new DropDownChoice("encadrement", encadrement);
			add(encadrt);

			// Ajout des roles
			List<String> roles = Arrays.asList(new String[] { "ADMIN", "USER",
					"SECRETARIAT" });
			add(new ListMultipleChoice<String>("roles", roles));
			
			//Ajout des nouveaux champs date du certificat medical et de l'année de cotisation
			DateTextField dateCMTextFiled = new DateTextField("dateCM", new PropertyModel<Date>(model, "dateCM"), new StyleDateConverter("S-", true));
			dateCMTextFiled.setRequired(true);
			add(dateCMTextFiled);
			dateCMTextFiled.add(new DatePicker());
			
			Date dateDuJour = new Date();
			GregorianCalendar gc = new GregorianCalendar();
			gc.setTime(dateDuJour);
			int anneeCourante = gc.get(Calendar.YEAR);
			int nextAnnee = anneeCourante + 1;
			
			List<Integer> annees = new ArrayList<Integer>();
			annees.add(new Integer(anneeCourante));
			annees.add(new Integer(nextAnnee));
			
			DropDownChoice<Integer> listAnnee = new DropDownChoice<Integer>("anneeCotisation", annees);
			listAnnee.setRequired(true);
			add(listAnnee);
			
			add(new AjaxButton("validPlongee") {
				@Override
				// La validation doit se faire en Ajax car le formulaire de la
				// fenêtre principal n'y a pas accés
				// http://yeswicket.com/index.php?post/2010/04/26/G%C3%A9rer-facilement-les-fen%C3%AAtres-modales-avec-Wicket
				protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
					Adherent adherent = (Adherent) form.getModelObject();
					
					// Mise au format des noms et prénom
					adherent.setNom(adherent.getNom().toUpperCase());
					adherent.setPrenom((adherent.getPrenom().substring(0, 1).toUpperCase()) + (adherent.getPrenom().substring(1).toLowerCase()));

					// Mise à jour de l'adhérent
					try {
						ResaSession resaSession = (ResaSession) getApplication()
								.getSessionStore().lookup(getRequest());
						resaSession.getAdherentService().updateAdherent(adherent);

						setResponsePage(AccueilPage.class);
						
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

            add(new Button("cancel", new ResourceModel("button.cancel")));
		}

	}
}
