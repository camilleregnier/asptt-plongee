package com.asptt.plongee.resa.wicket.page.admin.adherent.contact;

import com.asptt.plongee.resa.model.Adherent;
import com.asptt.plongee.resa.model.ContactUrgent;
import com.asptt.plongee.resa.model.ContactUrgentDataProvider;
import java.util.List;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxLink;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;

public class ContactPanel extends Panel {

    private static final long serialVersionUID = 1L;
    private static CompoundPropertyModel<Adherent> modelAdherent;
    private static DataView<ContactUrgent> cuView;
    private static ModalWindow modalContactModif;
    private static ModalWindow modalContactCreer;
    private static ModalWindow modalConfirmSupp;

    public static ModalWindow getModalContactModif() {
        return modalContactModif;
    }

    public static ModalWindow getModalContactCreer() {
        return modalContactCreer;
    }

    public static ModalWindow getModalConfirmSupp() {
        return modalConfirmSupp;
    }

    public static CompoundPropertyModel<Adherent> getModelAdherent() {
        return modelAdherent;
    }

    public static DataView<ContactUrgent> getCuView() {
        return cuView;
    }

    public ContactPanel(String id, IModel<Adherent> adherent) {
        super(id, adherent);
        setOutputMarkupId(true);

        modalContactModif = new ModalWindow("modalContactModif");
        modalContactModif.setCookieName("modal-modif-contact");
        add(modalContactModif);

        modalContactCreer = new ModalWindow("modalContactCreer");
        modalContactCreer.setCookieName("modal-creer-contact");
        add(modalContactCreer);

        modalConfirmSupp = new ModalWindow("modalConfirmSupp");
        modalConfirmSupp.setCookieName("modal-supp-contact");
        modalConfirmSupp.setResizable(false);
        modalConfirmSupp.setInitialWidth(30);
        modalConfirmSupp.setInitialHeight(15);
        modalConfirmSupp.setWidthUnit("em");
        modalConfirmSupp.setHeightUnit("em");
        modalConfirmSupp.setCssClassName(ModalWindow.CSS_CLASS_BLUE);
        add(modalConfirmSupp);

        add(new ContactForm("inputForm", adherent));
    }

    class ContactForm extends Form {

        private static final long serialVersionUID = 5374674730458593314L;

        public ContactForm(String id, IModel<Adherent> adherent) {
            super(id, adherent);
            modelAdherent = new CompoundPropertyModel<Adherent>(adherent);
            setModel(modelAdherent);

            add(new IndicatingAjaxLink("create") {
                @Override
                public void onClick(AjaxRequestTarget target) {
                    replaceModalContactCreer(target);
                    modalContactCreer.show(target);

                }
            });

            List<ContactUrgent> contactUrgents = adherent.getObject().getContacts();
            cuView = new DataView<ContactUrgent>("cuView", new ContactUrgentDataProvider(contactUrgents), 20) {
                @Override
                protected void populateItem(final Item<ContactUrgent> item) {
                    final ContactUrgent contact = (ContactUrgent) item.getModelObject();
                    item.add(new IndicatingAjaxLink("modif") {
                        @Override
                        public void onClick(AjaxRequestTarget target) {
                            replaceModalContactModif(target, item.getModel());
                            modalContactModif.show(target);
                        }
                    });

                    item.add(new Label("titre", contact.getTitre()));
                    item.add(new Label("nom", contact.getNom()));
                    item.add(new Label("prenom", contact.getPrenom()));
                    item.add(new Label("telephone", contact.getTelephone()));
                    item.add(new Label("telephtwo", contact.getTelephtwo()));

                    item.add(new IndicatingAjaxLink("supp") {
                        @Override
                        public void onClick(AjaxRequestTarget target) {
                            replaceModalSuppContact(target, contact);
                            modalConfirmSupp.show(target);
                        }
                    });

                    item.add(new AttributeModifier("class", true,
                            new AbstractReadOnlyModel<String>() {
                                private static final long serialVersionUID = 5259097512265622750L;

                                @Override
                                public String getObject() {
                                    return (item.getIndex() % 2 == 1) ? "even" : "odd";
                                }
                            }));

                }
            };
            cuView.setOutputMarkupId(true);
            add(cuView);
        }
    }

    public static void replaceModalContactModif(AjaxRequestTarget target, IModel<ContactUrgent> contact) {
        modalContactModif.setContent(new ModifContactPanel(modalContactModif.getContentId(), contact));
        modalContactModif.setTitle("Modifiez les contacts \u00e0 mettre \u00e0 jour");
        modalContactModif.setUseInitialHeight(true);
        // Pour éviter le message de disparition de la fenetre lors de la validation
        target.appendJavascript("Wicket.Window.unloadConfirmation  = false;");
    }

    public static void replaceModalContactCreer(AjaxRequestTarget target) {
        modalContactCreer.setContent(new CreerContactPanel(modalContactCreer.getContentId()));
        modalContactCreer.setTitle("Creation des contacts");
        modalContactCreer.setUseInitialHeight(true);
        // Pour éviter le message de disparition de la fenetre lors de la validation
        target.appendJavascript("Wicket.Window.unloadConfirmation  = false;");
    }

    public static void replaceModalSuppContact(AjaxRequestTarget target, ContactUrgent contact) {
        modalConfirmSupp.setContent(new ConfirmSuppContactPanel(modalConfirmSupp.getContentId(), contact));
        modalConfirmSupp.setTitle("Confirmation de suppression d'un contact");
        // Pour éviter le message de disparition de la fenetre lors de la validation
        target.appendJavascript("Wicket.Window.unloadConfirmation  = false;");
    }
}
