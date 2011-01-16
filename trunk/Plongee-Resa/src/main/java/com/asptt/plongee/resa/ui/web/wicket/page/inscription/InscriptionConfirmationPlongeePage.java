package com.asptt.plongee.resa.ui.web.wicket.page.inscription;

import java.util.List;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.AbstractReadOnlyModel;

import com.asptt.plongee.resa.model.Adherent;
import com.asptt.plongee.resa.model.InscritsPlongeeDataProvider;
import com.asptt.plongee.resa.model.Plongee;
import com.asptt.plongee.resa.ui.web.wicket.page.TemplatePage;

public class InscriptionConfirmationPlongeePage extends TemplatePage {

	@SuppressWarnings("serial")
	public InscriptionConfirmationPlongeePage(Plongee plongee) {
		
		// On affiche la liste des participants en guise de confirmation
		List<Adherent> adherentsInscrit = getResaSession().getPlongeeService().rechercherInscriptions(plongee,null,null,"date");
		
		add(new DataView<Adherent>("participants",new InscritsPlongeeDataProvider(adherentsInscrit)) {
			
			protected void populateItem(final Item<Adherent> item) {
				Adherent adherent = item.getModelObject();

				item.add(new Label("nom", adherent.getNom()));
				item.add(new Label("prenom", adherent.getPrenom()));
				
				// Dès que le plongeur est encadrant, on affiche son niveau d'encadrement
				String niveauAffiche = adherent.getPrerogative();
				
				// Pour les externes, le niveau est suffixé par (Ext.)
				if (adherent.getActifInt() ==2){
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
