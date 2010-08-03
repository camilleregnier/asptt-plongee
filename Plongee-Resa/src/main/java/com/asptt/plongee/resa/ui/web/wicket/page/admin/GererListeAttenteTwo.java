package com.asptt.plongee.resa.ui.web.wicket.page.admin;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.extensions.markup.html.form.palette.Palette;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.util.CollectionModel;
import org.apache.wicket.model.util.ListModel;

import com.asptt.plongee.resa.model.Adherent;
import com.asptt.plongee.resa.model.Plongee;
import com.asptt.plongee.resa.ui.web.wicket.page.TemplatePage;

public class GererListeAttenteTwo extends TemplatePage {
	public GererListeAttenteTwo(final Plongee plongee)
	{
		List<Adherent> persons = getResaSession().getPlongeeService().rechercherListeAttente(plongee);
		IChoiceRenderer<Adherent> renderer = new ChoiceRenderer<Adherent>("nomComplet", "nom");
		
		
		final Palette<Adherent> palette = new Palette<Adherent>("palette", 
				new ListModel<Adherent>(new ArrayList<Adherent>()), 
				new CollectionModel<Adherent>(persons), 
				renderer, 10, false);

		Form<?> form = new Form("form")
		{
			@Override
			protected void onSubmit()
			{
				info("selected person(s): " + palette.getDefaultModelObjectAsString());
				IModel modelAdherents  = palette.getDefaultModel();
				List<Adherent> adherents = (List<Adherent>) modelAdherents.getObject();
				for(Adherent adherent : adherents){
					getResaSession().getPlongeeService().inscrireAdherent(plongee, adherent);
					getResaSession().getPlongeeService().deInscrireAdherentEnListeAttente(plongee, adherent);
				}
			}
		};

		add(form);
		form.add(palette);

		add(new FeedbackPanel("feedback"));
	}

}
