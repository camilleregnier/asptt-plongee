package com.asptt.plongee.resa.ui.web.wicket.page.inscription;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.AbstractReadOnlyModel;

import com.asptt.plongee.resa.model.Adherent;
import com.asptt.plongee.resa.model.ListeAttentePlongeeDataProvider;
import com.asptt.plongee.resa.model.Plongee;
import com.asptt.plongee.resa.ui.web.wicket.page.TemplatePage;

public class InscriptionListeAttentePlongeePage extends TemplatePage {

	@SuppressWarnings("serial")
	public InscriptionListeAttentePlongeePage(Plongee plongee) {
		
		// On affiche la liste d'attente pour information
		add(new DataView<Adherent>("listeAttente",
				new ListeAttentePlongeeDataProvider(getResaSession()
						.getPlongeeService(), getResaSession()
						.getAdherentService(), plongee)) {
			protected void populateItem(final Item<Adherent> item) {
				Adherent adherent = item.getModelObject();

				item.add(new Label("nom", adherent.getNom()));
				item.add(new Label("prenom", adherent.getPrenom()));
				item.add(new Label("niveau", adherent.getNiveau()));

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
