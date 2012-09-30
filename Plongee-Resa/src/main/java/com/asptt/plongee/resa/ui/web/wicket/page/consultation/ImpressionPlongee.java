package com.asptt.plongee.resa.ui.web.wicket.page.consultation;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Session;
import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.AbstractReadOnlyModel;

import com.asptt.plongee.resa.exception.TechnicalException;
import com.asptt.plongee.resa.model.Adherent;
import com.asptt.plongee.resa.model.InscritsPlongeeDataProvider;
import com.asptt.plongee.resa.model.ListeAttentePlongeeDataProvider;
import com.asptt.plongee.resa.model.Plongee;
import com.asptt.plongee.resa.ui.web.wicket.ResaSession;
import com.asptt.plongee.resa.ui.web.wicket.page.ErreurTechniquePage;
import com.asptt.plongee.resa.ui.web.wicket.page.TemplatePage;

@AuthorizeInstantiation( { "USER", "SECRETARIAT" })
public class ImpressionPlongee extends WebPage {
	

	@SuppressWarnings("serial")
	public ImpressionPlongee(final Plongee plongee, final ResaSession session) {
		
		// Mise en forme de la date
		Calendar cal = Calendar.getInstance();
		cal.setTime(plongee.getDate());
		String dateAffichee = cal.getDisplayName(Calendar.DAY_OF_WEEK,
				Calendar.LONG, Locale.FRANCE)
				+ " ";
		dateAffichee = dateAffichee + cal.get(Calendar.DAY_OF_MONTH) + " ";
		dateAffichee = dateAffichee
				+ cal.getDisplayName(Calendar.MONTH, Calendar.LONG,
						Locale.FRANCE) + " ";
		dateAffichee = dateAffichee + cal.get(Calendar.YEAR);

		add(new Label("message", "Plong\u00e9e du " + dateAffichee + "    '" + plongee.getType() + "'"));
		add(new Label("nbInscrit", "Nombre de participants " + plongee.getParticipants().size()+ ""));

		List<Adherent> adherentsInscrit = plongee.getParticipants();

		add(new DataView<Adherent>("participants",
				new InscritsPlongeeDataProvider(adherentsInscrit)) {
			protected void populateItem(final Item<Adherent> item) {
				Adherent adherent = item.getModelObject();

				item.add(new Label("nom", adherent.getNom()));
				item.add(new Label("prenom", adherent.getPrenom()));

				// Dès que le plongeur est encadrant, on affiche son niveau
				// d'encadrement
				String niveauAffiche = adherent.getPrerogative();

				// Pour les externes, le niveau est suffixé par (Ext.)
				String refParrain = "";
				String noTelParrain = "";
				if (adherent.getActifInt() == 2){
					niveauAffiche = niveauAffiche + " (Ext.)";
					Adherent parrain = session.getAdherentService().rechercherParrainParIdentifiantFilleul(adherent.getNumeroLicense(), plongee.getId());
					if(null != parrain){
						refParrain=parrain.getNom().concat(" "+parrain.getPrenom());
						noTelParrain=parrain.getTelephone();
					}
				}
					
				item.add(new Label("niveau", niveauAffiche));
				item.add(new Label("telephone", adherent.getTelephone()));
				
				item.add(new Label("nomParrain", refParrain));
				item.add(new Label("telParrain", noTelParrain));

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

		List<Adherent> adhereAttente = plongee.getParticipantsEnAttente();

		DataView<Adherent> dataView = new DataView<Adherent>("listeAttente",
				new ListeAttentePlongeeDataProvider(adhereAttente)) {
			protected void populateItem(final Item<Adherent> item) {
				Adherent adherent = item.getModelObject();

				item.add(new Label("nom", adherent.getNom()));
				item.add(new Label("prenom", adherent.getPrenom()));

				// Dès que le plongeur est encadrant, on affiche son niveau
				// d'encadrement
				String niveauAffiche = adherent.getPrerogative();

				// Pour les externes, le niveau est suffixé par (Ext.)
				String refParrain = "";
				String noTelParrain = "";
				if (adherent.getActifInt() == 2){
					niveauAffiche = niveauAffiche + " (Ext.)";
					Adherent parrain = session.getAdherentService().rechercherParrainParIdentifiantFilleul(adherent.getNumeroLicense(), plongee.getId());
					if(null != parrain){
						refParrain=parrain.getNom().concat(" "+parrain.getPrenom());
						noTelParrain=parrain.getTelephone();
					}
				}
					
				item.add(new Label("niveau", niveauAffiche));
				item.add(new Label("telephone", adherent.getTelephone()));
				
				item.add(new Label("nomParrain", refParrain));
				item.add(new Label("telParrain", noTelParrain));

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
	}
}
