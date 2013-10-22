package com.asptt.plongee.resa.wicket.page.fichesecurite;

import com.asptt.plongee.resa.model.Adherent;
import com.asptt.plongee.resa.model.FicheSecurite;
import com.asptt.plongee.resa.model.Plongee;
import com.asptt.plongee.resa.model.PlongeeDataProvider;
import com.asptt.plongee.resa.util.CatalogueMessages;
import com.asptt.plongee.resa.wicket.ResaSession;
import com.asptt.plongee.resa.wicket.page.TemplatePage;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import org.apache.log4j.Logger;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxLink;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.StringResourceModel;

public class GererListePlongeeAddFS extends TemplatePage {

    private final Logger logger = Logger.getLogger(getClass());
    private Date dateMini;
    private Date dateMaxi;
    FeedbackPanel feedback = new FeedbackPanel("feedback");

    public GererListePlongeeAddFS() {
        dateMini = ResaSession.get().getDateDebutFS();
        dateMaxi = ResaSession.get().getDateFinFS();
        setPageTitle("Fiches de Sécurité");
        add(new Label("message", new StringResourceModel(CatalogueMessages.GERER_ADD_FS_MSG, this, new Model<Adherent>(ResaSession.get().getAdherent()))));
        feedback.setOutputMarkupId(true);
        add(feedback);
        add(new GererListePlongeeAddFSForm("listePlongees"));
    }

    public class GererListePlongeeAddFSForm extends Form {

        private static final long serialVersionUID = -1555366090072306934L;
        private List<Plongee> data;

        @SuppressWarnings("serial")
        public GererListePlongeeAddFSForm(String name) {

            super(name);

            data = ResaSession.get().getPlongeeService().rechercherPlongeesSansFS(dateMini, dateMaxi);

            PlongeeDataProvider pDataProvider = new PlongeeDataProvider(data);

            add(new DataView<Plongee>("simple", pDataProvider) {
                @Override
                protected void populateItem(final Item<Plongee> item) {
                    Plongee plongee = item.getModelObject();
                    String nomDP = "Aucun";
                    if (null != plongee.getDp()) {
                        nomDP = plongee.getDp().getNom();
                    }

                    item.add(new IndicatingAjaxLink("select") {
                        @Override
                        public void onClick(AjaxRequestTarget target) {
                            FicheSecurite fs = new FicheSecurite(item.getModelObject());
                            fs.setNomDP(item.getModelObject().getDp().getNom());
                            fs.setNomPilote(item.getModelObject().getPilote().getNom());
                            //On met la fiche de sécurité dans la session
                            ResaSession.get().setFicheSecurite(fs);
                            setResponsePage(new SaisieFicheSecurite());
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
        }
    }
}
