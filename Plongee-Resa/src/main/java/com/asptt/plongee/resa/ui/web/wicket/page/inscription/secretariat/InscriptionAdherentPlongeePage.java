package com.asptt.plongee.resa.ui.web.wicket.page.inscription.secretariat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.util.string.Strings;
import org.wicketstuff.objectautocomplete.AutoCompletionChoicesProvider;
import org.wicketstuff.objectautocomplete.ObjectAutoCompleteBuilder;
import org.wicketstuff.objectautocomplete.ObjectAutoCompleteField;
import org.wicketstuff.objectautocomplete.ObjectAutoCompleteRenderer;
import org.wicketstuff.objectautocomplete.ObjectAutoCompleteSelectionChangeListener;

import com.asptt.plongee.resa.model.Adherent;
import com.asptt.plongee.resa.ui.web.wicket.ResaSession;
import com.asptt.plongee.resa.ui.web.wicket.page.AccueilPage;
import com.asptt.plongee.resa.ui.web.wicket.page.TemplatePage;

public class InscriptionAdherentPlongeePage extends TemplatePage {

	public InscriptionAdherentPlongeePage() {
		super();
		add(new AdherentForm("form"));

	}

	public List<Adherent> getMatchingAdherents(String search) {
		if (Strings.isEmpty(search)) {
			List<Adherent> emptyList = Collections.emptyList();
			return emptyList;
		}
		
		List<Adherent> list = getResaSession().getAdherentService()
				.rechercherAdherentTout();
		
		ArrayList<Adherent> newList = new ArrayList<Adherent>();
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i).getNom().startsWith(search.toUpperCase())) {
				newList.add(list.get(i));
			}
		}
		return newList;
	}
	
	class AdherentForm extends Form{
		
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
					return adherent.getNom();
				}
			};
			
			ObjectAutoCompleteBuilder<Adherent, String> builder = new ObjectAutoCompleteBuilder<Adherent, String>(provider);
			builder.autoCompleteRenderer(renderer);
			builder.searchLinkText("Autre recherche");


			autocompleteField = builder.build("numeroLicense", new Model<String>());
			final TextField<String> adherent = autocompleteField.getSearchTextField();
			adherent.setRequired(true);
			
			
			add(autocompleteField);
		}
		
		public void onSubmit() {
			System.out.println(autocompleteField.getConvertedInput());
			setResponsePage(AccueilPage.class);
		}
		
	}

}