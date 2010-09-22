package com.asptt.plongee.resa.ui.web.wicket.page.admin;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxLink;
import org.apache.wicket.feedback.IFeedback;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.AbstractReadOnlyModel;

import com.asptt.plongee.resa.model.Plongee;
import com.asptt.plongee.resa.model.PlongeeDataProvider;
import com.asptt.plongee.resa.ui.web.wicket.page.TemplatePage;

public class GererListeAttenteOne extends TemplatePage {

	FeedbackPanel feedback = new FeedbackPanel("feedback");

	public GererListeAttenteOne() {
		add(new Label("message", getResaSession().getAdherent().getPrenom() + ", ci-dessous, les plongées pour lesquelles une liste d'attente existe."));
		feedback.setOutputMarkupId(true);
		add(feedback);
		add(new GererListeAttenteOneForm("inputForm", feedback));
	}

	public class GererListeAttenteOneForm extends Form {

		private static final long serialVersionUID = -1555366090072306934L;
		
		private List<Plongee> plongees;

		@SuppressWarnings("serial")
		public GererListeAttenteOneForm(String name, IFeedback feedback) {

			super(name);

			plongees = getResaSession().getPlongeeService().rechercherPlongeesOuvertesWithAttente( 	
					getResaSession().getPlongeeService().rechercherPlongeeTout());
			
			PlongeeDataProvider pDataProvider = new PlongeeDataProvider(plongees);

			add(new DataView<Plongee>("simple", pDataProvider) {
				
				protected void populateItem(final Item<Plongee> item) {          
						Plongee plongee = item.getModelObject();
						String nomDP = "Aucun";
						if (null != plongee.getDp()) {
							nomDP = plongee.getDp().getNom();
						}

						item.add(new IndicatingAjaxLink("select") {
							@Override
							public void onClick(AjaxRequestTarget target) {
								setResponsePage(new GererListeAttenteTwo(item.getModelObject()));
							}
						});

						// Mise en forme de la date
						Calendar cal = Calendar.getInstance();
						cal.setTime(plongee.getDate());
						String dateAffichee = cal.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.FRANCE) + " ";
						dateAffichee = dateAffichee + cal.get(Calendar.DAY_OF_MONTH) + " ";
						dateAffichee = dateAffichee + cal.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.FRANCE) + " ";
						dateAffichee = dateAffichee + cal.get(Calendar.YEAR);
						
						item.add(new Label("date", dateAffichee));
						item.add(new Label("dp", nomDP));
						item.add(new Label("type", plongee.getType()));
						item.add(new Label("niveauMini", plongee.getNiveauMinimum().toString()));
						
						// Places restantes
						item.add(new Label("placesRestantes", getResaSession().getPlongeeService().getNbPlaceRestante(plongee).toString()));


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
}
