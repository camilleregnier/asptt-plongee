package com.asptt.plongee.resa.ui.web.wicket.page.admin;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.PageParameters;
import org.apache.wicket.ResourceReference;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxLink;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.extensions.markup.html.form.palette.Palette;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.form.ValidationErrorFeedback;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.util.CollectionModel;
import org.apache.wicket.model.util.ListModel;
import org.apache.wicket.validation.validator.MinimumValidator;


import com.asptt.plongee.resa.exception.ResaException;
import com.asptt.plongee.resa.exception.TechnicalException;
import com.asptt.plongee.resa.model.Adherent;
import com.asptt.plongee.resa.model.NiveauAutonomie;
import com.asptt.plongee.resa.model.Plongee;
import com.asptt.plongee.resa.ui.web.wicket.page.ErreurTechniquePage;
import com.asptt.plongee.resa.ui.web.wicket.page.ErrorPage;
import com.asptt.plongee.resa.ui.web.wicket.page.TemplatePage;
import com.asptt.plongee.resa.ui.web.wicket.page.inscription.InscriptionConfirmationPlongeePage;

public class GererPlongeeAOuvrirTwo extends TemplatePage {

	private ModalWindow modalPlongee;
	TextField<Integer> maxPlaces;
	TextField<Integer> niveauMinimum;

	public GererPlongeeAOuvrirTwo(final Plongee plongee) {

		modalPlongee = new ModalWindow("modalPlongee");
		modalPlongee.setTitle("This is modal window with panel content.");
		modalPlongee.setCookieName("modal-plongee");
		add(modalPlongee);

		CompoundPropertyModel<Plongee> modelPlongee = new CompoundPropertyModel<Plongee>(
				plongee);

		List<Adherent> dps;
			dps = getResaSession().getAdherentService()
					.rechercherDPs(
							getResaSession().getAdherentService()
									.rechercherAdherentsActifs());
		
			IChoiceRenderer<Adherent> rendDp = new ChoiceRenderer<Adherent>("nom",
				"nom");
		

			final Palette<Adherent> palDp = new Palette<Adherent>("paletteDps",
				new ListModel<Adherent>(new ArrayList<Adherent>()),
				new CollectionModel<Adherent>(dps), rendDp, 10, false){
				// Modification de la feuille de style
				// pour agrandir la largeur de la palette
				protected ResourceReference getCSS() {
					return new ResourceReference(GererPlongeeAOuvrirTwo.class, "PlongeePalette.css");
			    }
			};

			List<Adherent> pilotes = getResaSession().getAdherentService()
				.rechercherPilotes(getResaSession().getAdherentService().rechercherAdherentsActifs());
			
			IChoiceRenderer<Adherent> rendPilote = new ChoiceRenderer<Adherent>("nom", "nom");

			final Palette<Adherent> palPilote = new Palette<Adherent>(
				"palettePilotes", new ListModel<Adherent>(
				new ArrayList<Adherent>()),
				new CollectionModel<Adherent>(pilotes), rendPilote, 10, false){
					// Modification de la feuille de style
					// pour agrandir la largeur de la palette
					protected ResourceReference getCSS() {
						return new ResourceReference(GererPlongeeAOuvrirTwo.class, "PlongeePalette.css");
					}
			};
			
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
						try {
							getResaSession().getPlongeeService().inscrireAdherent(
									plongee, adh, -1);
						} catch (ResaException e) {
							e.printStackTrace();
							ErrorPage ep = new ErrorPage(e);
							setResponsePage(ep);
						}
					}
					PageParameters param = new PageParameters();
					param.put("plongeeAOuvrir", plongee);
					param.put("inscrits", adhInscrits);
					//setResponsePage(new GererPlongeeAOuvrirThree(plongee));
					setResponsePage(new InscriptionConfirmationPlongeePage(plongee));
				}//fin du onSubmit()
			};

			form.setModel(modelPlongee);
			// Le nombre max. de places, pour info
			maxPlaces = new TextField<Integer>("nbMaxPlaces");
			maxPlaces.setOutputMarkupId(true);
			form.add(maxPlaces.setEnabled(false));
			// Le niveau mini. des plongeurs, pour info
			niveauMinimum = new TextField<Integer>("niveauMinimum");
			niveauMinimum.setOutputMarkupId(true);
			form.add(niveauMinimum.setEnabled(false));
			
			// Ajout des palettes
			form.add(palDp);
	        form.add(palPilote);
	        
			add(form);
	
			form.add(new IndicatingAjaxLink("change") {
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
	
	private void onSubmitPlongeePanel(AjaxRequestTarget target){
		target.addComponent(maxPlaces);
		target.addComponent(niveauMinimum);
		modalPlongee.close(target);
	}

	public  class PlongeePanel extends Panel {

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
			formPlongee.add(new RequiredTextField<Integer>("nbMaxPlaces", Integer.class).add(new MinimumValidator<Integer>(4)));

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

					// Mise à jour de la plongee (nombre de places max et niveau mini)
					getResaSession().getPlongeeService().modifierPlongee(plongee);
					onSubmitPlongeePanel(target);

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
