package com.asptt.plongee.resa.ui.web.wicket.page;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.Model;

import com.asptt.plongee.resa.ui.web.wicket.ResaSession;

public class ModifPasswordPage extends WebPage {

	public ModifPasswordPage() {
		super();
		add(new ModifPasswordForm("modifPasswordForm"));
	}
	
	private class ModifPasswordForm extends Form{

		public ModifPasswordForm(String id) {
			super(id);
			
			final FeedbackPanel feedback = new FeedbackPanel("feedback");
			feedback.setOutputMarkupId(true);
			add(feedback);
			
			final PasswordTextField oldPassword = new PasswordTextField("oldPassword", new Model<String>());
			final PasswordTextField newPassword1 = new PasswordTextField("newPassword1", new Model<String>());
			final PasswordTextField newPassword2 = new PasswordTextField("newPassword2", new Model<String>());
			
			add(oldPassword.setRequired(true));
			add(newPassword1.setRequired(true));
			add(newPassword2.setRequired(true));
			
			add(new AjaxButton("validPlongee") {
				
				@Override
				protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
					// Si les 2 nvx password sont égaux
					if (newPassword1.getValue().equals(newPassword2.getValue())){
						// Et qu'il sont différent de l'ancien mot de passe
						if (!newPassword1.getValue().equals(oldPassword.getValue())){
							// Mise à jour du mot de passe
							((ResaSession) getSession()).getAdherent().setPassword(newPassword1.getValue());
							
							// Redirection vers l'accueil
							setResponsePage(AccueilPage.class);
						}else{
							// Re-saisie du nouveau mot de passe
							error("Le nouveau mot de passe doit être différent de celui d'origine");
						}
					}else{
						// Re-saisie du nouveau mot de passe
						error("Erreur dans la saisie du nouveau mot de passe");
					}
					
				}
				
				// L'implémentation de cette méthode est nécessaire pour voir
				// les messages d'erreur dans le feedBackPanel
				protected void onError(AjaxRequestTarget target, Form<?> form) {
					target.addComponent(feedback);
				}
			});
		}
		
	}
	

}