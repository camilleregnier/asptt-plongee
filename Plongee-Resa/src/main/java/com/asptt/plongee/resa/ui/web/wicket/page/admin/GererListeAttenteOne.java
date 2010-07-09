package com.asptt.plongee.resa.ui.web.wicket.page.admin;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import org.apache.wicket.PageParameters;
import org.apache.wicket.feedback.IFeedback;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.Radio;
import org.apache.wicket.markup.html.form.RadioGroup;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

import com.asptt.plongee.resa.model.Plongee;
import com.asptt.plongee.resa.ui.web.wicket.page.TemplatePage;

public class GererListeAttenteOne extends TemplatePage {


	public GererListeAttenteOne() {
		final FeedbackPanel feedback = new FeedbackPanel("feedback");
		add(feedback);
		add(new GererListeAttenteOneForm("inputForm", feedback));
	}

	public class GererListeAttenteOneForm extends Form {

		private static final long serialVersionUID = -1555366090072306934L;
		
		private List<Plongee> data;
		RadioGroup<Plongee> group = new RadioGroup<Plongee>("group", new Model<Plongee>());

		@SuppressWarnings("serial")
		public GererListeAttenteOneForm(String name, IFeedback feedback) {

			super(name);
					
			add(group);

			data = getResaSession().getPlongeeService().rechercherPlongeesOuvertesWithAttente( 	
					getResaSession().getPlongeeService().rechercherPlongeeTout());

			ListView<Plongee> list = new ListView<Plongee>("plongeeList", data){
				public void populateItem(ListItem<Plongee> listItem) {           
					listItem.add(new Radio<Plongee>("radio", listItem.getModel()));
					
					// Formatage de la date affichée
					Calendar cal = Calendar.getInstance();
					cal.setTime(listItem.getModel().getObject().getDate());
					String dateAffichee = cal.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.FRANCE) + " ";
					dateAffichee = dateAffichee + cal.get(Calendar.DAY_OF_MONTH) + " ";
					dateAffichee = dateAffichee + cal.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.FRANCE) + " ";
					dateAffichee = dateAffichee + cal.get(Calendar.YEAR);
					
					listItem.add(new Label("date", dateAffichee));                
					listItem.add(new Label("type",new PropertyModel<String>(listItem.getDefaultModel(), "type")));
				}
			
			}.setReuseItems(true);
			group.add(list);
			
		}

		public void onSubmit() {
			IModel<Plongee>  list = group.getModel();
			System.out.println("la plongée : " +list.getObject().getId());
			PageParameters param = new PageParameters();
			param.put("plongeeAttente", list.getObject());
			setResponsePage(new GererListeAttenteTwo(list.getObject()));
		}

	}

}
