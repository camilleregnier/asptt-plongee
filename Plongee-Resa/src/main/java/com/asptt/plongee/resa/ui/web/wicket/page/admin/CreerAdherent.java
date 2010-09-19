package com.asptt.plongee.resa.ui.web.wicket.page.admin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.authorization.strategies.role.Roles;
import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.ListChoice;
import org.apache.wicket.markup.html.form.ListMultipleChoice;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
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
			
			
			// Ajout de la checkbox directeur de plongée
			add(new CheckBox("dp", model.bind("dp")));
			
			//Ajout des roles
			List<String> roles = Arrays.asList(new String[] { "ADMIN", "USER", "SECRETARIAT" });
			add(new ListMultipleChoice<String>("roles", roles));
			
			add(new AjaxButton("validAdherent") {

				@Override
				protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
					Adherent adherent = (Adherent) form.getModelObject();
					try {
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

//		public void onSubmit() {
//			Adherent adherent = (Adherent) getModelObject();
//			
//			// Mise au format des noms et prénom
//			adherent.setNom(adherent.getNom().toUpperCase());
//			adherent.setPrenom((adherent.getPrenom().substring(0, 1).toUpperCase()) + (adherent.getPrenom().substring(1).toLowerCase()));
//			
//			getResaSession().getAdherentService().creerAdherent(adherent);
//
//			setResponsePage(AccueilPage.class);
//		}

	}

}
