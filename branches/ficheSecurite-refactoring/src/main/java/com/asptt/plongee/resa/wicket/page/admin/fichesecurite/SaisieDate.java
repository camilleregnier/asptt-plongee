package com.asptt.plongee.resa.wicket.page.admin.fichesecurite;

import com.asptt.plongee.resa.model.Adherent;
import com.asptt.plongee.resa.util.CatalogueMessages;
import com.asptt.plongee.resa.wicket.ResaSession;
import com.asptt.plongee.resa.wicket.page.AccueilPage;
import com.asptt.plongee.resa.wicket.page.TemplatePage;
import java.util.Date;
import org.apache.log4j.Logger;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.datetime.StyleDateConverter;
import org.apache.wicket.datetime.markup.html.form.DateTextField;
import org.apache.wicket.extensions.yui.calendar.DatePicker;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.StringResourceModel;

public class SaisieDate extends TemplatePage {

    private final Logger logger = Logger.getLogger(getClass());
    private Date dateMini;
    private Date dateMaxi;
    FeedbackPanel feedback = new FeedbackPanel("feedback");

    public SaisieDate() {
        setPageTitle("Fiches de Sécurité Dates");
        add(new Label("message", new StringResourceModel(CatalogueMessages.GERER_ADD_FS_MSG, this, new Model<Adherent>(ResaSession.get().getAdherent()))));
        feedback.setOutputMarkupId(true);
        add(feedback);
        add(new SaisieDateForm("saisieDates"));
    }

    public class SaisieDateForm extends Form {

        private static final long serialVersionUID = -1555366090072306934L;

        @SuppressWarnings("serial")
        public SaisieDateForm(String name) {

            super(name);

            CompoundPropertyModel<DateBean> model = new CompoundPropertyModel<DateBean>(new DateBean());
            setModel(model);
            DateTextField dateMin = new DateTextField("dateMin", new PropertyModel(model, "dateMini"), new StyleDateConverter("S-", true));
            dateMin.setRequired(true);
            dateMin.add(new DatePicker());
            add(dateMin);
            logger.info("DateTextField dateMin : " + dateMin.getTextFormat());

            DateTextField dateMax = new DateTextField("dateMax", new PropertyModel<Date>(model, "dateMaxi"), new StyleDateConverter("S-", true));
            dateMax.setRequired(true);
            dateMax.add(new DatePicker());
            add(dateMax);
            logger.info("DateTextField dateMax : " + dateMax.getTextFormat());

            add(new AjaxButton("validDate") {
                @Override
                protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                    DateBean dateBean = (DateBean) form.getModelObject();
                    dateMini = dateBean.getDateMini();
                    dateMaxi = dateBean.getDateMaxi();
                    // on met les dates dans la session
                    ResaSession.get().setDateDebutFS(dateMini);
                    ResaSession.get().setDateFinFS(dateMaxi);
                    setResponsePage(new GererListePlongeeAddFS());
                }

                @Override
                protected void onError(AjaxRequestTarget target, Form<?> form) {
                    target.addComponent(feedback);
                }
            });

            add(new Link("cancel") {
                @Override
                public void onClick() {
                    setResponsePage(AccueilPage.class);
                }
            });

        }
    }

    public class DateBean {
        private Date dateMini;
        private Date dateMaxi;

        public Date getDateMini() {
            return dateMini;
        }

        public Date getDateMaxi() {
            return dateMaxi;
        }
    }

}
