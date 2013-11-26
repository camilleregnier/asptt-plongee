/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asptt.plongee.resa.wicket.page.admin.fichesecurite;

import com.asptt.plongee.resa.model.FicheSecurite;
import com.asptt.plongee.resa.model.Palanque;
import com.asptt.plongee.resa.model.Plongee;
import com.asptt.plongee.resa.util.CatalogueMessages;
import com.asptt.plongee.resa.util.ResaUtil;
import com.asptt.plongee.resa.wicket.ResaSession;
import com.asptt.plongee.resa.wicket.page.TemplatePage;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.validation.validator.MaximumValidator;
import org.apache.wicket.validation.validator.MinimumValidator;

/**
 *
 * @author ecus6396
 */
public class SaisiePalanquePage extends TemplatePage {

    private final Logger logger = Logger.getLogger(getClass());
    FeedbackPanel feedback = new FeedbackPanel("feedback");
    private FicheSecurite fs;
    private Plongee plongee;
    private Palanque palanque;
    private List<ChoiceRenderPlongeur> plongeursAInscrire;
    private List<ChoiceRenderPlongeur> plongeursPalanque;

    public SaisiePalanquePage() {
        setOutputMarkupId(true);
        setPageTitle("Saisie de Palanquee");
        feedback.setOutputMarkupId(true);
        add(feedback);
        //Récupération de la fiche de securite dans la session
        fs = ResaSession.get().getFicheSecurite();
        //init de la plongée
        this.plongee = fs.getPlongee();
        //init de la palanque
        palanque = new Palanque(fs.getPlongeursAInscrireMap(), fs.getPlongee().getDate());
        palanque.setNumero(fs.getNombrePalanque() + 1);

        plongeursAInscrire = palanque.getListPlongeursAInscrire();

        add(new Label("message",new StringResourceModel("message",this,null)));

        add(new Label("messagePlongee", new StringResourceModel(
                CatalogueMessages.SAISIE_PALANQUE_MSG_PLONGEE,
                this, new Model<FicheSecurite>(fs),
                new Object[]{ResaUtil.getDateString(plongee.getDateVisible()), plongee.getType()})));

        add(new Label("messageSite", new StringResourceModel(
                CatalogueMessages.SAISIE_PALANQUE_MSG_SITE,
                this, new Model<FicheSecurite>(fs))));

        add(new Label("messageMeteo", new StringResourceModel(
                CatalogueMessages.SAISIE_PALANQUE_MSG_METEO,
                this, new Model<FicheSecurite>(fs))));

        add(new Label("messageNumPalanque", new StringResourceModel(
                CatalogueMessages.SAISIE_PALANQUE_MSG_NUM_PALANQUE,
                this, new Model<Palanque>(palanque))));

        Form form = new SaisiePalanqueForm("palanqueForm");
        form.setOutputMarkupId(true);
        add(form);
    }

    public class PlongeurChoiceRenderer<T> extends ChoiceRenderer<ChoiceRenderPlongeur> {

        public PlongeurChoiceRenderer(String displayExpression, String idExpression) {
            super(displayExpression, idExpression);
        }

        @Override
        public Object getDisplayValue(ChoiceRenderPlongeur object) {
            return object.getValue();
        }

        @Override
        public String getIdValue(ChoiceRenderPlongeur object, int index) {
            return object.getKey();
        }
    }

    public class SaisiePalanqueForm extends Form {

        private static final long serialVersionUID = -1555366090072306974L;
        private final DropDownChoice<ChoiceRenderPlongeur> ddc;
        private Label labelGuide;
        private Label labelPlongeur1;
        private Label labelPlongeur2;
        private Label labelPlongeur3;
        private Label labelPlongeur4;
        private Integer hourSelected;

//        private MarkupContainer plongeur4Container;
        public boolean isConditionSatisfed() {
            if (palanque.getGuide() == null || palanque.getNomPlongeur1() == null
                    || palanque.getNomPlongeur2() == null || palanque.getNomPlongeur3() == null) {
                return false;
            } else {
                return true;
            }
        }

