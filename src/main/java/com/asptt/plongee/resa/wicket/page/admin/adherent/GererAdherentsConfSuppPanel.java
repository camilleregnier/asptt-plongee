/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asptt.plongee.resa.wicket.page.admin.adherent;

import com.asptt.plongee.resa.model.Adherent;
import com.asptt.plongee.resa.util.CatalogueMessages;
import com.asptt.plongee.resa.wicket.ResaSession;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxLink;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.StringResourceModel;

/**
 *
 * @author ecus6396
 */
public class GererAdherentsConfSuppPanel extends Panel {

    private static final long serialVersionUID = 196724625616748115L;

    @SuppressWarnings("unchecked")
    public GererAdherentsConfSuppPanel(String id, final IModel<Adherent> adherent) {
        super(id);
        init(adherent);
    }

    private void init(final IModel<Adherent> adherent) {

        // Informations (nom, prenom) du l'adherent à supprimer
        add(new Label("info", new StringResourceModel(CatalogueMessages.ADHERENT_CONFIRME_SUPP, this, new Model<Adherent>(adherent.getObject()))));

        // Le lien qui va fermer la fenêtre de confirmation
        // et appeler la méthode de d'inscription en liste d'attente si nécessaire
        add(new IndicatingAjaxLink("yes") {
            private static final long serialVersionUID = 1L;

            @Override
            public void onClick(AjaxRequestTarget target) {
                // On supprime l'adherent
                ResaSession.get().getAdherentService().supprimerAdherent(adherent.getObject());
                GererAdherents.getModalSupp().close(target);
                setResponsePage(GererAdherents.class);
            }
        });

        // Le lien qui va juste fermer la fenêtre de confirmation
        add(new IndicatingAjaxLink("no") {
            private static final long serialVersionUID = 1L;

            @Override
            public void onClick(AjaxRequestTarget target) {
                GererAdherents.getModalSupp().close(target);
            }
        });

    }
}
