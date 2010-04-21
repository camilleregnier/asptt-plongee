package com.asptt.plongee.resa.ui.web.wicket.page.inscription;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.wicket.feedback.IFeedback;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.PropertyModel;

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

		public InscriptionPlongeeFrom(String name, IFeedback feedback) {

			super(name);

//			// ajout de quelques plong�es, mais �a devra �tre r�cup�r� de la base
//			data = new ArrayList<Plongee>();
//			Calendar cal = Calendar.getInstance();
//			data.add(new Plongee(cal.getTime(), false));
//			cal.add(Calendar.DAY_OF_WEEK, 1);
//			data.add(new Plongee(cal.getTime(), false));
//			cal.add(Calendar.DAY_OF_WEEK, 1);
//			data.add(new Plongee(cal.getTime(), false));
//			cal.add(Calendar.DAY_OF_WEEK, 1);
//			data.add(new Plongee(cal.getTime(), false));
//		
//			ListView<Plongee> listView = new ListView<Plongee>("list", data) {
//				protected void populateItem(ListItem<Plongee> item) {
//					Plongee plongee = (Plongee) item.getModelObject();
//					item.add(new Label("name", plongee.getDate().toString()));
//					item.add(new CheckBox("check", new PropertyModel<Boolean>(
//							plongee, "statut")));
//				}
//			};
//			listView.setReuseItems(true);
//			add(listView);
			
			data = 	getResaSession().getPlongeeService().rechercherPlongeeTout();

			ListView<Plongee> listView2 = new ListView("plongeeList", data){
				public void populateItem(final ListItem listItem) {                
					final Plongee plongee =	(Plongee)listItem.getModelObject();                
					listItem.add(new Label("id", new Integer(plongee.getId()).toString()));                
					listItem.add(new Label("date", plongee.getDate().toString()));                
					listItem.add(new Label("demiJournee",plongee.getType().toString()));
					//listItem.add(new Label("statut",plongee.getStatut()));
					//listItem.add(new CheckBox("check", new PropertyModel<Boolean>(plongee, "statut")));
					listItem.add(new CheckBox("check"));
				}
			
			};
			
			listView2.setReuseItems(true);
			add(listView2);
			
		}

		public void onSubmit() {
			info("data: " + data); // print current contents
			setResponsePage(InscriptionConfirmationPlongeePage.class);
		}

	}

}
