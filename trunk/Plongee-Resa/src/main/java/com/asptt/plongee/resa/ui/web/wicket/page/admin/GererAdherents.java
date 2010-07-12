package com.asptt.plongee.resa.ui.web.wicket.page.admin;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.navigation.paging.PagingNavigator;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;

import com.asptt.plongee.resa.model.Adherent;
import com.asptt.plongee.resa.model.AdherentDataProvider;
import com.asptt.plongee.resa.ui.web.wicket.page.TemplatePage;

public class GererAdherents extends TemplatePage {

	private ModalWindow modal2;

	@SuppressWarnings("serial")
	public GererAdherents() {

		modal2 = new ModalWindow("modal2");
		modal2.setTitle("This is modal window with panel content.");
		modal2.setCookieName("modal-adherent");
		add(modal2);

		// On construit la liste des adhérents (avec pagination)
		DataView<Adherent> dataView = new DataView<Adherent>(
				"simple",
				new AdherentDataProvider(getResaSession().getAdherentService()),
				5) {

			protected void populateItem(final Item<Adherent> item) {
				Adherent adherent = item.getModelObject();
				
				item.add(new AjaxLink("select")
				{
					@Override
					public void onClick(AjaxRequestTarget target)
					{
						replaceModalWindow(target, item.getModel());
						modal2.show(target);
					}
				});

				item.add(new Label("license", adherent.getNumeroLicense()));
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
		};
		add(dataView);
		add(new PagingNavigator("navigator", dataView));
	}
	
	private void replaceModalWindow(AjaxRequestTarget target, IModel<Adherent> adherent) {
		modal2.setContent(new AdherentPanel(modal2.getContentId(), adherent));
		modal2.setTitle("Modifiez les informations à mettre à jour");
		}
}