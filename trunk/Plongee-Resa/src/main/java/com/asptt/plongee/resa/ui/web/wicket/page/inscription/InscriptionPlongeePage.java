package com.asptt.plongee.resa.ui.web.wicket.page.inscription;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.apache.wicket.datetime.markup.html.basic.DateLabel;
import org.apache.wicket.feedback.IFeedback;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Check;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.CheckBoxMultipleChoice;
import org.apache.wicket.markup.html.form.CheckGroup;
import org.apache.wicket.markup.html.form.CheckGroupSelector;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.util.convert.converters.DateConverter;

import com.asptt.plongee.resa.model.Plongee;
import com.asptt.plongee.resa.ui.web.wicket.page.TemplatePage;

public class InscriptionPlongeePage extends TemplatePage {

	public InscriptionPlongeePage() {
		final FeedbackPanel feedback = new FeedbackPanel("feedback");
		add(feedback);
		add(new InscriptionPlongeeFrom("inputForm", feedback));
	}

	public class InscriptionPlongeeFrom extends Form {

		private static final long serialVersionUID = -1555366090072306934L;

		private List<Plongee> data;
		CheckGroup<Plongee> group = new CheckGroup<Plongee>("group", new ArrayList<Plongee>());

		public InscriptionPlongeeFrom(String name, IFeedback feedback) {

			super(name);
					
			add(group);

			group.add(new CheckGroupSelector("groupselector"));
			
			data = 	getResaSession().getPlongeeService().rechercherPlongeeTout();

			ListView<Plongee> list = new ListView<Plongee>("plongeeList", data){
				public void populateItem(ListItem<Plongee> listItem) {           
					listItem.add(new Check<Plongee>("check", listItem.getModel()));
					listItem.add(new Label("date", new PropertyModel<Date>(listItem.getDefaultModel(), "date")));                
					listItem.add(new Label("type",new PropertyModel<String>(listItem.getDefaultModel(), "type")));
				}
			
			}.setReuseItems(true);
			group.add(list);
			
		}

		public void onSubmit() {
			// TODO appeler le service d'inscription
			Collection<Plongee> list = group.getModelObject();
			System.out.println("nb de plongée : " +list.size());
			setResponsePage(InscriptionConfirmationPlongeePage.class);
		}

	}

}
