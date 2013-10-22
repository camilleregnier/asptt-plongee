/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asptt.plongee.resa.wicket.page.fichesecurite;

import com.asptt.plongee.resa.exception.ResaException;
import com.asptt.plongee.resa.model.Adherent;
import com.asptt.plongee.resa.model.FicheSecurite;
import com.asptt.plongee.resa.model.Plongee;
import com.asptt.plongee.resa.util.CatalogueMessages;
import com.asptt.plongee.resa.wicket.ResaSession;
import org.apache.log4j.Logger;
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
public class SaisieFSConfSuppPlongeurPanel extends Panel{
    
    private final Logger logger = Logger.getLogger(getClass());
    FeedbackPanel feedback = new FeedbackPanel("feedback");
    private FicheSecurite fs;
    private Plongee plongee;
    
    public SaisieFSConfSuppPlongeurPanel(String id, IModel<Adherent> adherent) {
        super(id, adherent);
        this.fs = ResaSession.get().getFicheSecurite();
        this.plongee = fs.getPlongee();
        //le feedback panel pour les message d'erreur
        feedback.setOutputMarkupId(true);
        add(feedback);

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
                try {
                    // On supprime l'adherent
                    ResaSession.get().getPlongeeService().deInscrireAdherentForcee(plongee, adherent.getObject());
                } catch (ResaException ex) {
                    logger.warn("Pas glop"+ex);
                }
                //On met à jour la plongée dans le fiche de securite
                plongee = ResaSession.get().getPlongeeService().rechercherPlongeeParId(plongee.getId());
                fs.setPlongee(plongee);
                //On met à jour la fiche de securite dans la session
                ResaSession.get().setFicheSecurite(fs);
                //retour sur la page de saisie de la fiche de securite
                setResponsePage(new SaisieFicheSecurite());
            }
        });

        // Le lien qui va juste fermer la fenêtre de confirmation
        add(new IndicatingAjaxLink("no") {
            private static final long serialVersionUID = 1L;

            @Override
            public void onClick(AjaxRequestTarget target) {
                setResponsePage(new SaisieFicheSecurite());
            }
        });

    }
    
}
