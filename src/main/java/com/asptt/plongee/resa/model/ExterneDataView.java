/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asptt.plongee.resa.model;

import com.asptt.plongee.resa.wicket.page.admin.externe.GererExternes;
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
public class ExterneDataView extends DataView<Adherent> {

    public ExterneDataView(String id, IDataProvider dataProvider) {
        super(id, dataProvider);
    }

    public ExterneDataView(String id, IDataProvider dataProvider, int itemsPerPage) {
        super(id, dataProvider, itemsPerPage);
    }

    @Override
    protected void populateItem(final Item item) {
        final Adherent externe = (Adherent) item.getModelObject();

        item.add(new IndicatingAjaxLink("select") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                GererExternes.replaceModalWindowModif(target, item.getModel());
                GererExternes.getModalModif().show(target);
            }
        });

        item.add(new IndicatingAjaxLink("suppAdh") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                GererExternes.replaceModalWindowSupp(target, item.getModel());
                GererExternes.getModalSupp().show(target);
            }
        });

        item.add(new Label("license", externe.getNumeroLicense()));
        item.add(new Label("nom", externe.getNom()));
        item.add(new Label("prenom", externe.getPrenom()));

        // Dès que le plongeur est encadrant, on affiche son niveau d'encadrement
        String niveauAffiche = externe.getPrerogative();
        item.add(new Label("niveau", niveauAffiche));
        item.add(new AttributeModifier("class", true,
                new AbstractReadOnlyModel<String>() {
                    @Override
                    public String getObject() {
                        String cssClass;
                        if (item.getIndex() % 2 == 1) {
                            cssClass = "even";
                        } else {
                            cssClass = "odd";
                        }
//                        if (!externe.isActif()) {
//                            cssClass = cssClass + " inactif";
//                        }
                        return cssClass;
                    }
                }));
    }
}
