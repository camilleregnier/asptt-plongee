package com.asptt.plongee.resa.ui.web.wicket.page.admin;

import java.util.Calendar;
import java.util.Locale;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.navigation.paging.PagingNavigator;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.AbstractReadOnlyModel;

import com.asptt.plongee.resa.model.Adherent;
import com.asptt.plongee.resa.model.InscritsPlongeeDataProvider;
import com.asptt.plongee.resa.model.Plongee;
import com.asptt.plongee.resa.ui.web.wicket.page.TemplatePage;

public class GererPlongeeAOuvrirThree extends TemplatePage {


	@SuppressWarnings("serial")
	public GererPlongeeAOuvrirThree(Plongee plongee) {
		DataView<Adherent> dataView = new DataView<Adherent>("pageable", new InscritsPlongeeDataProvider(getResaSession()
				.getPlongeeService(), getResaSession()
				.getAdherentService(), plongee)) {

			@SuppressWarnings("serial")
			protected void populateItem(final Item<Adherent> item) {
				Adherent adherent = (Adherent) item.getModelObject();
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
		};
		
		dataView.setItemsPerPage(8);
		add(dataView);

		add(new PagingNavigator("navigator", dataView));
		
		// Formatage de la date affichée
		Calendar cal = Calendar.getInstance();
		cal.setTime(plongee.getDate());
		String dateAffichee = cal.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.FRANCE) + " ";
		dateAffichee = dateAffichee + cal.get(Calendar.DAY_OF_MONTH) + " ";
		dateAffichee = dateAffichee + cal.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.FRANCE) + " ";
		dateAffichee = dateAffichee + cal.get(Calendar.YEAR);
		
		add(new Label("date", dateAffichee)); 

		
	}

}
