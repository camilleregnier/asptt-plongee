package com.asptt.plongee.resa.wicket.page.secretariat;

import com.asptt.plongee.resa.exception.TechnicalException;
import com.asptt.plongee.resa.model.Adherent;
import com.asptt.plongee.resa.model.NiveauAutonomie;
import com.asptt.plongee.resa.wicket.ResaSession;
import com.asptt.plongee.resa.wicket.page.inscription.InscriptionPlongeePage;
import java.util.ArrayList;
import java.util.List;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.validation.validator.EmailAddressValidator;
import org.apache.wicket.validation.validator.PatternValidator;
import org.apache.wicket.validation.validator.StringValidator.ExactLengthValidator;

public class ExterieurPanel extends Panel {

    private CompoundPropertyModel modelAdherent;

    public ExterieurPanel(String id) {
        super(id);

        setOutputMarkupId(true);
        add(new ExterieurForm("inputForm"));

    }

    class ExterieurForm extends Form {

        private static final long serialVersionUID = 5374674730458593314L;

        public ExterieurForm(String id) {
            super(id);

            // feedback panel pour renvoyer des messages sur la page
            final FeedbackPanel feedback = new FeedbackPanel("feedback");
            feedback.setOutputMarkupId(true);
            add(feedback);

            modelAdherent = new CompoundPropertyModel(new Adherent());
            setModel(modelAdherent);

            add(new RequiredTextField<String>("nom"));
            add(new RequiredTextField<String>("prenom"));
            add(new RequiredTextField<String>("mail").add(EmailAddressValidator
                    .getInstance()));

            // numéro de téléphone au bon format (10 caractères numériques)
            RequiredTextField<String> telephone = new RequiredTextField<String>("telephone", String.class);
            telephone.add(ExactLengthValidator.exactLength(10));
            telephone.add(new PatternValidator("\\d{10}"));
            add(telephone);

            // Ajout de la liste des niveaux
            List<String> niveaux = new ArrayList<String>();
            for (NiveauAutonomie n : NiveauAutonomie.values()) {
                niveaux.add(n.toString());
            }
            add(new DropDownChoice("niveau", niveaux));

            add(new AjaxButton("validExt") {
                @Override
                // La validation doit se faire en Ajax car le formulaire de la
                // fenêtre principal n'y a pas accés
                // http://yeswicket.com/index.php?post/2010/04/26/G%C3%A9rer-facilement-les-fen%C3%AAtres-modales-avec-Wicket
                protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                    Adherent adherent = (Adherent) form.getModelObject();
                    try {
                        // Création de l'exterieur
                        ResaSession.get().getAdherentService().creerExterne(adherent);
                        setResponsePage(new InscriptionPlongeePage(adherent));

                    } catch (TechnicalException e) {
                        e.printStackTrace();
                        error(e.getKey());
                    } finally {
                        target.addComponent(feedback);
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