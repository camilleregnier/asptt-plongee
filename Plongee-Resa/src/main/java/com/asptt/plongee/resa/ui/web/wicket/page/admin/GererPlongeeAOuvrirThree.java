package com.asptt.plongee.resa.ui.web.wicket.page.admin;

import java.util.List;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.feedback.IFeedback;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.navigation.paging.PagingNavigator;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.markup.repeater.data.ListDataProvider;
import org.apache.wicket.model.AbstractReadOnlyModel;

import com.asptt.plongee.resa.model.Adherent;
import com.asptt.plongee.resa.model.Plongee;
import com.asptt.plongee.resa.ui.web.wicket.page.TemplatePage;
import com.asptt.plongee.resa.ui.web.wicket.page.inscription.InscriptionPlongeePage.InscriptionPlongeeFrom;

public class GererPlongeeAOuvrirThree extends TemplatePage {

	List<Adherent> adherents;
	Plongee plongee;
	public GererPlongeeAOuvrirThree(List<Adherent> adh, Plongee plg ){
		super();
		this.adherents = adh;
		this.plongee=plg;
		final FeedbackPanel feedback = new FeedbackPanel("feedback");
		add(feedback);
		ListDataAdherent lda = new ListDataAdherent("inputForm", feedback);
		try {
			lda.init();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		add(lda);
		
		
	}
		
	public class ListDataAdherent extends Form{	
	
		public ListDataAdherent(String id, IFeedback feedback) {
			super(id);
		}
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		public void init() throws Exception {
		DataView dataView = new DataView("pageable", new ListDataProvider(adherents))
		{
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(final Item item)
			{
				Adherent contact = (Adherent) item.getModelObject();
//				item.add(new ActionPanel("actions", item.getModel()));
				item.add(new Label("id", String.valueOf(contact.getNumeroLicense())));
				item.add(new Label("nom", contact.getNom()));
				item.add(new Label("prenom", contact.getPrenom()));

//				item.add(new AttributeModifier("class", true, new AbstractReadOnlyModel<String>()
//				{
//					@Override
//					public String getObject()
//					{
//						return (item.getIndex() % 2 == 1) ? "even" : "odd";
//					}
//				}));
			}
		};

		dataView.setItemsPerPage(8);
		add(dataView);

		add(new PagingNavigator("navigator", dataView));
		}
	}

}