        @SuppressWarnings("serial")
        public SaisiePalanqueForm(String id) {
            super(id);

            final CompoundPropertyModel<Palanque> modelPalanque =
                    new CompoundPropertyModel<Palanque>(palanque);
            setModel(modelPalanque);

            //----------------------Le Choix des plongeurs ------------------------
            ddc = new DropDownChoice<ChoiceRenderPlongeur>("listPlongeurs",
                    new PropertyModel(modelPalanque, "selectedPlongeur"),
                    plongeursAInscrire,
                    new PlongeurChoiceRenderer("value", "key")) {
                @Override
                protected boolean wantOnSelectionChangedNotifications() {
                    return true;
                }

                @Override
                protected void onSelectionChanged(ChoiceRenderPlongeur newSelection) {
                    palanque.setSelectedPlongeur(newSelection);
//                    logger.info("DDCP la selection" + newSelection.getValue());
//                    info("LASELECTION : " + newSelection.getValue());
//                    error("DDC");
                }
            };
            ddc.setOutputMarkupId(true);
            add(ddc);

            //--------------GUIGE----------------------- 
            AjaxButton b_validGuide = new AjaxButton("valideGuide") {
                @Override
                protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                    if (palanque.getGuide() != null) {
                        //on a déja un guide de saisi, donc erreur et on fait rien
                        error("Le guide à déjà été saisi : " + palanque.getNomGuide() + " Impossible d'en saisir un autre");
                    } else if (null == palanque.getSelectedPlongeur()) {
                        error("Vous devez d'abord sélectionner un plongeur");
                    } else {
                        palanque.setGuide(palanque.getSelectedPlongeur());
                        suppEntreeListPlongeursAInscrire(palanque.getGuide());
                        palanque.setSelectedPlongeur(null);
                        target.addComponent(labelGuide);
//                        target.addComponent(plongeur4Container);
                        target.addComponent(ddc);
                    }
                }

                @Override
                protected void onError(AjaxRequestTarget target, Form<?> form) {
                    target.addComponent(feedback);
                }
            };
            b_validGuide.setOutputMarkupId(true);
            add(b_validGuide);

            AjaxButton b_annulerGuide = new AjaxButton("annulerGuide") {
                @Override
                protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                    if (palanque.getGuide() == null) {
                        error("Le guide n'est pas saisi ");
                    } else {
//                        logger.info("ON SUBMITT ANNULERGUIDE, le nom de plongeur :" + palanque.getNomGuide());
//                        info("ON SUBMITT ANNULERGUIDE, le nom de plongeur :" + palanque.getNomGuide());
//                        error("annulerGuide");
                        addEntreeListPlongeursInscrits(palanque.getGuide());
                        palanque.setGuide(null);
                        palanque.setSelectedPlongeur(null);
                        target.addComponent(labelGuide);
//                        target.addComponent(plongeur4Container);
                        target.addComponent(ddc);
                    }
                }

                @Override
                protected void onError(AjaxRequestTarget target, Form<?> form) {
                    target.addComponent(feedback);
                }
            };
            b_annulerGuide.setOutputMarkupId(true);
            add(b_annulerGuide);

            labelGuide = new Label("nomGuide", new PropertyModel(modelPalanque, "nomGuide"));
            labelGuide.setOutputMarkupId(true);
            add(labelGuide);

            //--------------PLONGEUR1----------------------- 
            AjaxButton b_validPlongeur1 = new AjaxButton("validePlongeur1") {
                @Override
                protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                    if (palanque.getPlongeur1() != null) {
                        //on a déja un Plongeur1 de saisi, donc erreur et on fait rien
                        error("Le Plongeur1 à déjà été saisi : " + palanque.getPlongeur1().getValue() + " Impossible d'en saisir un autre");
                    } else if (null == palanque.getSelectedPlongeur()) {
                        error("Vous devez d'abord sélectionner un plongeur");
                    } else {
                        palanque.setPlongeur1(palanque.getSelectedPlongeur());
                        suppEntreeListPlongeursAInscrire(palanque.getPlongeur1());
                        palanque.setSelectedPlongeur(null);
                        target.addComponent(labelPlongeur1);
//                        target.addComponent(plongeur4Container);
                        target.addComponent(ddc);
                    }
                }

