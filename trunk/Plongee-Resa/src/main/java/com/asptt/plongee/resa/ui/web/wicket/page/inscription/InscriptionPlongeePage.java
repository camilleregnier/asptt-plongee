package com.asptt.plongee.resa.ui.web.wicket.page.inscription;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxLink;
import org.apache.wicket.feedback.IFeedback;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;

import com.asptt.plongee.resa.exception.ResaException;
import com.asptt.plongee.resa.exception.TechnicalException;
import com.asptt.plongee.resa.model.Adherent;
import com.asptt.plongee.resa.model.Plongee;
import com.asptt.plongee.resa.model.PlongeeDataProvider;
import com.asptt.plongee.resa.ui.web.wicket.page.ErreurTechniquePage;
import com.asptt.plongee.resa.ui.web.wicket.page.ErrorPage;
import com.asptt.plongee.resa.ui.web.wicket.page.TemplatePage;
import com.asptt.plongee.resa.ui.web.wicket.page.admin.GererPlongeeAOuvrirTwo;

public class InscriptionPlongeePage extends TemplatePage {
	
	private Adherent adh = null;
	private List<Plongee> plongees = null;
	
	public InscriptionPlongeePage(){
		
		this.adh = getResaSession().getAdherent(); 
		
		add(new Label("message", adh.getPrenom() + ", ci-dessous, les plongées auxquelles tu peux t'inscrire"));
		
		final FeedbackPanel feedback = new FeedbackPanel("feedback");
		add(feedback);
		
		init();
		
	}
	
	public InscriptionPlongeePage(Adherent adherent) {
		
		this.adh = adherent;
		
		add(new Label("message", adh.getPrenom() + " " + adh.getNom() + " peut s'inscrire aux plongées suivantes"));
		
		final FeedbackPanel feedback = new FeedbackPanel("feedback");
		add(feedback);
		
		init();
	}
	

	
	private void init() {

		try{
			plongees = getResaSession().getPlongeeService().rechercherPlongeePourInscriptionAdherent(adh);
		} catch (TechnicalException e) {
			e.printStackTrace();
			ErreurTechniquePage etp = new ErreurTechniquePage(e);
			setResponsePage(etp);
		}
		
		PlongeeDataProvider pDataProvider = new PlongeeDataProvider(plongees);

		add(new DataView<Plongee>("simple", pDataProvider) {
			protected void populateItem(final Item<Plongee> item) {
				Plongee plongee = item.getModelObject();
				String nomDP = "Aucun";
				if (null != plongee.getDp()) {
					nomDP = plongee.getDp().getNom();
				}

				item.add(new IndicatingAjaxLink("select") {
					@Override
					public void onClick(AjaxRequestTarget target) {
//						replaceModalWindow(target, item.getModel());
//						modal2.show(target);
						inscrire(target, item.getModel());
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
				
				// Places restantes
				item.add(new Label("placesRestantes", getResaSession().getPlongeeService().getNbPlaceRestante(plongee).toString()));

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

	public void inscrire(AjaxRequestTarget target, IModel<Plongee> iModel) {
		
		Plongee plongee = iModel.getObject();
		// Si l'adhérent est inscrit par le secrétariat
		// on ne prend pas l'adhérent en session
		try {
			int response = getResaSession().getPlongeeService().isOkForResa(
					plongee, 
					adh != null ? adh : getResaSession().getAdherent());
			
			switch (response) {
			case 1: //on peux inscrire l'adherent à la plongee
				try {
					getResaSession().getPlongeeService().inscrireAdherent(
							plongee, 
							adh != null ?  adh : getResaSession().getAdherent());
					setResponsePage(new InscriptionConfirmationPlongeePage(plongee));
					break;
				} catch (ResaException re) {
					// ça passe directement à l'inscription en liste d'attente (ci-dessous) car pas de break
				}
			case 0: //on inscrit l'adherent en liste d'attente
				if(getResaSession().getPlongeeService().isOkForListeAttente(
						plongee, 
						getResaSession().getAdherent())){
					//on peut inscrire l'adherent en liste attente
					getResaSession().getPlongeeService().inscrireAdherentEnListeAttente(
							plongee, 
							adh != null ?  adh : getResaSession().getAdherent());
					setResponsePage(new InscriptionListeAttentePlongeePage(plongee));
				}
				break;
			case 2: // ouvrir la plongée
				setResponsePage(new GererPlongeeAOuvrirTwo(plongee));
				break;
//					case -1: 
//						/*
//						 * Inscription impossible
//						 */
//						setResponsePage(new InscriptionFailurePlongeePage(plongee));
//						break;
//					default:
//						/*
//						 * Inscription impossible
//						 */
//						setResponsePage(new InscriptionFailurePlongeePage(plongee));
//						break;
			}
		} catch (ResaException e) {
			e.printStackTrace();
			ErrorPage ep = new ErrorPage(e);
			setResponsePage(ep);
		} catch (TechnicalException e) {
			e.printStackTrace();
			ErreurTechniquePage etp = new ErreurTechniquePage(e);
			setResponsePage(etp);
		}
	}
		
}
