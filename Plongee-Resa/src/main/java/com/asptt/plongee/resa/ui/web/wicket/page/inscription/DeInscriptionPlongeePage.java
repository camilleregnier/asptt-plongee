package com.asptt.plongee.resa.ui.web.wicket.page.inscription;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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

import com.asptt.plongee.resa.model.Adherent;
import com.asptt.plongee.resa.model.Plongee;
import com.asptt.plongee.resa.service.PlongeeService;
import com.asptt.plongee.resa.ui.web.wicket.page.TemplatePage;

public class DeInscriptionPlongeePage extends TemplatePage {


	public DeInscriptionPlongeePage() {
		final FeedbackPanel feedback = new FeedbackPanel("feedback");
		add(feedback);
		add(new InscriptionPlongeeFrom("inputForm", feedback));
	}

	public class InscriptionPlongeeFrom extends Form {

		private static final long serialVersionUID = -1555366090072306934L;
		
		private List<Plongee> data;
		CheckGroup<Plongee> group = new CheckGroup<Plongee>("group", new ArrayList<Plongee>());

		@SuppressWarnings("serial")
		public InscriptionPlongeeFrom(String name, IFeedback feedback) {

			super(name);
					
			add(group);

			group.add(new CheckGroupSelector("groupselector"));
			
			/*
			 * Retourne la liste des plongées ouvertes, pour les 7 prochains jours
			 */
			data = getResaSession().getPlongeeService().rechercherPlongeeInscritForAdherent( 	
					getResaSession().getAdherent());

			ListView<Plongee> list = new ListView<Plongee>("plongeeList", data){
				public void populateItem(ListItem<Plongee> listItem) {           
					listItem.add(new Check<Plongee>("check", listItem.getModel()));
					
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
			Collection<Plongee> list = group.getModelObject();
			List<Plongee> plongees = new ArrayList<Plongee>(list);
			for(Plongee plongee : plongees){
				getResaSession().getPlongeeService().deInscrireAdherent(
						plongee, 
						getResaSession().getAdherent());
				setResponsePage(new InscriptionConfirmationPlongeePage(plongee));
			}
		}

	}

}
