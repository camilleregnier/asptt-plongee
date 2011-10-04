package com.asptt.plongee.resa.ui.web.wicket.page.admin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxLink;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxSubmitButton;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.navigation.paging.PagingNavigator;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.util.string.Strings;
import org.wicketstuff.objectautocomplete.AutoCompletionChoicesProvider;
import org.wicketstuff.objectautocomplete.ObjectAutoCompleteBuilder;
import org.wicketstuff.objectautocomplete.ObjectAutoCompleteField;
import org.wicketstuff.objectautocomplete.ObjectAutoCompleteRenderer;

import com.asptt.plongee.resa.model.Adherent;
import com.asptt.plongee.resa.model.AdherentDataProvider;
import com.asptt.plongee.resa.model.DetachableAdherentModel;
import com.asptt.plongee.resa.ui.web.wicket.page.TemplatePage;
import com.asptt.plongee.resa.ui.web.wicket.page.inscription.InscriptionPlongeePage;

public class GererAdherents extends TemplatePage {

	private ModalWindow modal2;
	private List<Adherent> list;
	
	public List<Adherent> getMatchingAdherents(String search) {
		if (Strings.isEmpty(search)) {
			List<Adherent> emptyList = Collections.emptyList();
			return emptyList;
		}
		
		if (list == null) {
			list = getResaSession().getAdherentService()
				.rechercherAdherentsActifs();
		}
		
		ArrayList<Adherent> newList = new ArrayList<Adherent>();
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i).getNom().startsWith(search.toUpperCase())) {
				newList.add(list.get(i));
			}
		}
		return newList;
	}

	@SuppressWarnings("serial")
	public GererAdherents() {

		modal2 = new ModalWindow("modal2");
		modal2.setTitle("This is modal window with panel content.");
		modal2.setCookieName("modal-adherent");
		add(modal2);
		
		List<Adherent> adherents = getResaSession().getAdherentService().rechercherAdherentsTous();
		
		// On construit la liste des adhérents (avec pagination)
		DataView<Adherent> dataView = new DataView<Adherent>(
				"simple",
				new AdherentDataProvider(adherents), 10) {

				protected void populateItem(final Item<Adherent> item) {
				final Adherent adherent = item.getModelObject();
				
				item.add(new IndicatingAjaxLink("select")
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
				
				// Dès que le plongeur est encadrant, on affiche son niveau d'encadrement
				String niveauAffiche = adherent.getPrerogative();
				item.add(new Label("niveau", niveauAffiche));
				item.add(new AttributeModifier("class", true,
						new AbstractReadOnlyModel<String>() {
							@Override
							public String getObject() {
								String cssClass;
								if (item.getIndex() % 2 == 1){
									cssClass = "even";
								} else {
									cssClass = "odd";
								}
								if (!adherent.isActif()){
									cssClass = cssClass + " inactif";
								}
								return cssClass;
							}
						}));
			}
		};
		add(dataView);
		add(new PagingNavigator("navigator", dataView));
		
		add(new AdherentForm("form"));
		
	}
	
	private void replaceModalWindow(AjaxRequestTarget target, IModel<Adherent> adherent) {
		modal2.setContent(new AdherentPanel(modal2.getContentId(), adherent));
		modal2.setTitle("Modifiez les informations \u00e0 mettre \u00e0 jour");
		modal2.setUseInitialHeight(true);
		
		// Pour éviter le message de disparition de la fenetre lors de la validation
		target.appendJavascript( "Wicket.Window.unloadConfirmation  = false;");
		}
	
	class AdherentForm extends Form{

		private static final long serialVersionUID = 5529259540931669786L;
		ObjectAutoCompleteField<Adherent, String> autocompleteField ;

		public AdherentForm(String id) {
			super(id);
			
			AutoCompletionChoicesProvider<Adherent> provider = new AutoCompletionChoicesProvider<Adherent>() {
				private static final long serialVersionUID = 1L;

				public Iterator<Adherent> getChoices(String input) {
					return getMatchingAdherents(input).iterator();
				}
			};
			
			ObjectAutoCompleteRenderer<Adherent> renderer = new ObjectAutoCompleteRenderer<Adherent>(){
				private static final long serialVersionUID = 1L;

				protected String getIdValue(Adherent adherent) {
					return adherent.getNumeroLicense();
				}
				protected String getTextValue(Adherent adherent) {
					String texteAffiche = adherent.getNom() + " " + adherent.getPrenom() + " " + (adherent.getPrerogative());
					return texteAffiche;
				}
			};
			
			ObjectAutoCompleteBuilder<Adherent, String> builder = new ObjectAutoCompleteBuilder<Adherent, String>(provider);
			builder.autoCompleteRenderer(renderer);
			builder.searchLinkText("Autre recherche");
			builder.width(200);


			autocompleteField = builder.build("numeroLicense", new Model<String>());
			final TextField<String> adherent = autocompleteField.getSearchTextField();
			adherent.setRequired(true);
		
			
			add(autocompleteField);
			
			add(new IndicatingAjaxSubmitButton("valider", this) {

				@Override
				protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
					IModel<Adherent> adh = new DetachableAdherentModel(getResaSession().getAdherentService().rechercherAdherentParIdentifiant(autocompleteField.getConvertedInput()));
					replaceModalWindow(target, adh);
					modal2.show(target);
				}

			});
		}
	}
}