                @Override
                protected void onError(AjaxRequestTarget target, Form<?> form) {
                    target.addComponent(feedback);
                }
            };
            b_validPlongeur1.setOutputMarkupId(true);
            add(b_validPlongeur1);

            AjaxButton b_annulerPlongeur1 = new AjaxButton("annulerPlongeur1") {
                @Override
                protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                    if (palanque.getPlongeur1() == null) {
                        error("Le Plongeur1 n'est pas saisi ");
                    } else {
//                        logger.info("ON SUBMITT ANNULERPlongeur1, le nom de plongeur :" + palanque.getPlongeur1().getValue());
//                        info("ON SUBMITT ANNULERPlongeur1, le nom de plongeur :" + palanque.getPlongeur1().getValue());
//                        error("annulerPlongeur1");
                        addEntreeListPlongeursInscrits(palanque.getPlongeur1());
                        palanque.setPlongeur1(null);
                        palanque.setSelectedPlongeur(null);
                        target.addComponent(labelPlongeur1);
//                        target.addComponent(plongeur4Container);
                        target.addComponent(ddc);
                    }
                }

                @Override
                protected void onError(AjaxRequestTarget target, Form<?> form) {
                    target.addComponent(feedback);
                }
            };
            b_annulerPlongeur1.setOutputMarkupId(true);
            add(b_annulerPlongeur1);

            labelPlongeur1 = new Label("nomPlongeur1", new PropertyModel(modelPalanque, "nomPlongeur1"));
            labelPlongeur1.setOutputMarkupId(true);
            add(labelPlongeur1);

            //--------------PLONGEUR2----------------------- 
            AjaxButton b_validPlongeur2 = new AjaxButton("validePlongeur2") {
                @Override
                protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                    if (palanque.getPlongeur2() != null) {
                        //on a déja un Plongeur2 de saisi, donc erreur et on fait rien
                        error("Le Plongeur2 à déjà été saisi : " + palanque.getPlongeur2().getValue() + " Impossible d'en saisir un autre");
                    } else if (null == palanque.getSelectedPlongeur()) {
                        error("Vous devez d'abord sélectionner un plongeur");
                    } else {
                        palanque.setPlongeur2(palanque.getSelectedPlongeur());
                        suppEntreeListPlongeursAInscrire(palanque.getPlongeur2());
                        palanque.setSelectedPlongeur(null);
                        target.addComponent(labelPlongeur2);
//                        target.addComponent(plongeur4Container);
                        target.addComponent(ddc);
                    }
                }

                @Override
                protected void onError(AjaxRequestTarget target, Form<?> form) {
                    target.addComponent(feedback);
                }
            };
            b_validPlongeur2.setOutputMarkupId(true);
            add(b_validPlongeur2);

            AjaxButton b_annulerPlongeur2 = new AjaxButton("annulerPlongeur2") {
                @Override
                protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                    if (palanque.getPlongeur2() == null) {
                        error("Le Plongeur2 n'est pas saisi ");
                    } else {
//                        logger.info("ON SUBMITT ANNULERPlongeur2, le nom de plongeur :" + palanque.getPlongeur2().getValue());
//                        info("ON SUBMITT ANNULERPlongeur2, le nom de plongeur :" + palanque.getPlongeur2().getValue());
//                        error("annulerPlongeur2");
                        addEntreeListPlongeursInscrits(palanque.getPlongeur2());
                        palanque.setPlongeur2(null);
                        palanque.setSelectedPlongeur(null);
                        target.addComponent(labelPlongeur2);
//                        target.addComponent(plongeur4Container);
                        target.addComponent(ddc);
                    }
                }

                @Override
                protected void onError(AjaxRequestTarget target, Form<?> form) {
                    target.addComponent(feedback);
                }
            };
            b_annulerPlongeur2.setOutputMarkupId(true);
            add(b_annulerPlongeur2);

            labelPlongeur2 = new Label("nomPlongeur2", new PropertyModel(modelPalanque, "nomPlongeur2"));
            labelPlongeur2.setOutputMarkupId(true);
            add(labelPlongeur2);

            //--------------PLONGEUR3----------------------- 
            AjaxButton b_validPlongeur3 = new AjaxButton("validePlongeur3") {
                @Override
                protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                    if (palanque.getPlongeur3() != null) {
                        //on a déja un Plongeur3 de saisi, donc erreur et on fait rien
                        error("Le Plongeur3 à déjà été saisi : " + palanque.getPlongeur3().getValue() + " Impossible d'en saisir un autre");
                    } else if (null == palanque.getSelectedPlongeur()) {
                        error("Vous devez d'abord sélectionner un plongeur");
                    } else {
                        palanque.setPlongeur3(palanque.getSelectedPlongeur());
                        suppEntreeListPlongeursAInscrire(palanque.getPlongeur3());
                        palanque.setSelectedPlongeur(null);
                        target.addComponent(labelPlongeur3);
//                        target.addComponent(plongeur4Container);
                        target.addComponent(ddc);
                    }
                }

                @Override
                protected void onError(AjaxRequestTarget target, Form<?> form) {
                    target.addComponent(feedback);
                }
            };
            b_validPlongeur3.setOutputMarkupId(true);
            add(b_validPlongeur3);

            AjaxButton b_annulerPlongeur3 = new AjaxButton("annulerPlongeur3") {
                @Override
                protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                    if (palanque.getPlongeur3() == null) {
                        error("Le Plongeur3 n'est pas saisi ");
                    } else {
//                        logger.info("ON SUBMITT ANNULERPlongeur3, le nom de plongeur :" + palanque.getPlongeur3().getValue());
//                        info("ON SUBMITT ANNULERPlongeur3, le nom de plongeur :" + palanque.getPlongeur3().getValue());
//                        error("annulerPlongeur3");
                        addEntreeListPlongeursInscrits(palanque.getPlongeur3());
                        palanque.setPlongeur3(null);
                        palanque.setSelectedPlongeur(null);
                        target.addComponent(labelPlongeur3);
//                        target.addComponent(plongeur4Container);
                        target.addComponent(ddc);
                    }
                }

                @Override
                protected void onError(AjaxRequestTarget target, Form<?> form) {
                    target.addComponent(feedback);
                }
            };
            b_annulerPlongeur3.setOutputMarkupId(true);
            add(b_annulerPlongeur3);

            labelPlongeur3 = new Label("nomPlongeur3", new PropertyModel(modelPalanque, "nomPlongeur3"));
            labelPlongeur3.setOutputMarkupId(true);
            add(labelPlongeur3);

            //--------------PLONGEUR4----------------------- 
            AjaxButton b_validPlongeur4 = new AjaxButton("validePlongeur4") {
                @Override
                protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                    if (palanque.getPlongeur4() != null) {
                        //on a déja un Plongeur4 de saisi, donc erreur et on fait rien
                        error("Le Plongeur4 à déjà été saisi : " + palanque.getPlongeur4().getValue() + " Impossible d'en saisir un autre");
                    } else if (null == palanque.getSelectedPlongeur()) {
                        error("Vous devez d'abord sélectionner un plongeur");
                    } else {
                        palanque.setPlongeur4(palanque.getSelectedPlongeur());
                        suppEntreeListPlongeursAInscrire(palanque.getPlongeur4());
                        palanque.setSelectedPlongeur(null);
                        target.addComponent(labelPlongeur4);
//                        target.addComponent(plongeur4Container);
                        target.addComponent(ddc);
                    }
                }

                @Override
                protected void onError(AjaxRequestTarget target, Form<?> form) {
                    target.addComponent(feedback);
                }
            };
            b_validPlongeur4.setOutputMarkupId(true);
            add(b_validPlongeur4);

            AjaxButton b_annulerPlongeur4 = new AjaxButton("annulerPlongeur4") {
                @Override
                protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                    if (palanque.getPlongeur4() == null) {
                        error("Le Plongeur4 n'est pas saisi ");
                    } else {
//                        logger.info("ON SUBMITT ANNULERPlongeur4, le nom de plongeur :" + palanque.getPlongeur4().getValue());
//                        info("ON SUBMITT ANNULERPlongeur4, le nom de plongeur :" + palanque.getPlongeur4().getValue());
//                        error("annulerPlongeur4");
                        addEntreeListPlongeursInscrits(palanque.getPlongeur4());
                        palanque.setPlongeur4(null);
                        palanque.setSelectedPlongeur(null);
                        target.addComponent(labelPlongeur4);
//                        target.addComponent(plongeur4Container);
                        target.addComponent(ddc);
                    }
                }

                @Override
                protected void onError(AjaxRequestTarget target, Form<?> form) {
                    target.addComponent(feedback);
                }
            };
            b_annulerPlongeur4.setOutputMarkupId(true);
            add(b_annulerPlongeur4);

            labelPlongeur4 = new Label("nomPlongeur4", new PropertyModel(modelPalanque, "nomPlongeur4"));
            labelPlongeur4.setOutputMarkupId(true);
            add(labelPlongeur4);

