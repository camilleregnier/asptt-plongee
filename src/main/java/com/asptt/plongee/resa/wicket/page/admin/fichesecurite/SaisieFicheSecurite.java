package com.asptt.plongee.resa.wicket.page.admin.fichesecurite;

import com.asptt.plongee.resa.model.Adherent;
import com.asptt.plongee.resa.model.AdherentDataProvider;
import com.asptt.plongee.resa.model.FicheSecurite;
import com.asptt.plongee.resa.model.NavigationOriginePage;
import com.asptt.plongee.resa.model.Plongee;
import com.asptt.plongee.resa.util.FocusOnLoadBehavior;
import com.asptt.plongee.resa.util.ResaUtil;
import com.asptt.plongee.resa.wicket.ResaSession;
import com.asptt.plongee.resa.wicket.page.TemplatePage;
import com.asptt.plongee.resa.wicket.page.admin.plongee.AnnulerPlongeePanel;
import java.util.List;
import org.apache.log4j.Logger;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxLink;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.StringResourceModel;

public class SaisieFicheSecurite extends TemplatePage {

    private final Logger logger = Logger.getLogger(getClass());
    FeedbackPanel feedback = new FeedbackPanel("feedback");
    private Plongee plongee;
    private FicheSecurite fs;
    private static ModalWindow modalAnnulPlongee;
    private static ModalWindow modalAjoutPlongeur;
    private static ModalWindow modalSuppPlongeur;

    public SaisieFicheSecurite() {
        setPageTitle("Fiches de Sécurité Saisie");
        // le label pour le message
        add(new Label("message",new StringResourceModel("message",this,null)));
        //le feedback panel pour les message d'erreur
        feedback.setOutputMarkupId(true);
        add(feedback);

        //Récupération de la fiche de securite dans la session
        fs = ResaSession.get().getFicheSecurite();
        //init de la plongée
        this.plongee = fs.getPlongee();

        //Ajout de la Form
        add(new SaisieFicheSecuriteForm("fsForm", new CompoundPropertyModel(fs)));

        // Fenêtre modale pour l'annulation de la plongée
        modalAnnulPlongee = new ModalWindow("modalAnnulPlongee");
        modalAnnulPlongee.setTitle("Informations sur la plong\u00e9e a annuler");
        modalAnnulPlongee.setUseInitialHeight(false);
        modalAnnulPlongee.setWidthUnit("px");
        add(modalAnnulPlongee);

        // Fenêtre modale pour l'ajout d'un plongeur
        modalAjoutPlongeur = new ModalWindow("modalAjoutPlongeur");
        modalAjoutPlongeur.setTitle("Ajout d'un plongeur");
        modalAjoutPlongeur.setResizable(true);
        modalAjoutPlongeur.setInitialWidth(450);
        modalAjoutPlongeur.setInitialHeight(300);
        modalAjoutPlongeur.setWidthUnit("px");
        modalAjoutPlongeur.setHeightUnit("px");
        modalAjoutPlongeur.setCssClassName(ModalWindow.CSS_CLASS_BLUE);
        modalAjoutPlongeur.setUseInitialHeight(true);
        add(modalAjoutPlongeur);

        // Fenêtre modale pour la suppression d'un plongeur
        modalSuppPlongeur = new ModalWindow("modalSuppPlongeur");
        modalSuppPlongeur.setTitle("Suppression d'un plongeur");
        modalSuppPlongeur.setResizable(true);
        modalSuppPlongeur.setInitialWidth(450);
        modalSuppPlongeur.setInitialHeight(300);
        modalSuppPlongeur.setWidthUnit("px");
        modalSuppPlongeur.setHeightUnit("px");
        modalSuppPlongeur.setCssClassName(ModalWindow.CSS_CLASS_BLUE);
        modalSuppPlongeur.setUseInitialHeight(true);
        add(modalSuppPlongeur);

    }

    public class SaisieFicheSecuriteForm extends Form {

        private static final long serialVersionUID = -1555366090072306934L;
        private List<Adherent> data;

