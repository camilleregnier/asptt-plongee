/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asptt.plongee.resa.wicket.page.admin.externe;

import com.asptt.plongee.resa.exception.TechnicalException;
import com.asptt.plongee.resa.model.Adherent;
import com.asptt.plongee.resa.util.CatalogueMessages;
import com.asptt.plongee.resa.wicket.ResaSession;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxLink;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.StringResourceModel;

/**
 *
 * @author ecus6396
 */
public class GererExternesConfSuppPanel extends Panel {

    private static final long serialVersionUID = 196724625616748115L;
    FeedbackPanel feedback = new FeedbackPanel("feedback");
    private boolean controle = true;

    @SuppressWarnings("unchecked")
    public GererExternesConfSuppPanel(String id, final IModel<Adherent> externe) {
        super(id);
        init(externe);
    }

    private void init(final IModel<Adherent> externe) {

        // feedback panel pour renvoyer des messages sur la page
        feedback.setOutputMarkupId(true);
        add(feedback);

        // Informations (nom, prenom) du l'externe à supprimer
        add(new Label("info", new StringResourceModel(CatalogueMessages.ADHERENT_CONFIRME_SUPP, this, new Model<Adherent>(externe.getObject()))));

        add(new IndicatingAjaxLink("suppExt") {
            private static final long serialVersionUID = 1L;

            @Override
            public void onClick(AjaxRequestTarget target) {
                // On supprime l'externe
                try {
                    ResaSession.get().getAdherentService().supprimerExterne(externe.getObject(), controle);
                    GererExternes.getModalSupp().close(target);
                    setResponsePage(GererExternes.class);
                } catch (TechnicalException te) {
                    target.addComponent(feedback);
                    error(te.getKey());
                    controle = false;
                }
            }
           
        });

        // Le lien qui va juste fermer la fenêtre de confirmation
        add(new IndicatingAjaxLink("cancel") {
            private static final long serialVersionUID = 1L;

            @Override
            public void onClick(AjaxRequestTarget target) {
                GererExternes.getModalSupp().close(target);
            }
        });

    }
}