//            plongeur4Container = new MarkupContainer("toto") {
//                @Override
//                protected void onConfigure() {
//                    super.onConfigure();
//                    setVisibilityAllowed(isConditionSatisfed());
//                }
//            };
//            plongeur4Container.setOutputMarkupId(true);
//            plongeur4Container.add(b_validPlongeur4);
//            plongeur4Container.add(b_annulerPlongeur4);
//            plongeur4Container.add(labelPlongeur4);
//            add(plongeur4Container);

//            List<Integer> cpt60 = new ArrayList<Integer>();
//            for (int i = 0; i <= 60; i++) {
//                cpt60.add(i);
//            }
            //-----------------Profondeur Maxi--------------------------
//            add(new DropDownChoice<Integer>("profondeurMaxPrevue", new PropertyModel<Integer>(this, "hourSelected"), cpt60));
//            add(new DropDownChoice<Integer>("profondeurMaxRea", new PropertyModel<Integer>(this, "hourSelected"), cpt60));
            TextField pMaxPrevu = new TextField<Integer>("profondeurMaxPrevue", Integer.class);
            pMaxPrevu.add(new MaximumValidator<Integer>(60));
            pMaxPrevu.add(new MinimumValidator<Integer>(0));
            add(pMaxPrevu);
            TextField pMaxRea = new TextField<Integer>("profondeurMaxRea", Integer.class);
            pMaxRea.add(new MaximumValidator<Integer>(60));
            pMaxRea.add(new MinimumValidator<Integer>(0));
            add(pMaxRea);

            //-----------------Durée de la plongée--------------------------
