/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asptt.plongee.resa.wicket.page.admin.plongee;

import com.asptt.plongee.resa.model.FicheSecurite;
import com.asptt.plongee.resa.model.Plongee;
import com.asptt.plongee.resa.util.CatalogueMessages;
import com.asptt.plongee.resa.util.ResaUtil;
import com.asptt.plongee.resa.wicket.ResaSession;
import com.asptt.plongee.resa.wicket.page.AccueilPage;
import com.asptt.plongee.resa.wicket.page.admin.fichesecurite.GererListePlongeeAddFS;
import com.asptt.plongee.resa.wicket.page.admin.fichesecurite.SaisieFicheSecurite;
import org.apache.log4j.Logger;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.validation.validator.StringValidator;

/**
 *
 * @author ecus6396
 */
public class AnnulerPlongeePanel extends Panel {

    private final Logger logger = Logger.getLogger(getClass());
    FeedbackPanel feedback = new FeedbackPanel("feedback");
    private final Plongee plongee;
    private final String oriPage;

    public AnnulerPlongeePanel(String id, IModel<Plongee> plongeeModel, String originePage) {
        super(id, plongeeModel);
        feedback.setOutputMarkupId(true);
        add(feedback);
        setDefaultModel(plongeeModel);
        setVisible(true);
        plongee = plongeeModel.getObject();
        oriPage = originePage;

        // Informations précisant la plongeur concerné et la plongée
        // dans la fenêtre de confirmation de désinscription
        IModel<Plongee> model = new Model<Plongee>(plongee);
        StringResourceModel srmPlongee = new StringResourceModel(CatalogueMessages.ANNULATION_CONFIRMATION_PLONGEE, this, model,
                new Object[]{ResaUtil.getDateString(plongee.getDate()), new PropertyModel<Plongee>(model, "getType")});
        add(new Label("infoPlongee", srmPlongee.getString()));

        int nbPlongeursInscrits = plongee.getParticipants().size();
        String msgInfoPlongeur;
        if (nbPlongeursInscrits == 0) {
            msgInfoPlongeur = " ";
        } else {
            StringResourceModel srmPlongeur = new StringResourceModel(CatalogueMessages.ANNULATION_CONFIRMATION_PLONGEUR, this, model,
                    new Object[]{nbPlongeursInscrits});
            msgInfoPlongeur = srmPlongeur.getString();
        }
        add(new Label("infoPlongeurs", msgInfoPlongeur));
        add(new AnnulerPlongeeForm("annulPlongeeForm"));
    }

    public class AnnulerPlongeeForm extends Form {

        public AnnulerPlongeeForm(String id) {
            super(id);
            feedback.setOutputMarkupId(true);
            //Init du motif d'annulation de la plongée
            TextArea<String> motifField = new TextArea<String>("motif", Model.of(""));
            motifField.add(StringValidator.maximumLength(45));
            motifField.setRequired(true);
            add(motifField);

            // Le lien qui va fermer la fenêtre de confirmation
            // et appeler la méthode de désinscription de la page principale (DesInscriptionPlongeePage)
            add(new AjaxButton("validAnnulPlongee") {
                @Override
                protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                    setVisible(false);
                    // appel du service d'annulation de la plongée;
                    ResaSession.get().getPlongeeService().supprimerPlongee(plongee);
                    if(oriPage.equalsIgnoreCase("ANNULATION")){
                        setResponsePage(AccueilPage.class);
                    } else {
                        setResponsePage(new GererListePlongeeAddFS());
                    }
                }

                @Override
                protected void onError(AjaxRequestTarget target, Form<?> form) {
                    target.addComponent(feedback);
                }
            });

            //Bouton retour
            add(new Link("cancel") {
                @Override
                public void onClick() {
                    setVisible(false);
                    if(oriPage.equalsIgnoreCase("ANNULATION")){
                        setResponsePage(AnnulerPlongee.class);
                    } else {
                        setResponsePage( new SaisieFicheSecurite());
                    }
                }
            });

        }
    }
}
