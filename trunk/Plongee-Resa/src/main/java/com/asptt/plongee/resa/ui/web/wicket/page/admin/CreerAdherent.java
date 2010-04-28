package com.asptt.plongee.resa.ui.web.wicket.page.admin;

import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.CompoundPropertyModel;

import com.asptt.plongee.resa.model.Adherent;
import com.asptt.plongee.resa.ui.web.wicket.page.AccueilPage;
import com.asptt.plongee.resa.ui.web.wicket.page.TemplatePage;
import com.asptt.plongee.resa.ui.web.wicket.page.inscription.InscriptionConfirmationPlongeePage;

public class CreerAdherent extends TemplatePage {
	
	public CreerAdherent() {
		//super();
		add(new MyForm("inputForm"));
	}

	public class MyForm extends Form {

		/**
		 * 
		 */
		private static final long serialVersionUID = 5374674730458593314L;

		public MyForm(String id) {
			super(id, new CompoundPropertyModel(new Adherent()));
			add(new TextField("nom"));
			add(new TextField("prenom"));
			add(new TextField("numeroLicense"));
			add(new TextField("telephone"));
			add(new TextField("mail"));
		}
		public void onSubmit() {
			Adherent adherent = (Adherent) getModelObject();
			// TODO appeler le service adherent pour creer
			getResaSession().getAdherentService().creerAdherent(adherent);
			System.out.println(adherent.toString());
			setResponsePage(AccueilPage.class);
		}
		
	}

}
