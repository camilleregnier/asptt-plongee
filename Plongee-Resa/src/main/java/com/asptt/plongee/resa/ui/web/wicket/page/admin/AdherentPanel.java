package com.asptt.plongee.resa.ui.web.wicket.page.admin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.behavior.BehaviorsUtil;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.ListMultipleChoice;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.validation.validator.EmailAddressValidator;

import com.asptt.plongee.resa.model.Adherent;
import com.asptt.plongee.resa.model.NiveauAutonomie;
import com.asptt.plongee.resa.ui.web.wicket.ResaSession;
import com.asptt.plongee.resa.ui.web.wicket.page.AccueilPage;

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
			

			add(new RequiredTextField<String>("nom"));
			add(new RequiredTextField<String>("prenom"));
			add(new RequiredTextField<Integer>("numeroLicense", Integer.class));

			// TODO à modifier plus tard pour validation
			add(new RequiredTextField<Integer>("telephone", Integer.class));
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

			// Ajout de la checkbox directeur de plongée
			final CheckBox checkDp = new CheckBox("dp");

			//checkDp.setOutputMarkupId(true);
			add(checkDp);

			// Ajout de la liste des niveaux d'encadrement
			List<String> encadrement = new ArrayList<String>();
			for (Adherent.Encadrement e : Adherent.Encadrement.values()) {
				encadrement.add(e.toString());
			}
			DropDownChoice encadrt = new DropDownChoice("encadrement", encadrement);
//			encadrt.add (new AjaxFormComponentUpdatingBehavior("onchange"){
//		            protected void onUpdate(AjaxRequestTarget target) {
//		            	CheckBox check = (CheckBox)get("dp");
//		            	Adherent a = (Adherent) model.getChainedModel().getObject();
//		                if (a.getEncadrement().contains("E"))
//		                	a.setDp(true);
//		                model.setObject(a);
//		                checkDp.setModel(model.bind("dp"));
//		                target.addComponent(checkDp);
//		            }
//				});
			add(encadrt);

			// Ajout des roles
			List<String> roles = Arrays.asList(new String[] { "ADMIN", "USER",
					"SECRETARIAT" });
			add(new ListMultipleChoice<String>("roles", roles));

		}

		public void onSubmit() {
			Adherent adherent = (Adherent) getModelObject();

			// Mise à jour de l'adhérent
			ResaSession resaSession = (ResaSession) getApplication()
					.getSessionStore().lookup(getRequest());
			resaSession.getAdherentService().updateAdherent(adherent);

			setResponsePage(AccueilPage.class);
		}

	}
}
