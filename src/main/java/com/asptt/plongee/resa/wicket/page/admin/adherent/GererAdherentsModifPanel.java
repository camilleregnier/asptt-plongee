package com.asptt.plongee.resa.wicket.page.admin.adherent;

import com.asptt.plongee.resa.exception.ResaException;
import com.asptt.plongee.resa.exception.TechnicalException;
import com.asptt.plongee.resa.mail.PlongeeMail;
import com.asptt.plongee.resa.model.Adherent;
import com.asptt.plongee.resa.model.NiveauAutonomie;
import com.asptt.plongee.resa.wicket.ResaSession;
import com.asptt.plongee.resa.wicket.page.admin.adherent.contact.ContactPanel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.datetime.StyleDateConverter;
import org.apache.wicket.datetime.markup.html.form.DateTextField;
import org.apache.wicket.extensions.yui.calendar.DatePicker;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.ListMultipleChoice;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.validation.validator.EmailAddressValidator;
import org.apache.wicket.validation.validator.PatternValidator;
import org.apache.wicket.validation.validator.StringValidator.ExactLengthValidator;

public class GererAdherentsModifPanel extends Panel {

    private static final long serialVersionUID = 1L;
    private CompoundPropertyModel<Adherent> modelAdherent;
    private IModel mAdhSav;

    public GererAdherentsModifPanel(String id, IModel<Adherent> adherent) {
        super(id, adherent);
        mAdhSav = adherent;
        setOutputMarkupId(true);
        add(new AdherentForm("inputForm", adherent));
    }

    class AdherentForm extends Form {

        private static final long serialVersionUID = 5374674730458593314L;

        public AdherentForm(String id, IModel<Adherent> adherent) {
            super(id, adherent);
            modelAdherent = new CompoundPropertyModel<Adherent>(adherent);
            setModel(modelAdherent);

            final FeedbackPanel feedback = new FeedbackPanel("feedback");
            feedback.setOutputMarkupId(true);
            add(feedback);
            add(new RequiredTextField<String>("nom"));
            add(new RequiredTextField<String>("prenom"));
            add(new Label("numeroLicense", adherent.getObject().getNumeroLicense()));

            // numéro de téléphone au bon format (10 caractères numériques)
            RequiredTextField<String> telephone = new RequiredTextField<String>("telephone", String.class);
            telephone.add(ExactLengthValidator.exactLength(10));
            telephone.add(new PatternValidator("\\d{10}"));
            add(telephone);

            add(new RequiredTextField<String>("mail").add(EmailAddressValidator
                    .getInstance()));

            // Ajout de la liste des niveaux
            List<String> niveaux = new ArrayList<String>();
            for (NiveauAutonomie n : NiveauAutonomie.values()) {
                niveaux.add(n.toString());
            }
            add(new DropDownChoice("niveau", niveaux));

            // Ajout de la checkbox pilote
            add(new CheckBox("pilote"));

            // Ajout de la checkbox membre actif (ou pas)
            add(new CheckBox("actif"));

            // Ajout de la liste des niveaux d'encadrement
            List<String> encadrement = new ArrayList<String>();
            for (Adherent.Encadrement e : Adherent.Encadrement.values()) {
                encadrement.add(e.toString());
            }
            DropDownChoice encadrt = new DropDownChoice("encadrement", encadrement);
            add(encadrt);

            // Ajout des roles
            List<String> roles = Arrays.asList(new String[]{"ADMIN", "USER",
                        "SECRETARIAT"});
            add(new ListMultipleChoice<String>("roles", roles));

            //Ajout des nouveaux champs date du certificat medical et de l'année de cotisation
            DateTextField dateCMTextFiled = new DateTextField("dateCM", new PropertyModel<Date>(modelAdherent, "dateCM"), new StyleDateConverter("S-", true));
            dateCMTextFiled.setRequired(true);
            add(dateCMTextFiled);
            dateCMTextFiled.add(new DatePicker());

            Date dateDuJour = new Date();
            GregorianCalendar gc = new GregorianCalendar();
            gc.setTime(dateDuJour);
            int anneeCourante = gc.get(Calendar.YEAR);
            int nextAnnee = anneeCourante + 1;

            List<Integer> annees = new ArrayList<Integer>();
            annees.add(new Integer(anneeCourante));
            annees.add(new Integer(nextAnnee));

            DropDownChoice<Integer> listAnnee = new DropDownChoice<Integer>("anneeCotisation", annees);
            listAnnee.setRequired(true);
            add(listAnnee);
            // Ajout de la checkbox tiv
            add(new CheckBox("tiv"));
            //commentaire
            TextArea<String> textareaInput = new TextArea<String>("commentaire");
            textareaInput.add(ExactLengthValidator.maximumLength(45));
            add(textareaInput);

            ContactPanel cuPanel = new ContactPanel("cuPanel", adherent);
            add(cuPanel);

            add(new AjaxButton("validAdherent") {
                @Override
                // La validation doit se faire en Ajax car le formulaire de la
                // fenêtre principal n'y a pas accés
                // http://yeswicket.com/index.php?post/2010/04/26/G%C3%A9rer-facilement-les-fen%C3%AAtres-modales-avec-Wicket
                protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                    Adherent adherent = (Adherent) form.getModelObject();

                    // Mise au format des noms et prénom
                    adherent.setNom(adherent.getNom().toUpperCase());
                    adherent.setPrenom((adherent.getPrenom().substring(0, 1).toUpperCase()) + (adherent.getPrenom().substring(1).toLowerCase()));

                    // Mise à jour de l'adhérent
                    try {
                        ResaSession.get().getAdherentService().updateAdherent(adherent, PlongeeMail.PAS_DE_MAIL);
                        setResponsePage(GererAdherents.class);

                    } catch (TechnicalException e) {
                        e.printStackTrace();
                        error(e.getKey());
                    } catch (ResaException e) {
                        e.printStackTrace();
                        error(e.getKey());
                        setResponsePage(new GererAdherents());
                    }

                }

                // L'implémentation de cette méthode est nécessaire pour voir
                // les messages d'erreur dans le feedBackPanel
                @Override
                protected void onError(AjaxRequestTarget target, Form<?> form) {
                    target.addComponent(feedback);
                }
            });

//            add(new Button("cancel", new ResourceModel("button.cancel")));
            add(new Link("cancel") {
                @Override
                public void onClick() {
                    setModel(mAdhSav);
                    setResponsePage(GererAdherents.class);
                }
            });

        }
    }
}
