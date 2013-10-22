package com.asptt.plongee.resa.wicket.page.admin.plongee;

import com.asptt.plongee.resa.exception.TechnicalException;
import com.asptt.plongee.resa.model.NavigationOriginePage;
import com.asptt.plongee.resa.model.Plongee;
import com.asptt.plongee.resa.model.PlongeeDataProvider;
import com.asptt.plongee.resa.util.CatalogueMessages;
import com.asptt.plongee.resa.wicket.ResaSession;
import com.asptt.plongee.resa.wicket.page.ErreurTechniquePage;
import com.asptt.plongee.resa.wicket.page.TemplatePage;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxLink;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.StringResourceModel;

public class AnnulerPlongee extends TemplatePage {

    private static ModalWindow modalPlongee;
    Label infoLabel;
    private static DataView<Plongee> dataPlongees;

    public static DataView<Plongee> getDataPlongees() {
        return dataPlongees;
    }

    public AnnulerPlongee() {

        setPageTitle("Annuler une plongee");

        // Fenêtre modale d'informations sur la plongée à annuler
        modalPlongee = new ModalWindow("modalPlongee");
        modalPlongee.setTitle("Informations sur la plong\u00e9e");
        modalPlongee.setUseInitialHeight(false);
        modalPlongee.setWidthUnit("px");
        add(modalPlongee);

        infoLabel = new Label("infoLabel", new StringResourceModel(CatalogueMessages.ANNULER_MSG, this, null));
        add(infoLabel);

        // TODO, voir si on ajoute pas cela dans un panel
        // pour une mise à jour dynamique lors de l'annulation de la plongée
        try {
            List<Plongee> plongees = ResaSession.get().getPlongeeService().rechercherPlongeeProchainJour(ResaSession.get().getAdherent());

            PlongeeDataProvider pDataProvider = new PlongeeDataProvider(plongees);

            dataPlongees = new DataView<Plongee>("simple", pDataProvider) {
                @Override
                protected void populateItem(final Item<Plongee> item) {
                    Plongee plongee = item.getModelObject();
                    String nomDP = "Aucun";
                    if (null != plongee.getDp()) {
                        nomDP = plongee.getDp().getNom();
                    }

                    item.add(new IndicatingAjaxLink("annuler") {
                        @Override
                        public void onClick(AjaxRequestTarget target) {
                            replaceModalWindow(target, item.getModel());
                            modalPlongee.show(target);
                        }
                    });

                    // Mise en forme de la date
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(plongee.getDate());
                    String dateAffichee = cal.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.FRANCE) + " ";
                    dateAffichee = dateAffichee + cal.get(Calendar.DAY_OF_MONTH) + " ";
                    dateAffichee = dateAffichee + cal.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.FRANCE) + " ";
                    dateAffichee = dateAffichee + cal.get(Calendar.YEAR);

                    item.add(new Label("date", dateAffichee));
                    item.add(new Label("dp", nomDP));
                    item.add(new Label("type", plongee.getType()));
                    item.add(new Label("niveauMini", plongee.getNiveauMinimum().toString()));

                    // Places restantes
                    item.add(new Label("placesRestantes", ResaSession.get().getPlongeeService().getNbPlaceRestante(plongee).toString()));

                    item.add(new AttributeModifier("class", true,
                            new AbstractReadOnlyModel<String>() {
                                @Override
                                public String getObject() {
                                    return (item.getIndex() % 2 == 1) ? "even"
                                            : "odd";
                                }
                            }));
                }
            };
            dataPlongees.setOutputMarkupId(true);
            add(dataPlongees);
        } catch (TechnicalException e) {
            e.printStackTrace();
            ErreurTechniquePage etp = new ErreurTechniquePage(e);
            setResponsePage(etp);
        }
    }

    private void replaceModalWindow(AjaxRequestTarget target, IModel<Plongee> plongee) {
        modalPlongee.setContent(new AnnulerPlongeePanel(modalPlongee.getContentId(), plongee, NavigationOriginePage.ANNULATION.name()));
        // Pour éviter le message de disparition de la fenetre lors de la validation
        target.appendJavascript("Wicket.Window.unloadConfirmation  = false;");
    }
}
