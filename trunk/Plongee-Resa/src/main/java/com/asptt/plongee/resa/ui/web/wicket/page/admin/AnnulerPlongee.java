package com.asptt.plongee.resa.ui.web.wicket.page.admin;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxLink;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;

import com.asptt.plongee.resa.exception.ResaException;
import com.asptt.plongee.resa.exception.TechnicalException;
import com.asptt.plongee.resa.model.Adherent;
import com.asptt.plongee.resa.model.Plongee;
import com.asptt.plongee.resa.model.PlongeeDataProvider;
import com.asptt.plongee.resa.ui.web.wicket.page.AccueilPage;
import com.asptt.plongee.resa.ui.web.wicket.page.ErreurTechniquePage;
import com.asptt.plongee.resa.ui.web.wicket.page.ErrorPage;
import com.asptt.plongee.resa.ui.web.wicket.page.TemplatePage;

public class AnnulerPlongee extends TemplatePage {
	
	private ModalWindow modalPlongee;
	Label infoLabel;
	DataView<Plongee> dataPlongees;

	public AnnulerPlongee() {
		
		// Fenêtre modale d'informations sur la plongée à annuler
		modalPlongee = new ModalWindow("modalPlongee");
		modalPlongee.setTitle("Informations sur la plongée");
		modalPlongee.setUseInitialHeight(false);
		modalPlongee.setWidthUnit("px");
		add(modalPlongee);
		
		infoLabel = new Label("infoLabel", "Choisissez la plongée à annuler");
		add(infoLabel);
		
		// TODO, voir si on ajoute pas cela dans un panel
		// pour une mise à jour dynamique lors de l'annulation de la plongée
		try {
			List<Plongee> plongees = getResaSession().getPlongeeService().rechercherPlongeeProchainJour(getResaSession().getAdherent());
			
			PlongeeDataProvider pDataProvider = new PlongeeDataProvider(plongees);

			dataPlongees = new DataView<Plongee>("simple", pDataProvider) {
				protected void populateItem(final Item<Plongee> item) {
					Plongee plongee = item.getModelObject();
					String nomDP = "Aucun";
					if (null != plongee.getDp()) {
						nomDP = plongee.getDp().getNom();
					}

					item.add(new IndicatingAjaxLink("annuler") {
						@Override
						public void onClick(AjaxRequestTarget target) {
							replaceModalWindow(target, item.getModel());
							modalPlongee.show(target);
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
			};
			dataPlongees.setOutputMarkupId(true);
			add(dataPlongees);
		} catch (TechnicalException e) {
			e.printStackTrace();
			ErreurTechniquePage etp = new ErreurTechniquePage(e);
			setResponsePage(etp);
		}
	}
	
	private void replaceModalWindow(AjaxRequestTarget target, IModel<Plongee> plongee) {
		modalPlongee.setContent(new PlongeePanel(modalPlongee.getContentId(), plongee));
		
		// Pour éviter le message de disparition de la fenetre lors de la validation
		target.appendJavascript( "Wicket.Window.unloadConfirmation  = false;");
	}
	
	private void onClickPlongeePanel(AjaxRequestTarget target, Plongee plongee){

		modalPlongee.close(target);
		
		// appel du service d'annulation de la plongée;
		getResaSession().getPlongeeService().supprimerPlongee(plongee);
		
		setResponsePage(AccueilPage.class);

	}
	
	private void onClickNo(AjaxRequestTarget target){

		modalPlongee.close(target);

	}
	
	public  class PlongeePanel extends Panel {

		private static final long serialVersionUID = 8737443673255555616L;

		public PlongeePanel(String id, IModel<Plongee> plongeeModel) {
			super(id, plongeeModel);
			setOutputMarkupId(true);
			setDefaultModel(plongeeModel);
			
			final Plongee plongee = plongeeModel.getObject();
			
			// Informations précisant la plongeur concerné et la plongée
			// dans la fenêtre de confirmation de désinscription
			add(new Label("infoPlongee", "Etes-vous sûr de vouloir annuler la plongée du  " + plongee.getDate() + " " + plongee.getType() + " ?"));
			int nbPlongeursInscrits = plongee.getParticipants().size();
			add(new Label("infoPlongeurs", (nbPlongeursInscrits == 0) ? " " : " Il y a " + nbPlongeursInscrits + " plongeur(s) inscrits."));
			
			// Le lien qui va fermer la fenêtre de confirmation
			// et appeler la méthode de désinscription de la page principale (DesInscriptionPlongeePage)
			add(new IndicatingAjaxLink("yes")
			{
				private static final long serialVersionUID = 1L;

				@Override
				public void onClick(AjaxRequestTarget target)
				{
					onClickPlongeePanel(target, plongee);
				}
			});
			
			add(new IndicatingAjaxLink("no")
			{
				private static final long serialVersionUID = 1L;

				@Override
				public void onClick(AjaxRequestTarget target)
				{
					onClickNo(target);
				}
			});
		}
		
	}
}

