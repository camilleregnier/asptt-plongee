package com.asptt.plongee.resa.ui.web.wicket.page.admin;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.apache.wicket.PageParameters;
import org.apache.wicket.extensions.markup.html.form.palette.Palette;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.util.CollectionModel;
import org.apache.wicket.model.util.ListModel;

import com.asptt.plongee.resa.model.Adherent;
import com.asptt.plongee.resa.model.NiveauAutonomie;
import com.asptt.plongee.resa.model.Plongee;
import com.asptt.plongee.resa.ui.web.wicket.page.TemplatePage;

public class GererPlongeeAOuvrirTwo extends TemplatePage {
	
	public Plongee plongeeAOuvrir = null;
	
	public GererPlongeeAOuvrirTwo(final Plongee plongee)
	{		
		plongeeAOuvrir = plongee;
		
		List<Adherent> dps = getResaSession().getAdherentService().rechercherDPs( 
			getResaSession().getAdherentService().rechercherAdherentsActifs());
		IChoiceRenderer<Adherent> rendDp = new ChoiceRenderer<Adherent>("nom", "nom");

		final Palette<Adherent> palDp = new Palette<Adherent>("paletteDps", 
				new ListModel<Adherent>(new ArrayList<Adherent>()), 
				new CollectionModel<Adherent>(dps), 
				rendDp, 10, true);
		
		List<Adherent> pilotes = getResaSession().getAdherentService().rechercherPilotes( 
				getResaSession().getAdherentService().rechercherAdherentsActifs());
			IChoiceRenderer<Adherent> rendPilote = new ChoiceRenderer<Adherent>("nom", "nom");

			final Palette<Adherent> palPilote = new Palette<Adherent>("palettePilotes", 
					new ListModel<Adherent>(new ArrayList<Adherent>()), 
					new CollectionModel<Adherent>(pilotes), 
					rendPilote, 10, true);
			

		Form<?> form = new Form("form")
		{
			@Override
			protected void onSubmit()
			{
				
				IModel modelDps  = palDp.getDefaultModel();
				List<Adherent> dps = (List<Adherent>) modelDps.getObject();

				IModel modelPilotes  = palPilote.getDefaultModel();
				List<Adherent> pilotes = (List<Adherent>) modelPilotes.getObject();
				/*
				 * Impossible de gerer les doublons avec un HashSet
				 * Alors on le fait 'à la main' 
				 */
				List<String> idInscrits = new ArrayList<String>();
				for(Adherent adherent : dps){
					if(! idInscrits.contains(adherent.getNumeroLicense())){
						idInscrits.add(adherent.getNumeroLicense());
					}
				}
				for(Adherent adherent : pilotes){
					if(! idInscrits.contains(adherent.getNumeroLicense())){
						idInscrits.add(adherent.getNumeroLicense());
					}
				}
				/*
				 * Maintenant qu'on à la liste des id
				 * on reconstitu une liste d'adherent
				 */
				List<Adherent> adhInscrits = new ArrayList<Adherent>();
				for(String id : idInscrits){
//					info("selected Dp(s): " + palDp.getDefaultModelObjectAsString());
//					info("selected Pilote(s): " + palPilote.getDefaultModelObjectAsString());
//					info("selected inscrit : "+idInscrits);
					adhInscrits.add(getResaSession().getAdherentService().rechercherAdherentParIdentifiant(id));
				}
				/*
				 * Reste plus qu'a inscrire...
				 */
				for(Adherent adh : adhInscrits){
					getResaSession().getPlongeeService().inscrireAdherent(plongeeAOuvrir, adh);
				}
				PageParameters param = new PageParameters();
				param.put("plongeeAOuvrir", plongeeAOuvrir);
				param.put("inscrits", adhInscrits);
				setResponsePage(new GererPlongeeAOuvrirThree(plongeeAOuvrir));
			}
		};

		add(form);
		
		// Ajout de la liste des niveaux
		List<String> niveaux = new ArrayList<String>();
		for (NiveauAutonomie n : NiveauAutonomie.values()){
			niveaux.add(n.toString());
		}
		//add(new DropDownChoice("niveau", niveaux));
		form.add(palDp);
		form.add(palPilote);

		add(new FeedbackPanel("feedback"));
	}

}
