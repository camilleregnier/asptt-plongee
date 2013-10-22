/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asptt.plongee.resa.wicket.page.admin.adherent.contact;

import com.asptt.plongee.resa.exception.TechnicalException;
import com.asptt.plongee.resa.model.Adherent;
import com.asptt.plongee.resa.model.ContactUrgent;
import java.util.ArrayList;
import java.util.List;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.validation.validator.PatternValidator;

/**
 *
 * @author ecus6396
 */
public class ModifContactPanel extends Panel {

    private CompoundPropertyModel<ContactUrgent> modelContactModif;

    public ModifContactPanel(String id, IModel<ContactUrgent> contact) {
        super(id, contact);
        setOutputMarkupId(true);
        add(new ModifContactPanel.ModifContactForm("modifForm", contact));
    }

    class ModifContactForm extends Form {

        private static final long serialVersionUID = 5374674730458593314L;

        public ModifContactForm(String id, IModel<ContactUrgent> contact) {
            super(id, contact);
            modelContactModif = new CompoundPropertyModel<ContactUrgent>(contact);
            setModel(modelContactModif);

            final FeedbackPanel feedback = new FeedbackPanel("feedback");
            feedback.setOutputMarkupId(true);
            add(feedback);

            // Ajout de la liste des titres
            List<String> titres = new ArrayList<String>();
            titres.add("Mr");
            titres.add("Mme");
            add(new DropDownChoice("titre", titres).setRequired(true));
            add(new RequiredTextField<String>("nom"));
            add(new RequiredTextField<String>("prenom"));

            // numéro de téléphone au bon format (10 caractères numériques)
            TextField<String> telephone = new TextField<String>("telephone", String.class);
            telephone.add(new PatternValidator("^0[0-9]{9}$"));
            add(telephone);

            // numéro de téléphone au bon format (10 caractères numériques) facultatif
            TextField<String> telephtwo = new TextField<String>("telephtwo", String.class);
            telephtwo.add(new PatternValidator("^(0[0-9]{9})?$"));
            add(telephtwo);

            add(new AjaxButton("modifContact") {
                @Override
                protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
//						ContactUrgent contact = (ContactUrgent) form.getModelObject();
                    Adherent adh = (Adherent) ContactPanel.getModelAdherent().getObject();
                    try {
                        //Pour rafraichir les contact dans la fenetre parent
                        List<ContactUrgent> l_cu = adh.getContacts();
                        adh.setContacts(l_cu);
                        target.addComponent(ContactPanel.getCuView().getParent());
                        ContactPanel.getModalContactModif().close(target);
                    } catch (TechnicalException e) {
                        e.printStackTrace();
                        error(e.getKey());
                    }

                }
                // L'implémentation de cette méthode est nécessaire pour voir
                // les messages d'erreur dans le feedBackPanel

                @Override
                protected void onError(AjaxRequestTarget target, Form<?> form) {
                    target.addComponent(feedback);
                }
            });
        }
    }
}
