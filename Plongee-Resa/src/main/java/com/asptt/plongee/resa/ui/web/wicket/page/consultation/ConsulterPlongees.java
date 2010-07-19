package com.asptt.plongee.resa.ui.web.wicket.page.consultation;

import java.util.Calendar;
import java.util.Locale;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.version.undo.Change;

import com.asptt.plongee.resa.model.Adherent;
import com.asptt.plongee.resa.model.InscritsPlongeeDataProvider;
import com.asptt.plongee.resa.model.ListeAttentePlongeeDataProvider;
import com.asptt.plongee.resa.model.Plongee;
import com.asptt.plongee.resa.model.PlongeeDataProvider;
import com.asptt.plongee.resa.ui.web.wicket.page.TemplatePage;

@AuthorizeInstantiation({"USER", "SECRETARIAT"})
public class ConsulterPlongees extends TemplatePage {
	Plongee selected;

	InscritsPlongeeDataProvider inscrit = new InscritsPlongeeDataProvider(
			getResaSession().getPlongeeService(), getResaSession()
					.getAdherentService(), getSelected());

	private WebMarkupContainer dynamicPanel;

	@SuppressWarnings("serial")
	public ConsulterPlongees() {

		dynamicPanel = new WebMarkupContainer("dynamicPanel");
		dynamicPanel.setOutputMarkupId(true);
		add(dynamicPanel);


		add(new DataView<Plongee>("simple", new PlongeeDataProvider(
				getResaSession().getPlongeeService())) {
			protected void populateItem(final Item<Plongee> item) {
				Plongee plongee = item.getModelObject();
				String nomDP = "Aucun";
				if (null != plongee.getDp()) {
					nomDP = plongee.getDp().getNom();
				}

				item.add(new AjaxLink("select") {
					@Override
					public void onClick(AjaxRequestTarget target) {
						replaceEmptyPanel(target,
								new ParticipantsPanel("dynamicPanel", item.getModel()));
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

	public String getSelectedPlongeeLabel() {
		if (selected == null) {
			return "Pas de plongee selectionnee";
		} else {
			return String.valueOf(selected.getId());
		}
	}

	public Plongee getSelected() {
		if (null == selected) {
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

	public void setSelected(Plongee selected) {
		addStateChange(new Change() {
			private final Plongee old = ConsulterPlongees.this.selected;

			@Override
			public void undo() {
				ConsulterPlongees.this.selected = old;

			}
		});
		this.selected = selected;
	}

	private void replaceEmptyPanel(AjaxRequestTarget target, Panel newPanel) {

		this.dynamicPanel.replaceWith(newPanel);
		this.dynamicPanel = newPanel;
		target.addComponent(newPanel);

	}

	class ParticipantsPanel extends Panel {
		@SuppressWarnings("serial")
		public ParticipantsPanel(String id, IModel<Plongee> plongee) {
			super(id, plongee);
			setOutputMarkupId(true);

			add(new DataView<Adherent>("participants",
					new InscritsPlongeeDataProvider(getResaSession()
							.getPlongeeService(), getResaSession()
							.getAdherentService(), plongee.getObject())) {
				protected void populateItem(final Item<Adherent> item) {
					Adherent adherent = item.getModelObject();

					item.add(new Label("nom", adherent.getNom()));
					item.add(new Label("prenom", adherent.getPrenom()));
					item.add(new Label("niveau", adherent.getNiveau()));

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
			add(new DataView<Adherent>("listeAttente",
					new ListeAttentePlongeeDataProvider(getResaSession()
							.getPlongeeService(), getResaSession()
							.getAdherentService(), plongee.getObject())) {
				protected void populateItem(final Item<Adherent> item) {
					Adherent adherent = item.getModelObject();

					item.add(new Label("nom", adherent.getNom()));
					item.add(new Label("prenom", adherent.getPrenom()));
					item.add(new Label("niveau", adherent.getNiveau()));

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
