package com.asptt.plongee.resa.wicket.page;

import com.asptt.plongee.resa.wicket.ResaSession;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.Model;
import org.apache.wicket.validation.validator.StringValidator.ExactLengthValidator;

public class ModifPasswordPage extends WebPage {

    public ModifPasswordPage() {
        super();
        add(new ModifPasswordForm("modifPasswordForm"));
    }

    private class ModifPasswordForm extends Form {

        public ModifPasswordForm(String id) {
            super(id);

            final FeedbackPanel feedback = new FeedbackPanel("feedback");
            feedback.setOutputMarkupId(true);
            add(feedback);

            final PasswordTextField oldPassword = new PasswordTextField(
                    "oldPassword", new Model<String>());
            final PasswordTextField newPassword1 = new PasswordTextField(
                    "newPassword1", new Model<String>());
            final PasswordTextField newPassword2 = new PasswordTextField(
                    "newPassword2", new Model<String>());

            // Contrainte sur le password : entre 6 et 8 caractères
            oldPassword.add(ExactLengthValidator.lengthBetween(6, 8));
            newPassword1.add(ExactLengthValidator.lengthBetween(6, 8));
            newPassword2.add(ExactLengthValidator.lengthBetween(6, 8));

            add(oldPassword.setRequired(true));
            add(newPassword1.setRequired(true));
            add(newPassword2.setRequired(true));

            add(new Link("cancel") {
                @Override
                public void onClick() {
                    setResponsePage(LoginPage.class);
                }
            });

            add(new AjaxButton("validPassword") {
                @Override
                protected void onSubmit(AjaxRequestTarget target, Form<?> form) {

                    // L'ancien mot de passe saisi doit bien correspondre à l'ancien
                    if (oldPassword.getValue().equalsIgnoreCase(
                            ((ResaSession) getSession()).getAdherent()
                            .getPassword())) {
                        // Si les 2 nvx password sont égaux
                        if (newPassword1.getValue().equals(
                                newPassword2.getValue())) {
                            // Et qu'il sont différent de l'ancien mot de passe
                            if (!newPassword1.getValue().equals(
                                    oldPassword.getValue())) {
                                // Mise à jour du mot de passe
                                ((ResaSession) getSession()).getAdherent()
                                        .setPassword(newPassword1.getValue());
                                // Appel du service
                                ((ResaSession) getSession()).getAdherentService().updatePasswordAdherent(
                                        ((ResaSession) getSession()).getAdherent());

                                // Redirection vers l'accueil
                                setResponsePage(AccueilPage.class);
                            } else {
                                // Re-saisie du nouveau mot de passe
                                error("Le nouveau mot de passe doit \u00eatre diff\u00e9rent de celui d'origine.");
                            }
                        } else {
                            // Re-saisie du nouveau mot de passe
                            error("Erreur dans la saisie du nouveau mot de passe !");
                        }
                    } else {
                        // Re-saisie du nouveau mot de passe
                        error("L'ancien mot de passe n'est pas correct !");
                    }

                }

                // L'implémentation de cette méthode est nécessaire pour voir
                // les messages d'erreur dans le feedBackPanel
                protected void onError(AjaxRequestTarget target, Form<?> form) {
                    target.addComponent(feedback);
                }
            });//Fin du add

        }//Fin du constructeur de l'inner class ModifPasswordForm
    }//Fin de la class ModifPasswordForm
}
