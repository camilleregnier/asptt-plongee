package com.asptt.plongee.resa.ui.web.wicket.page.admin;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.version.undo.Change;

import com.asptt.plongee.resa.model.Adherent;
import com.asptt.plongee.resa.model.InscritsPlongeeDataProvider;
import com.asptt.plongee.resa.model.Plongee;
import com.asptt.plongee.resa.model.PlongeeDataProvider;
import com.asptt.plongee.resa.ui.web.wicket.page.TemplatePage;
import com.asptt.plongee.resa.ui.web.wicket.page.admin.CreerAdherent.MyForm;

@AuthorizeInstantiation("USER")
public class ConsulterPlongees extends TemplatePage {
	Plongee selected;
	
	InscritsPlongeeDataProvider inscrit = new InscritsPlongeeDataProvider(getResaSession().getPlongeeService(),
			getResaSession().getAdherentService(), getSelected());
	
	public ConsulterPlongees() {
		
		add(new Label("selectedLabel", new PropertyModel<String>(this, "selectedPlongeeLabel")));
		
		add(new DataView<Plongee>("simple", new PlongeeDataProvider(getResaSession().getPlongeeService()))
				{
					protected void populateItem(final Item<Plongee> item)
					{
						Plongee plongee = item.getModelObject();
						String nomDP = "Aucun";
						if(null != plongee.getDp()){
							nomDP = plongee.getDp().getNom();
						}
						item.add(new ActionPanel("actions", item.getModel()));
						item.add(new Label("id", String.valueOf(plongee.getId())));
						item.add(new Label("date", plongee.getDate().toString()));
						item.add(new Label("dp", nomDP));
						item.add(new Label("type", plongee.getType()));

						item.add(new AttributeModifier("class", true, new AbstractReadOnlyModel<String>()
						{
							@Override
							public String getObject()
							{
								return (item.getIndex() % 2 == 1) ? "even" : "odd";
							}
						}));
					}
				});
		
//		add(new DataView<Adherent>("participants", new InscritsPlongeeDataProvider(getResaSession().getPlongeeService(),
//				getResaSession().getAdherentService(), getSelected()))
		add(new DataView<Adherent>("participants", inscrit)
				{
					protected void populateItem(final Item<Adherent> item)
					{
						Adherent adherent = item.getModelObject();
						
						item.add(new Label("nom", adherent.getNom()));
						item.add(new Label("prenom", adherent.getPrenom()));
						item.add(new Label("niveau", adherent.getNiveau()));

						item.add(new AttributeModifier("class", true, new AbstractReadOnlyModel<String>()
						{
							@Override
							public String getObject()
							{
								return (item.getIndex() % 2 == 1) ? "even" : "odd";
							}
						}));
					}
				});
				
	}
	
	public String getSelectedPlongeeLabel()
	{
		if (selected == null)
		{
			return "Pas de plongee selectionnee";
		}
		else
		{
			return String.valueOf(selected.getId());
		}
	}
	
	public Plongee getSelected()
	{
		if(null == selected){
			selected = new Plongee();
			selected.setId(-1);
		}
		return selected;
	}

	/**
	 * sets selected contact
	 * 
	 * @param selected
	 */
	
	public void setSelected(Plongee selected)
	{
		addStateChange(new Change()
		{
			private final Plongee old = ConsulterPlongees.this.selected;

			@Override
			public void undo()
			{
				ConsulterPlongees.this.selected = old;

			}
		});
		this.selected = selected;
	}
	
	class ActionPanel extends Panel
	{
		public ActionPanel(String id, IModel<Plongee> model)
		{
			super(id, model);
			add(new Link("select")
			{
				@Override
				public void onClick()
				{
					selected = (Plongee)getParent().getDefaultModelObject();
					
					inscrit.model(selected);
				}
			});
		}
	}

}