        @SuppressWarnings("serial")
        public SaisieFicheSecuriteForm(String name, CompoundPropertyModel<FicheSecurite> fsModel) {

            super(name);
            setModel(fsModel);

            //init de la date de la plongée
            String dateAffiche = ResaUtil.getDateString(plongee.getDate());
            TextField<String> dateField = new TextField<String>("date", Model.of(dateAffiche));
            dateField.setEnabled(false);
            add(dateField);

            //Init du Type de la plongée
            String typePlongee = plongee.getType();
            TextField<String> typeField = new TextField<String>("type", Model.of(typePlongee));
            typeField.setEnabled(false);
            add(typeField);

            //Init du Site de la plongée
            add(new RequiredTextField<String>("site").add(new FocusOnLoadBehavior()));

            //Init de la Météo
            add(new TextField<String>("meteo"));

            //init du pilote a partir de la fiche de securite
            TextField<String> piloteField = new TextField<String>("pilote", new PropertyModel<String>(fsModel, "nomPilote"));
            add(piloteField);

            //init du DP à partir de la fiche de securite
            TextField<String> dpField = new TextField<String>("dp", new PropertyModel<String>(fsModel, "nomDP"));
            add(dpField);

            data = ResaSession.get().getAdherentService().rechercherAdherentInscrits(plongee);

            AdherentDataProvider pDataProvider = new AdherentDataProvider(data);

//-----------------------------DEBUT de la definition de la liste des Plongeurs ------------------------
            add(new DataView<Adherent>("simple", pDataProvider) {
                @Override
                protected void populateItem(final Item<Adherent> item) {
                    Adherent adherent = item.getModelObject();

                    item.add(new Label("nom", adherent.getNom()));
                    item.add(new Label("prenom", adherent.getPrenom()));

                    // Dès que le plongeur est encadrant, on affiche son niveau d'encadrement
                    String niveauAffiche = adherent.getPrerogative();

                    // Pour les externes, le niveau est suffixé par (Ext.)
                    if (adherent.getActifInt() == 2) {
                        niveauAffiche = niveauAffiche + " (Ext.)";
                    }

                    item.add(new Label("niveau", niveauAffiche));

                    item.add(new IndicatingAjaxLink("suppAdh") {
                        @Override
                        public void onClick(AjaxRequestTarget target) {
                            appelModalSuppPlongeur(target, item.getModel());
                            modalSuppPlongeur.show(target);
                        }
                    });

                    item.add(new AttributeModifier("class", true,
                            new AbstractReadOnlyModel<String>() {
                                @Override
                                public String getObject() {
                                    return (item.getIndex() % 2 == 1) ? "even"
                                            : "odd";
                                }
                            }));
                }
            });
//-----------------------------FIN de la definition de la liste des Plongeurs ------------------------

            add(new AjaxButton("annulPlongee") {
                @Override
                protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                    FicheSecurite ficheSecu = (FicheSecurite) form.getModelObject();
                    appelModalAnnulPlongee(target, new Model(ficheSecu.getPlongee()));
                    modalAnnulPlongee.show(target);
                }

                @Override
                protected void onError(AjaxRequestTarget target, Form<?> form) {
                    target.addComponent(feedback);
                }
            });

            add(new AjaxButton("addPlongeur") {
                @Override
                protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                    FicheSecurite ficheSecu = (FicheSecurite) form.getModelObject();
                    ResaSession.get().setFicheSecurite(ficheSecu);
                    appelModalAjoutPlongeur(target, new Model(ficheSecu.getPlongee()));
                    modalAjoutPlongeur.show(target);
                }

                @Override
                protected void onError(AjaxRequestTarget target, Form<?> form) {
                    target.addComponent(feedback);
                }
            });

            add(new Link("cancel") {
                @Override
                public void onClick() {
                    setResponsePage(new GererListePlongeeAddFS());
                }
            });

            add(new AjaxButton("saisiePalanque") {
                @Override
                protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                    setResponsePage(new SaisiePalanquePage());
                }

                @Override
                protected void onError(AjaxRequestTarget target, Form<?> form) {
                    target.addComponent(feedback);
                }
            });

        }
    }

    private void appelModalAnnulPlongee(AjaxRequestTarget target, IModel<Plongee> plongee) {
        modalAnnulPlongee.setContent(
                new AnnulerPlongeePanel(
                modalAnnulPlongee.getContentId(),
                plongee,
                NavigationOriginePage.FICHE_SECURITE.name()));
        // Pour éviter le message de disparition de la fenetre lors de la validation
        target.appendJavascript("Wicket.Window.unloadConfirmation  = false;");
    }

    private void appelModalAjoutPlongeur(AjaxRequestTarget target, IModel<Plongee> plongee) {
        modalAjoutPlongeur.setContent(
                new SaisieFSAjoutPlongeurPanel(modalAjoutPlongeur.getContentId()));
        // Pour éviter le message de disparition de la fenetre lors de la validation
        target.appendJavascript("Wicket.Window.unloadConfirmation  = false;");
    }

    private void appelModalSuppPlongeur(AjaxRequestTarget target, IModel<Adherent> adherent) {
        modalSuppPlongeur.setContent(
                new SaisieFSConfSuppPlongeurPanel(modalSuppPlongeur.getContentId(), adherent));
        // Pour éviter le message de disparition de la fenetre lors de la validation
        target.appendJavascript("Wicket.Window.unloadConfirmation  = false;");
    }
}
