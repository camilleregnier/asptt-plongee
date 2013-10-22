package com.asptt.plongee.resa.wicket.page.fichesecurite;

import com.asptt.plongee.resa.exception.ResaException;
import com.asptt.plongee.resa.exception.TechnicalException;
import com.asptt.plongee.resa.model.FicheSecurite;
import com.asptt.plongee.resa.model.Palanque;
import com.asptt.plongee.resa.model.PalanqueDataProvider;
import com.asptt.plongee.resa.model.Plongee;
import com.asptt.plongee.resa.util.ResaUtil;
import com.asptt.plongee.resa.wicket.ResaSession;
import com.asptt.plongee.resa.wicket.page.TemplatePage;
import java.util.List;
import org.apache.log4j.Logger;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;

public class RecapFicheSecurite extends TemplatePage {

    private final Logger logger = Logger.getLogger(getClass());
    FeedbackPanel feedback = new FeedbackPanel("feedback");
    private Plongee plongee;
    private FicheSecurite fs;
    private static ModalWindow modalAnnulPalanque;
    private static ModalWindow modalAjoutPalanque;
    private static ModalWindow modalSuppPalanque;

    public RecapFicheSecurite() {
        setPageTitle("Fiches de Sécurité Recap");
        // le label pour le message
        add(new Label("message"));
        //le feedback panel pour les message d'erreur
        feedback.setOutputMarkupId(true);
        add(feedback);

        //Récupération de la fiche de securite dans la session
        fs = ResaSession.get().getFicheSecurite();
        //init de la plongée
        this.plongee = fs.getPlongee();

        //date de la plongée
        String dateAffiche = ResaUtil.getDateString(plongee.getDate());
        Label dateLabel = new Label("date", Model.of(dateAffiche));
        add(dateLabel);

        //Type de la plongée
        String typePlongee = plongee.getType();
        Label typeLabel = new Label("type", Model.of(typePlongee));
        add(typeLabel);

        //Site de la plongée
        String site = fs.getSite();
        add(new Label("site", Model.of(site)));

        //Meteo de la plongée
        String meteo = fs.getMeteo();
        add(new Label("meteo", Model.of(meteo)));

        //pilote
        String pilote = fs.getNomPilote();
        add(new Label("pilote", Model.of(pilote)));

        //dp
        String dp = fs.getNomDP();
        add(new Label("dp", Model.of(dp)));

        //Ajout de la Form
        add(new RecapFicheSecuriteForm("fsForm", new CompoundPropertyModel(fs)));

    }

    public class RecapFicheSecuriteForm extends Form {

        private static final long serialVersionUID = -1555366090072306934L;

        @SuppressWarnings("serial")
        public RecapFicheSecuriteForm(String name, CompoundPropertyModel<FicheSecurite> fsModel) {

            super(name);
            setModel(fsModel);

            //liste des palanques
            List<Palanque> palanques = fs.getPalanques();
            PalanqueDataProvider pDataProvider = new PalanqueDataProvider(palanques);

//-----------------------------DEBUT de la definition des Palanques------------------------
            add(new DataView<Palanque>("simple", pDataProvider) {
                @Override
                protected void populateItem(final Item<Palanque> item) {
                    Palanque palanque = item.getModelObject();
                    item.add(new Label("numero", String.valueOf(palanque.getNumero())));

                    item.add(new Label("guide", palanque.getNomGuide()));
                    item.add(new Label("plongeur1", palanque.getNomPlongeur1()));
                    item.add(new Label("plongeur2", palanque.getNomPlongeur2()));
                    item.add(new Label("plongeur3", palanque.getNomPlongeur3()));
                    item.add(new Label("plongeur4", palanque.getNomPlongeur4()));

                    item.add(new Label("profondeurMaxPrevue", String.valueOf(palanque.getProfondeurMaxPrevue())));
                    item.add(new Label("profondeurMaxRea", String.valueOf(palanque.getProfondeurMaxRea())));

                    item.add(new Label("dureeTotalePrevue", String.valueOf(palanque.getDureeTotalePrevue())));
                    item.add(new Label("dureeTotaleRea", String.valueOf(palanque.getDureeTotaleRea())));

                    item.add(new Label("heureSortie", String.valueOf(palanque.getHeureSortie() + "H" + palanque.getMinuteSortie())));

                    item.add(new Label("palier3", String.valueOf(palanque.getPalier3m())));
                    item.add(new Label("palier6", String.valueOf(palanque.getPalier6m())));
                    item.add(new Label("palier9", String.valueOf(palanque.getPalier9m())));
                    item.add(new Label("palierProfond", String.valueOf(palanque.getPalierProfond())));

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

            add(new Link("cancel") {
                @Override
                public void onClick() {
                    setResponsePage(new GererListePlongeeAddFS());
                }
            });

            add(new AjaxButton("validerFS") {
                @Override
                protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                    try {
                        ResaSession.get().getPlongeeService().creerFicheSecurite(fs);
//                        setResponsePage(new GererListePlongeeAddFS());
                        setResponsePage(new ImpressionFicheSecurite());
                    } catch (ResaException ex) {
                        logger.debug("ResaException : " + ex.getKey());
                    } catch (TechnicalException ex) {
                        logger.debug("TechnicalException : " + ex.getKey());
                        try {
                            //Suite Pb on supprime tous ce qui vient d'être créé
                            ResaSession.get().getPlongeeService().supprimerFicheSecurite(fs);
                        } catch (ResaException ex1) {
                            logger.debug("ResaException : " + ex1.getKey());
                        } catch (TechnicalException ex1) {
                            logger.debug("TechnicalException : " + ex1.getKey());
                        }
                        target.addComponent(feedback);
                        error(ex.getKey());
                    }
                }

                @Override
                protected void onError(AjaxRequestTarget target, Form<?> form) {
                    target.addComponent(feedback);
                }
            });

        }
    }
}
