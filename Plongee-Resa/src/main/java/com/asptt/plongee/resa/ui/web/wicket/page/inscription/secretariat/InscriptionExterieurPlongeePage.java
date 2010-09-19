package com.asptt.plongee.resa.ui.web.wicket.page.inscription.secretariat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxLink;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxSubmitButton;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.Model;
import org.apache.wicket.util.string.Strings;
import org.wicketstuff.objectautocomplete.AutoCompletionChoicesProvider;
import org.wicketstuff.objectautocomplete.ObjectAutoCompleteBuilder;
import org.wicketstuff.objectautocomplete.ObjectAutoCompleteField;
import org.wicketstuff.objectautocomplete.ObjectAutoCompleteRenderer;

import com.asptt.plongee.resa.model.Adherent;
import com.asptt.plongee.resa.ui.web.wicket.page.TemplatePage;
import com.asptt.plongee.resa.ui.web.wicket.page.inscription.InscriptionPlongeePage;

public class InscriptionExterieurPlongeePage extends TemplatePage {
	
	private ModalWindow modalExterieur;

	public InscriptionExterieurPlongeePage() {
		super();
		add(new ExterieurForm("form"));
		

		// Lien pour la création du plongeur extérieur
		modalExterieur = new ModalWindow("modalExterieur");
		modalExterieur.setTitle("This is modal window with panel content.");
		modalExterieur.setCookieName("modal-exterieur");
		add(modalExterieur);
		
		add(new IndicatingAjaxLink("creerExterieur") {
			@Override
			public void onClick(AjaxRequestTarget target) {
				replaceModalWindow(target);
				modalExterieur.show(target);

			}
		});

	}

	public List<Adherent> getMatchingAdherents(String search) {
		if (Strings.isEmpty(search)) {
			List<Adherent> emptyList = Collections.emptyList();
			return emptyList;
		}
		
		List<Adherent> list = getResaSession().getAdherentService()
				.rechercherExternes();
		
		ArrayList<Adherent> newList = new ArrayList<Adherent>();
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i).getNom().startsWith(search.toUpperCase())) {
				newList.add(list.get(i));
			}
		}
		return newList;
	}
	
	class ExterieurForm extends Form{
		
		ObjectAutoCompleteField<Adherent, String> autocompleteField ;

		public ExterieurForm(String id) {
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
					String texteAffiche = adherent.getNom() + " " + adherent.getPrenom() + " " + adherent.getNiveau();
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
					setResponsePage(new InscriptionPlongeePage(getResaSession().getAdherentService().rechercherAdherentParIdentifiant(autocompleteField.getConvertedInput())));
				}

			});
		}
		
	}
	
	private void replaceModalWindow(AjaxRequestTarget target) {
		modalExterieur.setContent(new ExterieurPanel(modalExterieur.getContentId()));
		modalExterieur.setTitle("Compl&eacute;tez les  informations concernant le plongeur ext&eacute;rieur");
		
		// La hauteur de la fenetre s'adapte à son contenu
		modalExterieur.setUseInitialHeight(false);
		
		// Pour éviter le message de disparition de la fenetre lors de la validation
		target.appendJavascript( "Wicket.Window.unloadConfirmation  = false;");
	}

}