//            add(new RequiredTextField<Integer>("dureeTotalePrevue", Integer.class).add(new MaximumValidator<Integer>(60)));
//            RequiredTextField<Integer> dureeTotalRea = new RequiredTextField<Integer>("dureeTotaleRea", Integer.class);
//            dureeTotalRea.add(new MaximumValidator<Integer>(60));
//            add(dureeTotalRea);
            //-----------------Durée de la plongée--------------------------
//            add(new DropDownChoice<Integer>("dureeTotalePrevue", new PropertyModel<Integer>(this, "hourSelected"), cpt60));
//            add(new DropDownChoice<Integer>("dureeTotaleRea", new PropertyModel<Integer>(this, "hourSelected"), cpt60));
            TextField dureePrevu = new TextField<Integer>("dureeTotalePrevue", Integer.class);
            dureePrevu.add(new MaximumValidator<Integer>(60));
            dureePrevu.add(new MinimumValidator<Integer>(0));
            add(dureePrevu);
            TextField dureeRea = new TextField<Integer>("dureeTotaleRea", Integer.class);
            dureeRea.add(new MaximumValidator<Integer>(60));
            dureeRea.add(new MinimumValidator<Integer>(0));
            add(dureeRea);
            //-----------------Paliers--------------------------
//            add(new DropDownChoice<Integer>("palier3m", new PropertyModel<Integer>(this, "hourSelected"), cpt60));
//            add(new DropDownChoice<Integer>("palier6m", new PropertyModel<Integer>(this, "hourSelected"), cpt60));
//            add(new DropDownChoice<Integer>("palier9m", new PropertyModel<Integer>(this, "hourSelected"), cpt60));
//            add(new DropDownChoice<Integer>("palierProfond", new PropertyModel<Integer>(this, "hourSelected"), cpt60));
            TextField palier3 = new TextField<Integer>("palier3m", Integer.class);
            palier3.add(new MinimumValidator<Integer>(0));
            add(palier3);
            TextField palier6 = new TextField<Integer>("palier6m", Integer.class);
            palier6.add(new MinimumValidator<Integer>(0));
            add(palier6);
            TextField palier9 = new TextField<Integer>("palier9m", Integer.class);
            palier9.add(new MinimumValidator<Integer>(0));
            add(palier9);
            TextField palierP = new TextField<Integer>("palierProfond", Integer.class);
            palierP.add(new MinimumValidator<Integer>(0));
            add(palierP);
            //-----------------Heure de sortie
