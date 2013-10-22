package com.asptt.plongee.resa.wicket.page.inscription;

import com.asptt.plongee.resa.model.Adherent;
import com.asptt.plongee.resa.model.InscritsPlongeeDataProvider;
import com.asptt.plongee.resa.model.Plongee;
import com.asptt.plongee.resa.wicket.ResaSession;
import com.asptt.plongee.resa.wicket.page.TemplatePage;
import java.util.List;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.AbstractReadOnlyModel;

public class InscriptionConfirmationPlongeePage extends TemplatePage {

    @SuppressWarnings("serial")
    public InscriptionConfirmationPlongeePage(Plongee plongee) {
        setPageTitle("Inscription Plongee - Confirmation");
        // On affiche la liste des participants en guise de confirmation
        List<Adherent> adherentsInscrit = ResaSession.get().getPlongeeService().rechercherInscriptions(plongee, null, null, "date");

        add(new DataView<Adherent>("participants", new InscritsPlongeeDataProvider(adherentsInscrit)) {
            protected void populateItem(final Item<Adherent> item) {
                Adherent adherent = item.getModelObject();

                item.add(new Label("nom", adherent.getNom()));
                item.add(new Label("prenom", adherent.getPrenom()));

                // Dès que le plongeur est encadrant, on affiche son niveau d'encadrement
                String niveauAffiche = adherent.getPrerogative();

                // Pour les externes, le niveau est suffixé par (Ext.)
                if (adherent.getActifInt() == 2) {
                    niveauAffiche = niveauAffiche + " (Ext.)";
                }

                item.add(new Label("niveau", niveauAffiche));

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
