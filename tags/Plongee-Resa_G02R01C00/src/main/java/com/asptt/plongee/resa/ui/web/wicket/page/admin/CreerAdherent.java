package com.asptt.plongee.resa.ui.web.wicket.page.admin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.authorization.strategies.role.Roles;
import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.datetime.StyleDateConverter;
import org.apache.wicket.datetime.markup.html.form.DateTextField;
import org.apache.wicket.extensions.yui.calendar.DatePicker;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.ListChoice;
import org.apache.wicket.markup.html.form.ListMultipleChoice;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.util.lang.EnumeratedType;
import org.apache.wicket.validation.validator.EmailAddressValidator;
import org.apache.wicket.validation.validator.PatternValidator;
import org.apache.wicket.validation.validator.StringValidator.ExactLengthValidator;

import com.asptt.plongee.resa.exception.ResaException;
import com.asptt.plongee.resa.exception.TechnicalException;
import com.asptt.plongee.resa.model.Adherent;
import com.asptt.plongee.resa.model.NiveauAutonomie;
import com.asptt.plongee.resa.model.Plongee;
import com.asptt.plongee.resa.model.Adherent.Encadrement;
import com.asptt.plongee.resa.ui.web.wicket.page.AccueilPage;
import com.asptt.plongee.resa.ui.web.wicket.page.TemplatePage;
import com.asptt.plongee.resa.ui.web.wicket.page.inscription.InscriptionConfirmationPlongeePage;

@AuthorizeInstantiation("ADMIN")
public class CreerAdherent extends TemplatePage {

	FeedbackPanel feedback = new FeedbackPanel("feedback");
	
	public CreerAdherent() {
		setPageTitle("Creer adherent");
		// Constructeur du formulaire et du feedback panel pour renvoyer des messages sur la page
		feedback.setOutputMarkupId(true);
		add(feedback);
		add(new MyForm("inputForm"));
	}

	public class MyForm extends Form {

		/**
		 * 
		 */
		private static final long serialVersionUID = 5374674730458593314L;

		public MyForm(String id) {
			super(id);
			CompoundPropertyModel model = new CompoundPropertyModel(new Adherent());
			setModel(model);

			add(new RequiredTextField<String>("nom"));
			add(new RequiredTextField<String>("prenom"));
			add(new RequiredTextField<String>("numeroLicense",String.class).add(new PatternValidator("\\d{6}")));
			
			// numéro de téléphone au bon format (10 caractères numériques)
			RequiredTextField<String> telephone = new RequiredTextField<String>("telephone", String.class);
			telephone.add(ExactLengthValidator.exactLength(10));
			telephone.add(new PatternValidator("\\d{10}"));
			add(telephone);
			
			add(new RequiredTextField<String>("mail").add(EmailAddressValidator.getInstance()));
			
			// Ajout de la liste des niveaux
			List<String> niveaux = new ArrayList<String>();
			for (NiveauAutonomie n : NiveauAutonomie.values()){
				niveaux.add(n.toString());
			}
			add(new DropDownChoice("niveau", niveaux));
			
			// Ajout de la liste des niveaux d'encadrement
			List<String> encadrement = new ArrayList<String>();
			for (Adherent.Encadrement e : Adherent.Encadrement.values()){
				encadrement.add(e.toString());
			}
			add(new DropDownChoice("encadrement", encadrement));
			
			// Ajout de la checkbox pilote
			add(new CheckBox("pilote", model.bind("pilote")));
			
			//Ajout des roles
			List<String> roles = Arrays.asList(new String[] { "ADMIN", "USER", "SECRETARIAT" });
			add(new ListMultipleChoice<String>("roles", roles));
			
			//Ajout du champs date du certificat medical
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
			
			// Ajout de la checkbox TIV
			add(new CheckBox("tiv", model.bind("tiv")));
			//commentaire
			TextArea<String> textareaInput = new TextArea<String>("commentaire");
			textareaInput.add(ExactLengthValidator.maximumLength(45));
			add(textareaInput);
			
	        ContactPanel cuPanel = new ContactPanel("cuPanel", model);
	        add(cuPanel);

	        add(new AjaxButton("validAdherent") {

				@Override
				protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
					Adherent adherent = (Adherent) form.getModelObject();
					try {
//						Date dateDuJour = new Date();
//						GregorianCalendar gc = new GregorianCalendar();
//						gc.setTime(dateDuJour);
//						int anneeCourante = gc.get(Calendar.YEAR);
//						adherent.setAnneeCotisation(new Integer(anneeCourante));
						getResaSession().getAdherentService().creerAdherent(adherent);

						setResponsePage(AccueilPage.class);
					}  catch (TechnicalException e) {
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
