/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asptt.plongee.resa.model;

import com.asptt.plongee.resa.wicket.page.admin.adherent.GererAdherents;
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
public class AdherentDataView extends DataView<Adherent> {

    public AdherentDataView(String id, IDataProvider dataProvider) {
        super(id, dataProvider);
    }

    public AdherentDataView(String id, IDataProvider dataProvider, int itemsPerPage) {
        super(id, dataProvider, itemsPerPage);
    }

    @Override
    protected void populateItem(final Item item) {
        final Adherent adherent = (Adherent) item.getModelObject();

        item.add(new IndicatingAjaxLink("select") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                GererAdherents.replaceModalWindowModif(target, item.getModel());
                GererAdherents.getModalModif().show(target);
            }
        });

        item.add(new IndicatingAjaxLink("suppAdh") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                GererAdherents.replaceModalWindowSupp(target, item.getModel());
                GererAdherents.getModalSupp().show(target);
            }
        });

        item.add(new IndicatingAjaxLink("pwdAdh") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                GererAdherents.replaceModalWindowPwd(target, item.getModel());
                GererAdherents.getModalPwd().show(target);
            }
        });

        item.add(new Label("license", adherent.getNumeroLicense()));
        item.add(new Label("nom", adherent.getNom()));
        item.add(new Label("prenom", adherent.getPrenom()));

        // Dès que le plongeur est encadrant, on affiche son niveau d'encadrement
        String niveauAffiche = adherent.getPrerogative();
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
                        if (!adherent.isActif()) {
                            cssClass = cssClass + " inactif";
                        }
                        return cssClass;
                    }
                }));
    }
}
