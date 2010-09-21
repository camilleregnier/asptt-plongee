package com.asptt.plongee.resa.ui.web.wicket.page.admin;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import org.apache.wicket.PageParameters;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxLink;
import org.apache.wicket.feedback.IFeedback;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.Radio;
import org.apache.wicket.markup.html.form.RadioGroup;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.validation.validator.DateValidator;

import com.asptt.plongee.resa.model.Plongee;
import com.asptt.plongee.resa.model.PlongeeDataProvider;
import com.asptt.plongee.resa.ui.web.wicket.page.TemplatePage;

public class GererListeAttenteOne extends TemplatePage {

	FeedbackPanel feedback = new FeedbackPanel("feedback");

	public GererListeAttenteOne() {
		feedback.setOutputMarkupId(true);
		add(feedback);
		add(new GererListeAttenteOneForm("inputForm", feedback));
	}

	public class GererListeAttenteOneForm extends Form {

		private static final long serialVersionUID = -1555366090072306934L;
		
		private List<Plongee> plongees;
		RadioGroup<Plongee> group = new RadioGroup<Plongee>("group", new Model<Plongee>());

		@SuppressWarnings("serial")
		public GererListeAttenteOneForm(String name, IFeedback feedback) {

			super(name);
					
			add(group);

			plongees = getResaSession().getPlongeeService().rechercherPlongeesOuvertesWithAttente( 	
					getResaSession().getPlongeeService().rechercherPlongeeTout());

			PlongeeDataProvider pDataProvider = new PlongeeDataProvider(plongees);
			
			DataView<Plongee> dataPlongees = new DataView<Plongee>("plongeeList", pDataProvider){
				protected void populateItem(final Item<Plongee> listItem) {
					Plongee plongee = listItem.getModelObject();
					
					IndicatingAjaxLink link = new IndicatingAjaxLink("select") {
						@Override
						public void onClick(AjaxRequestTarget target) {
							IModel<Plongee>  list = listItem.getModel();
							System.out.println("la plongée : " +list.getObject().getId());
							PageParameters param = new PageParameters();
							param.put("plongeeAttente", list.getObject());
							setResponsePage(new GererListeAttenteTwo(list.getObject()));
						}
					};
					listItem.add(link);
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
			
			};
			group.add(dataPlongees);
			
		}

	}

}
