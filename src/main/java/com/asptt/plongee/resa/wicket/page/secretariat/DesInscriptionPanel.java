package com.asptt.plongee.resa.wicket.page.secretariat;

import java.util.Calendar;
import java.util.Locale;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.behavior.IBehavior;
import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxLink;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.CompoundPropertyModel;

import com.asptt.plongee.resa.model.Adherent;
import com.asptt.plongee.resa.model.Plongee;
import com.asptt.plongee.resa.model.PlongeesAdherentDataProvider;
import com.asptt.plongee.resa.wicket.ResaSession;
import com.asptt.plongee.resa.util.ResaUtil;

public abstract class DesInscriptionPanel extends Panel {

	private static final long serialVersionUID = -1601864562144567667L;
	final ModalWindow modalConfirm;

	public DesInscriptionPanel(String id, final Adherent plongeur) {
		super(id, new CompoundPropertyModel<Adherent>(plongeur));
		setOutputMarkupId(true);

		final ResaSession resaSession = (ResaSession) getApplication()
				.getSessionStore().lookup(getRequest());

		// Initialisation de la fenêtre modal de confirmation d'annulation
		add(modalConfirm = new ModalWindow("modalConfirm"));
		//modalConfirm.setPageMapName("modal-2");

		modalConfirm.setTitle("Confirmation");

		modalConfirm.setResizable(false);
		modalConfirm.setInitialWidth(30);
		modalConfirm.setInitialHeight(15);
		modalConfirm.setWidthUnit("em");
		modalConfirm.setHeightUnit("em");

		modalConfirm.setCssClassName(ModalWindow.CSS_CLASS_BLUE);
		

		// La liste des plongées auxquelle le plongeur est inscrit
		add(new DataView<Plongee>("simple", new PlongeesAdherentDataProvider(
				plongeur, resaSession.getPlongeeService())) {

			private static final long serialVersionUID = 4491729538132325557L;

			protected void populateItem(final Item<Plongee> item) {
				final Plongee plongee = item.getModelObject();
				String nomDP = "Aucun";
				if (null != plongee.getDp()) {
					nomDP = plongee.getDp().getNom();
				}

				item.add(new IndicatingAjaxLink("select") {
					@Override
					public void onClick(final AjaxRequestTarget target) {						
						
						modalConfirm.setPageCreator(new ModalWindow.PageCreator()
						{
							public Page createPage()
							{
								return new ConfirmSelectionModal(modalConfirm, plongee, plongeur);
							}
						});
						modalConfirm.show(target);
					}

				});

				// Mise en forme de la date
				Calendar cal = Calendar.getInstance();
				cal.setTime(plongee.getDate());
				String dateAffichee = cal.getDisplayName(Calendar.DAY_OF_WEEK,
						Calendar.LONG, Locale.FRANCE)
						+ " ";
				dateAffichee = dateAffichee + cal.get(Calendar.DAY_OF_MONTH)
						+ " ";
				dateAffichee = dateAffichee
						+ cal.getDisplayName(Calendar.MONTH, Calendar.LONG,
								Locale.FRANCE) + " ";
				dateAffichee = dateAffichee + cal.get(Calendar.YEAR);

				item.add(new Label("date", dateAffichee));
				item.add(new Label("dp", nomDP));
				item.add(new Label("type", plongee.getType()));
				item.add(new Label("niveauMini", plongee.getNiveauMinimum()
						.toString()));

				// Places restantes
				item.add(new Label("placesRestantes", resaSession
						.getPlongeeService().getNbPlaceRestante(plongee)
						.toString()));

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

	public abstract void onSave(AjaxRequestTarget target, Plongee plongee, Adherent plongeur);

	public class ConfirmSelectionModal extends WebPage
	{
		public ConfirmSelectionModal(final ModalWindow window, final Plongee plongee,
				final Adherent plongeur)
		{
			// Informations précisant la plongeur concerné et la plongée
			// dans la fenêtre de confirmation de désinscription
			String infoPlongeur = "Etes-vous s\u00fbr de vouloir annuler l'inscription de " + plongeur.getPrenom() + " " + plongeur.getNom();
			String infoPlongee =  "\u00e0 la plong\u00e9e du " + ResaUtil.getDateString(plongee.getDate()) + " " + plongee.getType() + " ?";
			String infoEncadrant = "";
			
			if(plongeur.isEncadrent()){
				ResaSession resaSession = (ResaSession) getApplication()
				.getSessionStore().lookup(getRequest());
				
				//C'est un encadrant : on regarde s'il en reste assez
				if( ! resaSession.getPlongeeService().isEnoughEncadrant(plongee)){
					infoEncadrant = infoEncadrant + "Il ne reste plus assez d'encadrant !!";
				}
			}
			add(new Label("infoPlongeur", infoPlongeur));
			add(new Label("infoPlongee", infoPlongee));
			add(new Label("infoEncadrant", infoEncadrant));
			
			// Le lien qui va fermer la fenêtre de confirmation
			// et appeler la méthode de désinscription de la page principale (DesInscriptionPlongeePage)
			add(new IndicatingAjaxLink("yes")
			{
			
				private static final long serialVersionUID = -8801329059174173909L;	

				@Override
				public void onClick(AjaxRequestTarget target)
				{
					onSave(target, plongee, plongeur);
					modalConfirm.close(target);
					
				}
			});

			add(new IndicatingAjaxLink("no")
			{
				private static final long serialVersionUID = 7770018649246290638L;

				@Override
				public void onClick(AjaxRequestTarget target)
				{
					modalConfirm.close(target);
				}
			});
		}

	}

}
