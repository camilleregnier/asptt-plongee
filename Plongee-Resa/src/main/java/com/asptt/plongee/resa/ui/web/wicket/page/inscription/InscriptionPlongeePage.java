package com.asptt.plongee.resa.ui.web.wicket.page.inscription;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.wicket.datetime.markup.html.basic.DateLabel;
import org.apache.wicket.feedback.IFeedback;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Check;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.CheckBoxMultipleChoice;
import org.apache.wicket.markup.html.form.CheckGroup;
import org.apache.wicket.markup.html.form.CheckGroupSelector;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.util.convert.converters.DateConverter;

import com.asptt.plongee.resa.model.Adherent;
import com.asptt.plongee.resa.model.Plongee;
import com.asptt.plongee.resa.service.PlongeeService;
import com.asptt.plongee.resa.ui.web.wicket.page.TemplatePage;
import com.asptt.plongee.resa.ui.web.wicket.page.admin.GererPlongeeAOuvrirTwo;

public class InscriptionPlongeePage extends TemplatePage {
	
	private Adherent adhSecretariat;
	
	public InscriptionPlongeePage(){
		
		add(new Label("message", getResaSession().getAdherent().getPrenom() + ", ci-dessous, les plongées auxquelles tu peux t'inscrire"));
		
		final FeedbackPanel feedback = new FeedbackPanel("feedback");
		add(feedback);
		
		add(new InscriptionPlongeeFrom("inputForm", feedback));
	}
	
	public InscriptionPlongeePage(Adherent adherent) {
		
		adhSecretariat = adherent;
		
		add(new Label("message", adhSecretariat.getPrenom() + " " + adhSecretariat.getNom() + " peut s'inscrire aux plongées suivantes"));
		
		final FeedbackPanel feedback = new FeedbackPanel("feedback");
		add(feedback);
		
		add(new InscriptionPlongeeFrom("inputForm", feedback));
	}

	public class InscriptionPlongeeFrom extends Form {

		private static final long serialVersionUID = -1555366090072306934L;
		
		private List<Plongee> data;
		CheckGroup<Plongee> group = new CheckGroup<Plongee>("group", new ArrayList<Plongee>());

		@SuppressWarnings("serial")
		public InscriptionPlongeeFrom(String name, IFeedback feedback) {

			super(name);
					
			add(group);

			group.add(new CheckGroupSelector("groupselector"));
			
			/*
			 * Retourne la liste des plongées ouvertes, pour les 7 prochains jours
			 */
			data = getResaSession().getPlongeeService().rechercherPlongeePourInscriptionAdherent( 	
					adhSecretariat != null ? adhSecretariat : getResaSession().getAdherent());

			ListView<Plongee> list = new ListView<Plongee>("plongeeList", data){
				public void populateItem(ListItem<Plongee> listItem) {           
					listItem.add(new Check<Plongee>("check", listItem.getModel()));
					
					// Formatage de la date affichée
					Calendar cal = Calendar.getInstance();
					cal.setTime(listItem.getModel().getObject().getDate());
					String dateAffichee = cal.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.FRANCE) + " ";
					dateAffichee = dateAffichee + cal.get(Calendar.DAY_OF_MONTH) + " ";
					dateAffichee = dateAffichee + cal.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.FRANCE) + " ";
					dateAffichee = dateAffichee + cal.get(Calendar.YEAR);
					
					listItem.add(new Label("date", dateAffichee));                
					listItem.add(new Label("type",new PropertyModel<String>(listItem.getDefaultModel(), "type")));
					listItem.add(new Label("niveauMini",new PropertyModel<String>(listItem.getDefaultModel(), "niveauMinimum")));
				}
			
			}.setReuseItems(true);
			group.add(list);
			
		}

		public void onSubmit() {
			// TODO appeler le service d'inscription
			Collection<Plongee> list = group.getModelObject();
			List<Plongee> plongees = new ArrayList<Plongee>(list);
			for(Plongee plongee : plongees){
				int response = getResaSession().getPlongeeService().isOkForResa(
						plongee, 
						getResaSession().getAdherent());
				
				switch (response) {
				case 1: //on peux inscrire l'adherent à la plongee
					getResaSession().getPlongeeService().inscrireAdherent(
							plongee, 
							adhSecretariat != null ?  adhSecretariat : getResaSession().getAdherent());
					setResponsePage(new InscriptionConfirmationPlongeePage(plongee));
					break;
				case 0: //on inscrit l'adherent en liste d'attente
					if(getResaSession().getPlongeeService().isOkForListeAttente(
							plongee, 
							getResaSession().getAdherent())){
						//on peut inscrire l'adherent en liste attente
						getResaSession().getPlongeeService().inscrireAdherentEnListeAttente(
								plongee, 
								adhSecretariat != null ?  adhSecretariat : getResaSession().getAdherent());
						setResponsePage(new InscriptionListeAttentePlongeePage(plongee));
					}
					break;
				case 2: // ouvrir la plongée
					setResponsePage(new GererPlongeeAOuvrirTwo(plongee));
					break;
				case -1: 
					/*
					 * Inscription impossible
					 */
					setResponsePage(new InscriptionFailurePlongeePage(plongee));
					break;
				default:
					/*
					 * Inscription impossible
					 */
					setResponsePage(new InscriptionFailurePlongeePage(plongee));
					break;
				}
//				if(response == 1){
//					//on peux inscrire l'adherent à la plongee
//					getResaSession().getPlongeeService().inscrireAdherent(
//							plongee, 
//							getResaSession().getAdherent());
//					setResponsePage(new InscriptionConfirmationPlongeePage(plongee));
//				}else{
//					//on verifie si on peut le mettre en liste Attente
//					if(getResaSession().getPlongeeService().isOkForListeAttente(
//							plongee, 
//							getResaSession().getAdherent())){
//						//on peut inscrire l'adherent en liste attente
//						getResaSession().getPlongeeService().inscrireAdherentEnListeAttente(
//								plongee, 
//								getResaSession().getAdherent());
//						setResponsePage(new InscriptionListeAttentePlongeePage(plongee));
//					}else{
//						/*
//						 * Inscription impossible
//						 */
//					}
//				}
			}
		}

	}

}
