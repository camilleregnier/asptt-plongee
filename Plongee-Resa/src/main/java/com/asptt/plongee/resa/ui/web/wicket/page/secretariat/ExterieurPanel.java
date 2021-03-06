package com.asptt.plongee.resa.ui.web.wicket.page.secretariat;

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

import com.asptt.plongee.resa.exception.TechnicalException;
import com.asptt.plongee.resa.model.Adherent;
import com.asptt.plongee.resa.model.NiveauAutonomie;
import com.asptt.plongee.resa.ui.web.wicket.ResaSession;
import com.asptt.plongee.resa.ui.web.wicket.page.inscription.InscriptionPlongeePage;

public class ExterieurPanel extends Panel {

    private CompoundPropertyModel modelAdherent;
    private Adherent parrain;

    public ExterieurPanel(String id) {
        super(id);

        setOutputMarkupId(true);
        add(new ExterieurForm("inputForm"));

    }

    public ExterieurPanel(String id, Adherent parrain) {
        super(id);
        this.parrain = parrain;

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

            // Ajout de la liste des aptitudes
            List<String> aptitudes = new ArrayList<String>();
            for (Adherent.Aptitude apt : Adherent.Aptitude.values()) {
                aptitudes.add(apt.toString());
            }
            add(new DropDownChoice("aptitude", aptitudes));

            add(new AjaxButton("validExt") {
                @Override
                // La validation doit se faire en Ajax car le formulaire de la
                // fenêtre principal n'y a pas accés
                // http://yeswicket.com/index.php?post/2010/04/26/G%C3%A9rer-facilement-les-fen%C3%AAtres-modales-avec-Wicket
                protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                    Adherent adherent = (Adherent) form.getModelObject();
                    try {
                        // Création de l'exterieur
                        ResaSession resaSession = (ResaSession) getApplication()
                                .getSessionStore().lookup(getRequest());

                        resaSession.getAdherentService().creerExterne(adherent);

                        // Si il y a un parrain, l'adhérent passé en paramètre est de fait un filleul
                        setResponsePage(new InscriptionPlongeePage(parrain, adherent));

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
