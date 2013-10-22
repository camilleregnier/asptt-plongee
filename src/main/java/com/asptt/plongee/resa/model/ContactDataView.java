/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asptt.plongee.resa.model;

import com.asptt.plongee.resa.wicket.page.admin.adherent.contact.ContactPanel;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxLink;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.AbstractReadOnlyModel;

/**
 *
 * @author ecus6396
 */
public class ContactDataView extends DataView<ContactUrgent> {

    public ContactDataView(String id, IDataProvider dataProvider) {
        super(id, dataProvider);
    }

    public ContactDataView(String id, IDataProvider dataProvider, int itemsPerPage) {
        super(id, dataProvider, itemsPerPage);
    }

    @Override
    protected void populateItem(final Item<ContactUrgent> item) {
        final ContactUrgent contact = (ContactUrgent) item.getModelObject();
        item.add(new IndicatingAjaxLink("modif") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                ContactPanel.replaceModalContactModif(target, item.getModel());
                ContactPanel.getModalContactModif().show(target);
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
                ContactPanel.replaceModalSuppContact(target, contact);
                ContactPanel.getModalConfirmSupp().show(target);
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
}
