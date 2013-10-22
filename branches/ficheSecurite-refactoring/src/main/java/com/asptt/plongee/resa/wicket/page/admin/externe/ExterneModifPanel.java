package com.asptt.plongee.resa.wicket.page.admin.externe;

import com.asptt.plongee.resa.exception.TechnicalException;
import com.asptt.plongee.resa.model.Adherent;
import com.asptt.plongee.resa.model.NiveauAutonomie;
import com.asptt.plongee.resa.wicket.ResaSession;
import java.util.ArrayList;
import java.util.List;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxLink;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.validation.validator.EmailAddressValidator;
import org.apache.wicket.validation.validator.PatternValidator;
import org.apache.wicket.validation.validator.StringValidator.ExactLengthValidator;

public class ExterneModifPanel extends Panel {

    private CompoundPropertyModel modelExterne;

    public ExterneModifPanel(String id, IModel<Adherent> im_externe) {
        super(id, im_externe);
        setOutputMarkupId(true);
        add(new ExterieurForm("inputForm", im_externe));
    }

    class ExterieurForm extends Form {

        private static final long serialVersionUID = 5374674730458593314L;

        public ExterieurForm(String id, IModel<Adherent> im_externe) {
            super(id, im_externe);

            // feedback panel pour renvoyer des messages sur la page
            final FeedbackPanel feedback = new FeedbackPanel("feedback");
            feedback.setOutputMarkupId(true);
            add(feedback);

            modelExterne = new CompoundPropertyModel(im_externe);
            setModel(modelExterne);

            add(new Label("license", im_externe.getObject().getNumeroLicense()));
            add(new Label("nom", im_externe.getObject().getNom()));
            add(new Label("prenom", im_externe.getObject().getPrenom()));
            add(new RequiredTextField<String>("mail").add(EmailAddressValidator.getInstance()));

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

            add(new AjaxButton("modifExt") {
                @Override
                // La validation doit se faire en Ajax car le formulaire de la
                // fenêtre principal n'y a pas accés
                // http://yeswicket.com/index.php?post/2010/04/26/G%C3%A9rer-facilement-les-fen%C3%AAtres-modales-avec-Wicket
                protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                    Adherent externe = (Adherent) form.getModelObject();
                    try {
                        // Modification de l'externe
                        ResaSession.get().getAdherentService().modifierExterne(externe);
                        setVisible(false);
                        setEnabled(false);
                        setResponsePage(new GererExternes());
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

            // Le lien qui va juste fermer la fenêtre de confirmation
            add(new IndicatingAjaxLink("cancel") {
                private static final long serialVersionUID = 1L;

                @Override
                public void onClick(AjaxRequestTarget target) {
                    GererExternes.getModalModif().close(target);;
                }
            });

        }
    }
}