package com.asptt.plongee.resa.ui.web.wicket.page.secretariat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
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

import com.asptt.plongee.resa.exception.ResaException;
import com.asptt.plongee.resa.exception.TechnicalException;
import com.asptt.plongee.resa.mail.PlongeeMail;
import com.asptt.plongee.resa.model.Adherent;
import com.asptt.plongee.resa.model.Plongee;
import com.asptt.plongee.resa.ui.web.wicket.page.TemplatePage;
import com.asptt.plongee.resa.ui.web.wicket.page.inscription.InscriptionConfirmationPlongeePage;

public class DesInscriptionPlongeePage extends TemplatePage {

	// Pour la confirmation
	private ModalWindow modalPlongees;
	
	private final FeedbackPanel feedback;

	public DesInscriptionPlongeePage() {
		super();
		
		feedback = new FeedbackPanel("feedback");
		feedback.setOutputMarkupId(true);
		add(feedback);
		
		add(new PlongeurADesinscrireForm("formPlongeurADesinscrire"));

		// Fenêtre modale de consultation des plongées pour lesquelles
		// le plongeur est inscrit
		modalPlongees = new ModalWindow("modalPlongees");
		modalPlongees.setTitle("Liste des plongées pour ce plongeur");
		modalPlongees.setUseInitialHeight(false);
		modalPlongees.setInitialWidth(750);
		modalPlongees.setWidthUnit("px");
		add(modalPlongees);

	}

	public List<Adherent> getMatchingAdherents(String search) {
		if (Strings.isEmpty(search)) {
			List<Adherent> emptyList = Collections.emptyList();
			return emptyList;
		}

		// Dans le cas de la desinscription, la list de recherche
		// est composée des adhérents actifs et des externes (à priori ils
		// existent dans ce cas)
		List<Adherent> list = getResaSession().getAdherentService()
				.rechercherAdherentsActifs();
		list.addAll(getResaSession().getAdherentService().rechercherExternes());

		ArrayList<Adherent> newList = new ArrayList<Adherent>();
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i).getNom().startsWith(search.toUpperCase())) {
				newList.add(list.get(i));
			}
		}
		return newList;
	}

	class PlongeurADesinscrireForm extends Form {

		ObjectAutoCompleteField<Adherent, String> autocompleteField;

		public PlongeurADesinscrireForm(String id) {
			super(id);

			AutoCompletionChoicesProvider<Adherent> provider = new AutoCompletionChoicesProvider<Adherent>() {
				private static final long serialVersionUID = 1L;

				public Iterator<Adherent> getChoices(String input) {
					return getMatchingAdherents(input).iterator();
				}
			};

			ObjectAutoCompleteRenderer<Adherent> renderer = new ObjectAutoCompleteRenderer<Adherent>() {
				private static final long serialVersionUID = 1L;

				protected String getIdValue(Adherent adherent) {
					return adherent.getNumeroLicense();
				}

				protected String getTextValue(Adherent adherent) {
					String texteAffiche = adherent.getNom()
							+ " "
							+ adherent.getPrenom()
							+ " "
							+ ((adherent.getEncadrement() != null) ? adherent
									.getEncadrement() : adherent.getNiveau());
					// Pour les externes, le niveau est suffixé par (Ext.)
					if (adherent.getActifInt() == 2) {
						texteAffiche = texteAffiche + " (Ext.)";
					}
					return texteAffiche;
				}
			};

			ObjectAutoCompleteBuilder<Adherent, String> builder = new ObjectAutoCompleteBuilder<Adherent, String>(
					provider);
			builder.autoCompleteRenderer(renderer);
			builder.searchLinkText("Autre recherche");
			builder.width(200);

			autocompleteField = builder.build("numeroLicense",
					new Model<String>());
			final TextField<String> adherent = autocompleteField
					.getSearchTextField();
			adherent.setRequired(true);

			add(autocompleteField);

			add(new IndicatingAjaxSubmitButton("select", this) {

				@Override
				protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
					replaceModalWindow(target, getResaSession()
							.getAdherentService()
							.rechercherAdherentParIdentifiant(
									autocompleteField.getConvertedInput()));
					modalPlongees.show(target);
				}

			});
		}
	}
	
	public void deInscrire(AjaxRequestTarget target, Plongee plongee, Adherent plongeur) {
		
		try {
			
			//SI c'est un encadrant il faut verifier s'il en reste assez
			//et sinon envoyer un mail 
			if(plongeur.getEncadrement() == null){	
				//Ce n'est pas un encadrant : on desinscrit
				
				//S'il y a des personnes en liste d'attente => mail aux ADMIN
				if(getResaSession().getPlongeeService().rechercherListeAttente(plongee).size() > 0){
					getResaSession().getPlongeeService().deInscrireAdherent(
							plongee, 
							plongeur, PlongeeMail.MAIL_PLACES_LIBRES);
				 
				} else {
					getResaSession().getPlongeeService().deInscrireAdherent(
							plongee, 
							plongeur, -1);
				}
			} else {
					//C'est un encadrant
					//S'il y a des personnes en liste d'attente => mail
					if(getResaSession().getPlongeeService().rechercherListeAttente(plongee).size() > 0){
						getResaSession().getPlongeeService().deInscrireAdherent(
								plongee, 
								plongeur, PlongeeMail.MAIL_PLACES_LIBRES);
						
					} else {
						getResaSession().getPlongeeService().deInscrireAdherent(
								plongee, 
								plongeur, -1);
					}
			}
			modalPlongees.close(target);
			//setResponsePage(new InscriptionConfirmationPlongeePage(plongee));
		} catch (TechnicalException e) {
			e.printStackTrace();
			error(e.getKey());
		} catch (ResaException e) {
			e.printStackTrace();
			error(e.getKey());
		} finally {
			target.addComponent(feedback);
		}
	}

	private void replaceModalWindow(AjaxRequestTarget target, Adherent plongeur) {
		// Pour éviter le message de disparition de la fenetre lors de la validation
		target.appendJavascript( "Wicket.Window.unloadConfirmation  = false;");
		
		
		modalPlongees.setContent(new DesInscriptionPanel(modalPlongees
				.getContentId(), plongeur) {

			private static final long serialVersionUID = -2457749183505257418L;

			@Override
			public void onSave(AjaxRequestTarget target, Plongee plongee,
					Adherent plongeur) {

					deInscrire(target, plongee, plongeur);
					//modalPlongees.close(target);

			}

		});
	}
}