//            add(new TimeField("timeField", new PropertyModel<Date>(modelPalanque, "heureSortie")));
//            add(new DropDownChoice<Integer>("heureSortie", new PropertyModel<Integer>(this, "hourSelected"), cpt60));
//            add(new DropDownChoice<Integer>("minuteSortie", new PropertyModel<Integer>(this, "hourSelected"), cpt60));
            TextField heureSortie = new TextField<Integer>("heureSortie", Integer.class);
            heureSortie.add(new MaximumValidator<Integer>(60));
            heureSortie.add(new MinimumValidator<Integer>(0));
            add(heureSortie);
            TextField minuteSortie = new TextField<Integer>("minuteSortie", Integer.class);
            minuteSortie.add(new MaximumValidator<Integer>(60));
            minuteSortie.add(new MinimumValidator<Integer>(0));
            add(minuteSortie);
            //------------------- Validation de la palanquee
            AjaxButton b_validerPalanque = new AjaxButton("validerPalanque") {
                @Override
                protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                    Palanque palanque = (Palanque) form.getModelObject();
                    if (getPlongeursPalanque().size() < 2) {
                        error("Il faut au moins 2 plongeurs dans une Palanquee");
                    } else {
                        fs.suppPlongeursAInscrireMap(getPlongeursPalanque());
                        palanque.setListPlongeursPalanque(getPlongeursPalanque());
                        fs.getPalanques().add(palanque);
                        if (fs.getPlongeursAInscrireMap().size() == 1) {
                            error("Il reste un plongeur à inscrire");
                            fs.getPalanques().remove(palanque);
                        } else {
                            if (fs.getPlongeursAInscrireMap().isEmpty()) {
                                //TODO faire page de recap et valider la palanquee
                                setResponsePage(new RecapFicheSecurite());
                            } else {

                                setResponsePage(new SaisiePalanquePage());
                            }
                        }
                    }
                }

                @Override
                protected void onError(AjaxRequestTarget target, Form<?> form) {
                    target.addComponent(feedback);
                }
            };
            b_validerPalanque.setOutputMarkupId(true);
            add(b_validerPalanque);

        }

        public int getHourSelected() {
            if (null == hourSelected) {
                hourSelected = 0;
            }
            return hourSelected;
        }

        public void setHourSelected(int hourSelected) {
            this.hourSelected = hourSelected;
        }
    }

    public List<ChoiceRenderPlongeur> getPlongeursPalanque() {
        if (null == plongeursPalanque) {
            plongeursPalanque = new ArrayList<ChoiceRenderPlongeur>();
        }
        return plongeursPalanque;
    }

    public void setPlongeursPalanque(List<ChoiceRenderPlongeur> plongeursPalanque) {
        this.plongeursPalanque = plongeursPalanque;
    }

    public void suppEntreeListPlongeursAInscrire(ChoiceRenderPlongeur value) {
        if (plongeursAInscrire.contains(value)) {
            plongeursAInscrire.remove(value);
        }
        // on met le plongeur dans les plongeurs de la palanque
        if (!getPlongeursPalanque().contains(value)) {
            getPlongeursPalanque().add(value);
        }
    }

    public void addEntreeListPlongeursInscrits(ChoiceRenderPlongeur value) {
        if (!plongeursAInscrire.contains(value)) {
            plongeursAInscrire.add(value);
        }
        // on enleve le plongeur des plongeurs de la palanque
        if (getPlongeursPalanque().contains(value)) {
            getPlongeursPalanque().remove(value);
        }
    }
}
