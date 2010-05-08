package com.asptt.plongee.resa.ui.web.wicket.page.consultation;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.extensions.markup.html.form.palette.Palette;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.util.CollectionModel;
import org.apache.wicket.model.util.ListModel;

import com.asptt.plongee.resa.model.Adherent;
import com.asptt.plongee.resa.ui.web.wicket.page.TemplatePage;

public class ConsulterPlongee extends TemplatePage {
	
	public ConsulterPlongee()
	{
		List<Adherent> persons = getResaSession().getAdherentService().rechercherAdherentTout();
		IChoiceRenderer<Adherent> renderer = new ChoiceRenderer<Adherent>("nom", "nom");

//		final Palette<Adherent> palette = new Palette<Adherent>("palette", new ListModel<Adherent>(
//				new ArrayList<Adherent>()), new CollectionModel<Adherent>(persons), renderer, 10, true);
		final Palette<Adherent> palette = new Palette<Adherent>("palette", 
				new ListModel<Adherent>(new ArrayList<Adherent>()), 
				new CollectionModel<Adherent>(persons), 
				renderer, 10, true);


		Form<?> form = new Form("form")
		{
			@Override
			protected void onSubmit()
			{
				info("selected person(s): " + palette.getDefaultModelObjectAsString());
			}
		};

		add(form);
		form.add(palette);

		add(new FeedbackPanel("feedback"));
	}

}
