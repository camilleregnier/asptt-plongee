package com.asptt.plongee.resa.ui.web.wicket.page.admin;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.PageParameters;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.extensions.markup.html.form.palette.Palette;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.util.CollectionModel;
import org.apache.wicket.model.util.ListModel;

import com.asptt.plongee.resa.model.Adherent;
import com.asptt.plongee.resa.model.NiveauAutonomie;
import com.asptt.plongee.resa.model.Plongee;
import com.asptt.plongee.resa.ui.web.wicket.page.TemplatePage;

public class GererPlongeeAOuvrirTwo extends TemplatePage {

	private ModalWindow modalPlongee;

	public GererPlongeeAOuvrirTwo(final Plongee plongee) {

		modalPlongee = new ModalWindow("modalPlongee");
		modalPlongee.setTitle("This is modal window with panel content.");
		modalPlongee.setCookieName("modal-plongee");
		add(modalPlongee);

		CompoundPropertyModel<Plongee> modelPlongee = new CompoundPropertyModel<Plongee>(
				plongee);

		List<Adherent> dps = getResaSession().getAdherentService()
				.rechercherDPs(
						getResaSession().getAdherentService()
								.rechercherAdherentsActifs());
		IChoiceRenderer<Adherent> rendDp = new ChoiceRenderer<Adherent>("nom",
				"nom");
		

		final Palette<Adherent> palDp = new Palette<Adherent>("paletteDps",
				new ListModel<Adherent>(new ArrayList<Adherent>()),
				new CollectionModel<Adherent>(dps), rendDp, 10, true);

		List<Adherent> pilotes = getResaSession().getAdherentService()
				.rechercherPilotes(
						getResaSession().getAdherentService()
								.rechercherAdherentsActifs());
		IChoiceRenderer<Adherent> rendPilote = new ChoiceRenderer<Adherent>(
				"nom", "nom");

		final Palette<Adherent> palPilote = new Palette<Adherent>(
				"palettePilotes", new ListModel<Adherent>(
						new ArrayList<Adherent>()),
				new CollectionModel<Adherent>(pilotes), rendPilote, 10, true);

		final Form<Plongee> form = new Form<Plongee>("form") {

			private static final long serialVersionUID = 4611593854191923422L;

			@Override
			protected void onSubmit() {

				IModel<?> modelDps = palDp.getDefaultModel();
				List<Adherent> dps = (List<Adherent>) modelDps.getObject();

				IModel<?> modelPilotes = palPilote.getDefaultModel();
				List<Adherent> pilotes = (List<Adherent>) modelPilotes
						.getObject();
				/*
				 * Impossible de gerer les doublons avec un HashSet Alors on le
				 * fait 'à la main'
				 */
				List<String> idInscrits = new ArrayList<String>();
				for (Adherent adherent : dps) {
					if (!idInscrits.contains(adherent.getNumeroLicense())) {
						idInscrits.add(adherent.getNumeroLicense());
					}
				}
				for (Adherent adherent : pilotes) {
					if (!idInscrits.contains(adherent.getNumeroLicense())) {
						idInscrits.add(adherent.getNumeroLicense());
					}
				}
				/*
				 * Maintenant qu'on à la liste des id on reconstitue une liste
				 * d'adherent
				 */
				List<Adherent> adhInscrits = new ArrayList<Adherent>();
				for (String id : idInscrits) {
					adhInscrits.add(getResaSession().getAdherentService()
							.rechercherAdherentParIdentifiant(id));
				}
				/*
				 * Reste plus qu'a inscrire...
				 */
				for (Adherent adh : adhInscrits) {
					getResaSession().getPlongeeService().inscrireAdherent(
							plongee, adh);
				}
				PageParameters param = new PageParameters();
				param.put("plongeeAOuvrir", plongee);
				param.put("inscrits", adhInscrits);
				setResponsePage(new GererPlongeeAOuvrirThree(plongee));
			}
		};

		form.setModel(modelPlongee);
		// Le nombre max. de places, pour info
		form.add(new TextField<Integer>("maxPlaces").setEnabled(false));
		// Le niveau mini. des plongeurs, pour info
		form.add(new TextField<Integer>("niveauMinimum").setEnabled(false));
		
		// Ajout des palettes
		form.add(palDp);
        form.add(palPilote);
        
		add(form);

		form.add(new AjaxLink("change") {
			@Override
			public void onClick(AjaxRequestTarget target) {
				replaceModalWindow(target, form.getModel());
				modalPlongee.show(target);

			}
		});

	}

	private void replaceModalWindow(AjaxRequestTarget target,
			IModel<Plongee> plongee) {
		modalPlongee.setContent(new PlongeePanel(modalPlongee.getContentId(),
				plongee));
		modalPlongee.setTitle("Caractéristiques de la plongée");
		
		// La hauteur de la fenetre s'adapte à son contenu
		modalPlongee.setUseInitialHeight(false);

		// Pour éviter le message de disparition de la fenetre lors de la
		// validation
		target.appendJavascript("Wicket.Window.unloadConfirmation  = false;");
	}

	class PlongeePanel extends Panel {

		private static final long serialVersionUID = -5814508281132946597L;

		public PlongeePanel(String id, IModel<Plongee> plongee) {
			super(id, plongee);
			setOutputMarkupId(true);
			setDefaultModel(plongee);
			
			// feedback panel pour renvoyer des messages sur la page
			final FeedbackPanel feedback = new FeedbackPanel("feedback");
			feedback.setOutputMarkupId(true);

			Form formPlongee = new Form("formPlongee");
			formPlongee.setModel(plongee);
			
			// Ajout du feedBack au formulaire
			formPlongee.add(feedback);
			
			// Nombre de places max
			formPlongee.add(new RequiredTextField<Integer>("maxPlaces",
					Integer.class));

			// Ajout de la liste des niveaux
			List<String> niveaux = new ArrayList<String>();
			for (NiveauAutonomie n : NiveauAutonomie.values()) {
				niveaux.add(n.toString());
			}
			formPlongee.add(new DropDownChoice("niveauMinimum", niveaux));
			
			formPlongee.add(new AjaxButton("validPlongee") {
				@Override
				// La validation doit se faire en Ajax car le formulaire de la
				// fenêtre principal n'y a pas accés
				// http://yeswicket.com/index.php?post/2010/04/26/G%C3%A9rer-facilement-les-fen%C3%AAtres-modales-avec-Wicket
				protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
					Plongee plongee = (Plongee) form.getModelObject();

					// TODO update plongee
					getResaSession().getPlongeeService().modifierPlongee(plongee);

				}
				
				// L'implémentation de cette méthode est nécessaire pour voir
				// les messages d'erreur dans le feedBackPanel
				protected void onError(AjaxRequestTarget target, Form<?> form) {
					target.addComponent(feedback);
				}

			});


			// Ajout du formulaire à la page
			add(formPlongee);

		}

	}

}
