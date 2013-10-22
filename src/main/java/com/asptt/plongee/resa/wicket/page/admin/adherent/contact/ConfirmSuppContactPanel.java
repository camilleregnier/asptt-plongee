/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asptt.plongee.resa.wicket.page.admin.adherent.contact;

import com.asptt.plongee.resa.model.Adherent;
import com.asptt.plongee.resa.model.ContactUrgent;
import com.asptt.plongee.resa.util.CatalogueMessages;
import java.util.List;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxLink;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.StringResourceModel;

/**
 *
 * @author ecus6396
 */
public class ConfirmSuppContactPanel extends Panel {

    private static final long serialVersionUID = 196724625616748115L;

    @SuppressWarnings("unchecked")
    public ConfirmSuppContactPanel(String id, final ContactUrgent contact) {
        super(id);

        // Informations (nom, prenom) du contact à supprimer
        add(new Label("info", new StringResourceModel(CatalogueMessages.CONTACT_CONFIRME_SUPP, this, new Model<ContactUrgent>(contact))));

        // Le lien qui va fermer la fenêtre de confirmation
        // et appeler la méthode de d'inscription en liste d'attente si nécessaire
        add(new IndicatingAjaxLink("yes") {
            private static final long serialVersionUID = 1L;

            @Override
            public void onClick(AjaxRequestTarget target) {
                // On supprime le contact
                Adherent adh = (Adherent) ContactPanel.getModelAdherent().getObject();
                List<ContactUrgent> l_cu = adh.getContacts();
                l_cu.remove(contact);
                adh.setContacts(l_cu);
                target.addComponent(ContactPanel.getCuView().getParent());
                ContactPanel.getModalConfirmSupp().close(target);
            }
        });

        // Le lien qui va juste fermer la fenêtre de confirmation
        add(new IndicatingAjaxLink("no") {
            private static final long serialVersionUID = 1L;

            @Override
            public void onClick(AjaxRequestTarget target) {
                ContactPanel.getModalConfirmSupp().close(target);
            }
        });

    }
}
