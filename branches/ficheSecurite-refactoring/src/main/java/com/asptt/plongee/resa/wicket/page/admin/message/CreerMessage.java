package com.asptt.plongee.resa.wicket.page.admin.message;

import com.asptt.plongee.resa.exception.ResaException;
import com.asptt.plongee.resa.exception.TechnicalException;
import com.asptt.plongee.resa.model.Message;
import com.asptt.plongee.resa.util.CatalogueMessages;
import com.asptt.plongee.resa.wicket.ResaSession;
import com.asptt.plongee.resa.wicket.page.AccueilPage;
import com.asptt.plongee.resa.wicket.page.TemplatePage;
import java.util.Date;
import java.util.GregorianCalendar;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.datetime.StyleDateConverter;
import org.apache.wicket.datetime.markup.html.form.DateTextField;
import org.apache.wicket.extensions.yui.calendar.DatePicker;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.StringResourceModel;

public class CreerMessage extends TemplatePage {

    FeedbackPanel feedback = new FeedbackPanel("feedback");

    public CreerMessage() {
        setPageTitle("Creer Message");
        feedback.setOutputMarkupId(true);
        add(feedback);
        add(new MessageForm("inputForm"));
    }

    class MessageForm extends Form<Message> {

        private static final long serialVersionUID = 5374674730458593314L;

        public MessageForm(String id) {
            super(id);
            CompoundPropertyModel<Message> model = new CompoundPropertyModel<Message>(new Message());
            setModel(model);

            TextArea<String> textareaInput = new TextArea<String>("libelle");
            textareaInput.setRequired(true);
            add(textareaInput);

            DateTextField dateDebTextFiled = new DateTextField("dateDebut", new PropertyModel<Date>(model, "dateDebut"), new StyleDateConverter("S-", true));
            dateDebTextFiled.setRequired(true);
            add(dateDebTextFiled);
            dateDebTextFiled.add(new DatePicker());

            DateTextField dateFinTextFiled = new DateTextField("dateFin", new PropertyModel<Date>(model, "dateFin"), new StyleDateConverter("S-", true));
            dateFinTextFiled.setRequired(false);
            add(dateFinTextFiled);
            dateFinTextFiled.add(new DatePicker());

            add(new RequiredTextField<Integer>("rang", Integer.class));

            add(new AjaxButton("validerMessage") {
                @Override
                // La validation doit se faire en Ajax car le formulaire de la
                // fenêtre principal n'y a pas accés
                // http://yeswicket.com/index.php?post/2010/04/26/G%C3%A9rer-facilement-les-fen%C3%AAtres-modales-avec-Wicket
                protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                    Message message = (Message) form.getModelObject();

                    // Mise à jour du message
                    try {
                        //on force la date de fin à 23h59
                        if (null != message.getDateFin()) {
                            Date dateFin = message.getDateFin();
                            GregorianCalendar gc = new GregorianCalendar();
                            gc.setTime(dateFin);
                            gc.add(GregorianCalendar.HOUR_OF_DAY, 23);
                            gc.add(GregorianCalendar.MINUTE, 59);
                            message.setDateFin(gc.getTime());
                            if (message.getDateFin().before(message.getDateDebut())) {
                                throw new ResaException(CatalogueMessages.DATE_INCOMPATIBLE);
                            }
                        }
                        ResaSession.get().getAdherentService().createMessage(message);
                        setResponsePage(AccueilPage.class);

                    } catch (TechnicalException e) {
                        e.printStackTrace();
                        error(e.getKey());
                    } catch (ResaException e) {
                        e.printStackTrace();
                        error(initMessageException(e.getKey()));
                    }

                }

                // L'implémentation de cette méthode est nécessaire pour voir
                // les messages d'erreur dans le feedBackPanel
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

    private String initMessageException(String entreeCatalogue) {
        String libError = "";
        if (entreeCatalogue.equalsIgnoreCase(CatalogueMessages.DATE_INCOMPATIBLE)) {
            StringResourceModel srm = new StringResourceModel(CatalogueMessages.DATE_INCOMPATIBLE, this, null);
            libError = srm.getString();
        }
        return libError;
    }
}
