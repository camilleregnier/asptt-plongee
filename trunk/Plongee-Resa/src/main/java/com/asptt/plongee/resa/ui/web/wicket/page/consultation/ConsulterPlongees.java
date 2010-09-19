package com.asptt.plongee.resa.ui.web.wicket.page.consultation;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxLink;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.version.undo.Change;

import com.asptt.plongee.resa.exception.ResaException;
import com.asptt.plongee.resa.exception.TechnicalException;
import com.asptt.plongee.resa.model.Adherent;
import com.asptt.plongee.resa.model.InscritsPlongeeDataProvider;
import com.asptt.plongee.resa.model.ListeAttentePlongeeDataProvider;
import com.asptt.plongee.resa.model.Plongee;
import com.asptt.plongee.resa.model.PlongeeDataProvider;
import com.asptt.plongee.resa.ui.web.wicket.page.ErreurTechniquePage;
import com.asptt.plongee.resa.ui.web.wicket.page.ErrorPage;
import com.asptt.plongee.resa.ui.web.wicket.page.TemplatePage;

@AuthorizeInstantiation({"USER", "SECRETARIAT"})
public class ConsulterPlongees extends TemplatePage {
	private Plongee selected;
	private ModalWindow modal2;

	@SuppressWarnings("serial")
	public ConsulterPlongees() {
		
		modal2 = new ModalWindow("modal2");
		modal2.setTitle("This is modal window with panel content.");
		modal2.setCookieName("modal-adherent");
		add(modal2);

		try {
			List<Plongee> plongees = getResaSession().getPlongeeService().rechercherPlongeeProchainJour(getResaSession().getAdherent());
			
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
							replaceModalWindow(target, item.getModel());
							modal2.show(target);
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
		}	catch (TechnicalException e) {
			e.printStackTrace();
			ErreurTechniquePage etp = new ErreurTechniquePage(e);
			setResponsePage(etp);
		}

	}

	public String getSelectedPlongeeLabel() {
		if (selected == null) {
			return "Pas de plongee selectionnee";
		} else {
			return String.valueOf(selected.getId());
		}
	}

	public Plongee getSelected() {
		if (null == selected) {
			selected = new Plongee();
			selected.setId(-1);
		}
		return selected;
	}

	/**
	 * sets selected contact
	 * 
	 * @param selected
	 */

	public void setSelected(Plongee selected) {
		addStateChange(new Change() {
			private static final long serialVersionUID = -1384730190380850382L;
			private final Plongee old = ConsulterPlongees.this.selected;

			@Override
			public void undo() {
				ConsulterPlongees.this.selected = old;

			}
		});
		this.selected = selected;
	}
	
	private void replaceModalWindow(AjaxRequestTarget target, IModel<Plongee> plongee) {
		modal2.setContent(new ParticipantsPanel(modal2.getContentId(), plongee));
		modal2.setTitle("Liste des participants");
	}

	class ParticipantsPanel extends Panel {

		private static final long serialVersionUID = 6206469268417992518L;

		@SuppressWarnings("serial")
		public ParticipantsPanel(String id, IModel<Plongee> plongee) {
			super(id, plongee);
			setOutputMarkupId(true);
			
			try {
			
				List<Adherent> adherentsInscrit = getResaSession().getPlongeeService().rechercherInscriptions(plongee.getObject(),null,null);
	
				add(new DataView<Adherent>("participants",
						new InscritsPlongeeDataProvider(adherentsInscrit)) {
					protected void populateItem(final Item<Adherent> item) {
						Adherent adherent = item.getModelObject();
	
						item.add(new Label("nom", adherent.getNom()));
						item.add(new Label("prenom", adherent.getPrenom()));
						
						// Dès que le plongeur est encadrant, on affiche son niveau d'encadrement
						String niveauAffiche;
						if (adherent.getEncadrement() != null)
							niveauAffiche = adherent.getEncadrement();
						else niveauAffiche = adherent.getNiveau();
						
						// Pour les externes, le niveau est suffixé par (Ext.)
						if (adherent.getActifInt() ==2){
							niveauAffiche = niveauAffiche + " (Ext.)";
						}
							
						item.add(new Label("niveau", niveauAffiche));
	
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
			
				List<Adherent> adhereAttente = getResaSession().getPlongeeService().rechercherListeAttente(plongee.getObject());
				
				DataView<Adherent> dataView = new DataView<Adherent>("listeAttente",
						new ListeAttentePlongeeDataProvider(adhereAttente)) {
							protected void populateItem(final Item<Adherent> item) {
								Adherent adherent = item.getModelObject();
	
								item.add(new Label("nom", adherent.getNom()));
								item.add(new Label("prenom", adherent.getPrenom()));
								item.add(new Label("niveau", adherent.getNiveau()));
	
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
				add(dataView);
			} catch (TechnicalException e) {
				e.printStackTrace();
				ErreurTechniquePage etp = new ErreurTechniquePage(e);
				setResponsePage(etp);
			}
			
		}
	}
}
