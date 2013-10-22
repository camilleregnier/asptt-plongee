package com.asptt.plongee.resa.wicket.page.admin.plongee.attente;

import com.asptt.plongee.resa.exception.ResaException;
import com.asptt.plongee.resa.exception.TechnicalException;
import com.asptt.plongee.resa.model.Adherent;
import com.asptt.plongee.resa.model.Plongee;
import com.asptt.plongee.resa.util.CatalogueMessages;
import com.asptt.plongee.resa.wicket.ResaSession;
import com.asptt.plongee.resa.wicket.page.AccueilPage;
import com.asptt.plongee.resa.wicket.page.TemplatePage;
import com.asptt.plongee.resa.wicket.page.inscription.InscriptionConfirmationPlongeePage;
import java.util.ArrayList;
import java.util.List;
import org.apache.wicket.ResourceReference;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.extensions.markup.html.form.palette.Palette;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.model.util.CollectionModel;
import org.apache.wicket.model.util.ListModel;

public class GererListeAttenteTwo extends TemplatePage {

    private FeedbackPanel feedback = new FeedbackPanel("feedback");

    public GererListeAttenteTwo(final Plongee plongee) {
        setPageTitle("Gerer les listes d'attente");

        feedback.setOutputMarkupId(true);
        add(feedback);
        add(new Label("message1", new StringResourceModel(CatalogueMessages.LISTE_ATTENTE_TWO_1, this, null)));
        add(new Label("message2", new StringResourceModel(CatalogueMessages.LISTE_ATTENTE_TWO_2, this, null)));
        List<Adherent> persons = ResaSession.get().getPlongeeService().rechercherListeAttente(plongee);
        IChoiceRenderer<Adherent> renderer = new ChoiceRenderer<Adherent>("nomComplet", "nom");

        final Palette<Adherent> palette = new Palette<Adherent>("palette",
                new ListModel<Adherent>(new ArrayList<Adherent>()),
                new CollectionModel<Adherent>(persons),
                renderer, 10, false) {
            // Modification de la feuille de style
            // pour agrandir la largeur de la palette
            protected ResourceReference getCSS() {
                return new ResourceReference(GererListeAttenteTwo.class, "PlongeePalette.css");
            }
        };

        Form<?> form = new Form("form") {
        };

        AjaxButton valid = new AjaxButton("valid") {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {

                IModel modelAdherents = palette.getDefaultModel();
                List<Adherent> adherents = (List<Adherent>) modelAdherents.getObject();
                for (Adherent adherent : adherents) {
                    ResaSession.get().getPlongeeService().fairePasserAttenteAInscrit(plongee, adherent);
                }
                setResponsePage(new InscriptionConfirmationPlongeePage(plongee));
            }

            // L'implémentation de cette méthode est nécessaire pour voir
            // les messages d'erreur dans le feedBackPanel
            protected void onError(AjaxRequestTarget target, Form<?> form) {
                target.addComponent(feedback);
            }
        };

        Link cancel = new Link("cancel") {
            @Override
            public void onClick() {
                setResponsePage(AccueilPage.class);
            }
        };

        AjaxButton supp = new AjaxButton("supprimer") {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                IModel modelAdherents = palette.getDefaultModel();
                List<Adherent> adherents = (List<Adherent>) modelAdherents.getObject();
                for (Adherent adherent : adherents) {
                    try {
                        ResaSession.get().getPlongeeService().supprimerDeLaListeDattente(plongee, adherent, 1);
                    } catch (TechnicalException e) {
                        e.printStackTrace();
                        error(e.getKey());
                    } catch (ResaException e) {
                        e.printStackTrace();
                        error(e.getKey());
                    } finally {
                        target.addComponent(feedback);
                    }
                }
                setResponsePage(new GererListeAttenteTwo(plongee));
            }

            protected void onError(AjaxRequestTarget target, Form<?> form) {
                target.addComponent(feedback);
            }
        };

        form.add(valid);
        form.add(cancel);
        form.add(supp);
        add(form);
        form.add(palette);
    }
}
