package com.asptt.plongee.resa.ui.web.wicket.page.admin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.wicket.datetime.StyleDateConverter;
import org.apache.wicket.datetime.markup.html.form.DateTextField;
import org.apache.wicket.extensions.yui.calendar.DatePicker;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.PropertyModel;

import com.asptt.plongee.resa.model.NiveauAutonomie;
import com.asptt.plongee.resa.model.Plongee;
import com.asptt.plongee.resa.ui.web.wicket.page.AccueilPage;
import com.asptt.plongee.resa.ui.web.wicket.page.TemplatePage;

public class CreerPlongee extends TemplatePage {
	
	private static final Locale LOCALE_EN = new Locale("en");
	
	public CreerPlongee() {
		// Constructeur du formulaire et du feedback panel pour renvoyer des messages sur la page
		final FeedbackPanel feedback = new FeedbackPanel("feedback");
		add(feedback);
		add(new PlongeeForm("inputForm"));
	}

	public class PlongeeForm extends Form<Plongee> {

		private static final long serialVersionUID = 5374674730458593314L;

		public PlongeeForm(String id) {
			super(id);
			CompoundPropertyModel<Plongee> model = new CompoundPropertyModel<Plongee>(new Plongee());
			setModel(model);
			
			DateTextField dateTextFiled = new DateTextField("date", new PropertyModel<Date>(model, "date"), new StyleDateConverter("S-", true));
			dateTextFiled.setRequired(true);
			add(dateTextFiled);
			dateTextFiled.add(new DatePicker());
			
			add(new RequiredTextField<Integer>("nbMaxPlaces", Integer.class));
			
			
			// Ajout de la liste des niveaux
			List<String> niveaux = new ArrayList<String>();
			for (NiveauAutonomie n : NiveauAutonomie.values()){
				niveaux.add(n.toString());
			}
			add(new DropDownChoice("niveauMinimum", niveaux));
			
			//Ajout du type de plongee
			List<String> type = Arrays.asList(new String[] { "MATIN", "APRES_MIDI", "SOIR", "NUIT" });
			add(new DropDownChoice<String>("type", type));
			
		}

		public void onSubmit() {
			Plongee plongee = (Plongee) getModelObject();
			
			getResaSession().getPlongeeService().creerPlongee(plongee);

			setResponsePage(AccueilPage.class);
		}

	}


}
