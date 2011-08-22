package com.asptt.plongee.resa.ui.web.wicket.page.admin;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.ResourceReference;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.extensions.markup.html.form.palette.Palette;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.util.CollectionModel;
import org.apache.wicket.model.util.ListModel;

import com.asptt.plongee.resa.exception.ResaException;
import com.asptt.plongee.resa.exception.TechnicalException;
import com.asptt.plongee.resa.model.Adherent;
import com.asptt.plongee.resa.model.Plongee;
import com.asptt.plongee.resa.ui.web.wicket.component.ConfirmAjaxLink;
import com.asptt.plongee.resa.ui.web.wicket.page.AccueilPage;
import com.asptt.plongee.resa.ui.web.wicket.page.TemplatePage;
import com.asptt.plongee.resa.ui.web.wicket.page.inscription.InscriptionConfirmationPlongeePage;

public class GererListeAttenteTwo extends TemplatePage {
	
	private FeedbackPanel feedback = new FeedbackPanel("feedback");

	public GererListeAttenteTwo(final Plongee plongee)
	{
		feedback.setOutputMarkupId(true);
		add(feedback);

		List<Adherent> persons = getResaSession().getPlongeeService().rechercherListeAttente(plongee);
		IChoiceRenderer<Adherent> renderer = new ChoiceRenderer<Adherent>("nomComplet", "nom");
		
		
		final Palette<Adherent> palette = new Palette<Adherent>("palette", 
				new ListModel<Adherent>(new ArrayList<Adherent>()), 
				new CollectionModel<Adherent>(persons), 
				renderer, 10, false){
			
			// Modification de la feuille de style
			// pour agrandir la largeur de la palette
			protected ResourceReference getCSS() {
			     return new ResourceReference(GererListeAttenteTwo.class, "PlongeePalette.css");
			    }
			
		};

		Form<?> form = new Form("form")
		{

			
		};

		AjaxButton valid = new AjaxButton("valid") {
			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				
				IModel modelAdherents  = palette.getDefaultModel();
				List<Adherent> adherents = (List<Adherent>) modelAdherents.getObject();
				for(Adherent adherent : adherents){
					getResaSession().getPlongeeService().fairePasserAttenteAInscrit(plongee, adherent);
				}
				setResponsePage(new InscriptionConfirmationPlongeePage(plongee));
			}
			
			// L'implémentation de cette méthode est nécessaire pour voir
			// les messages d'erreur dans le feedBackPanel
			protected void onError(AjaxRequestTarget target, Form<?> form) {
				target.addComponent(feedback);
			}
		};
		
		Link cancel = new Link("cancel") {
			@Override
			public void onClick() {
				setResponsePage(AccueilPage.class);
			}
		};
		
		AjaxButton supp = new AjaxButton("supprimer") {
			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				IModel modelAdherents  = palette.getDefaultModel();
				List<Adherent> adherents = (List<Adherent>) modelAdherents.getObject();
				for(Adherent adherent : adherents){
					try{
					getResaSession().getPlongeeService().supprimerDeLaListeDattente(plongee, adherent, 1);
					} catch (TechnicalException e) {
						e.printStackTrace();
						error(e.getKey());
					} catch (ResaException e) {
						e.printStackTrace();
						error(e.getKey());
					}  finally {
						target.addComponent(feedback);
					}
				}
				setResponsePage(new GererListeAttenteTwo(plongee));
			}
			protected void onError(AjaxRequestTarget target, Form<?> form) {
				target.addComponent(feedback);
			}
		};
		
		form.add(valid);
		form.add(cancel);
		form.add(supp);
		add(form);
		form.add(palette);
	}

}
