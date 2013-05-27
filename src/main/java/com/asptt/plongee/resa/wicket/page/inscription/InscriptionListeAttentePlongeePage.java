package com.asptt.plongee.resa.wicket.page.inscription;

import java.util.List;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.AbstractReadOnlyModel;

import com.asptt.plongee.resa.model.Adherent;
import com.asptt.plongee.resa.model.ListeAttentePlongeeDataProvider;
import com.asptt.plongee.resa.model.Plongee;
import com.asptt.plongee.resa.wicket.page.TemplatePage;

public class InscriptionListeAttentePlongeePage extends TemplatePage {

	@SuppressWarnings("serial")
	public InscriptionListeAttentePlongeePage(Plongee plongee, String message) {
		setPageTitle("Inscription liste d'attente");
		add(new Label("lib", message));
		// On affiche la liste d'attente pour information
		List<Adherent> adhereAttente = getResaSession().getPlongeeService().rechercherListeAttente(plongee);
		add(new DataView<Adherent>("listeAttente",
				new ListeAttentePlongeeDataProvider(adhereAttente)) {
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
