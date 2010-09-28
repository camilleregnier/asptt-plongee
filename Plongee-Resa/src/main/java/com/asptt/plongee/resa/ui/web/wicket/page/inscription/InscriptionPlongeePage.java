package com.asptt.plongee.resa.ui.web.wicket.page.inscription;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import javax.mail.MessagingException;

import org.apache.commons.mail.Email;
import org.apache.commons.mail.SimpleEmail;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxLink;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;

import com.asptt.plongee.resa.exception.ResaException;
import com.asptt.plongee.resa.exception.TechnicalException;
import com.asptt.plongee.resa.mail.PlongeeMail;
import com.asptt.plongee.resa.model.Adherent;
import com.asptt.plongee.resa.model.Plongee;
import com.asptt.plongee.resa.model.PlongeeDataProvider;
import com.asptt.plongee.resa.ui.web.wicket.page.TemplatePage;
import com.asptt.plongee.resa.ui.web.wicket.page.admin.GererPlongeeAOuvrirTwo;

public class InscriptionPlongeePage extends TemplatePage {
	
	private Adherent adh = null;
	private List<Plongee> plongees = null;
	private FeedbackPanel feedback = new FeedbackPanel("feedback");
	
	
	public InscriptionPlongeePage(){
		this.adh = getResaSession().getAdherent(); 
		add(new Label("message", adh.getPrenom() + ", ci-dessous, les plongées auxquelles tu peux t'inscrire"));
		feedback.setOutputMarkupId(true);
		add(feedback);
		init();
	}
	
	public InscriptionPlongeePage(Adherent adherent) {
		this.adh = adherent;
		add(new Label("message", adh.getPrenom() + " " + adh.getNom() + " peut s'inscrire aux plongées suivantes"));
		feedback.setOutputMarkupId(true);
		add(feedback);
		init();
	}
	

	
	private void init() {

		try{
			plongees = getResaSession().getPlongeeService().rechercherPlongeePourInscriptionAdherent(adh);
		} catch (TechnicalException e) {
			e.printStackTrace();
			error(e.getKey());
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
					})
				);
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
			case 0: //on inscrit l'adherent en liste d'attente sans envoi de mail
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
			case 4: //on inscrit l'adherent en liste d'attente avec envoi d'un mail
				if(getResaSession().getPlongeeService().isOkForListeAttente(
						plongee, 
						getResaSession().getAdherent())){
					//on peut inscrire l'adherent en liste attente
					getResaSession().getPlongeeService().inscrireAdherentEnListeAttente(
							plongee, 
							adh != null ?  adh : getResaSession().getAdherent());
					
					// Mise en forme de la date
					SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
					String dateAffichee = sdf.format(plongee.getDate());
					//Envoi du mail
					Email eMail = new SimpleEmail();
					eMail.setSubject("Manque d'encadrement - plongée du : "+dateAffichee);
					StringBuffer sb = new StringBuffer("Bonjour,\n");
					sb.append("Nous avons un manque d'encadrant sur la plongée du "+dateAffichee+" de "+plongee.getType()+"\n");
					sb.append("Si vous êtes disponible, votre inscription est la bienvenue.\n");
					sb.append("\n");
					sb.append("-----------------------------------------------------------\n");
					sb.append("ATTENTION : dans ce cas AVERTISSEZ les administrateurs \n");
					sb.append("pour qu'ils inscrivent les personnes en liste d'attente.\n");
					sb.append("-----------------------------------------------------------\n");
					sb.append("\n");
					sb.append("Cordialement\n");
					
					eMail.setMsg(sb.toString());
					List<String> destis = new ArrayList<String>();
					destis.add("eric.simon28@orange.fr");
					destis.add("camille.regnier@gmail.com");

					PlongeeMail pMail = new PlongeeMail(eMail);
					pMail.sendMail("ENCADRANT");
					
					
					setResponsePage(new InscriptionListeAttentePlongeePage(plongee));
				}
				break;
			case 3: //on inscrit l'encadrant ou P4 en liste d'attente avec envoi d'un mail aux admins
				getResaSession().getPlongeeService().inscrireAdherent(
						plongee, 
						adh != null ?  adh : getResaSession().getAdherent());
					
					// Mise en forme de la date
					SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
					String dateAffichee = sdf.format(plongee.getDate());
					//Envoi du mail
					Email eMail = new SimpleEmail();
					eMail.setSubject("Inscription sur la plongée du : "+dateAffichee+" encore fermée");
					StringBuffer sb = new StringBuffer("Bonjour,\n");
					sb.append("l'encadrant/P4 "+adh.getNom()+" , "+adh.getPrenom()+" \n");
					sb.append("Viens de s'inscrire à la plongée du "+dateAffichee+" de "+plongee.getType()+"\n");
					sb.append("\n");
					sb.append("Cette plongée est encore fermée.\n");
					sb.append("\n");
					sb.append("Pouvez-vous l'ouvrir en trouvant un DP et/ou un pilote?.\n");
					sb.append("Cordialement\n");
					
					eMail.setMsg(sb.toString());
					List<String> destis = new ArrayList<String>();
					destis.add("eric.simon28@orange.fr");
					destis.add("camille.regnier@gmail.com");

					PlongeeMail pMail = new PlongeeMail(eMail);
					pMail.sendMail("ADMIN");
					
					setResponsePage(new InscriptionConfirmationPlongeePage(plongee));
				break;
			case 1: //on peux inscrire l'adherent à la plongee
				getResaSession().getPlongeeService().inscrireAdherent(
						plongee, 
						adh != null ?  adh : getResaSession().getAdherent());
				setResponsePage(new InscriptionConfirmationPlongeePage(plongee));
				break;

			case 2: // ouvrir la plongée
				setResponsePage(new GererPlongeeAOuvrirTwo(plongee));
				break;
			}

		} catch (ResaException e) {
			e.printStackTrace();
			error(e.getKey());
		} catch (TechnicalException e) {
			e.printStackTrace();
			error(e.getKey());
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			error(e.getCause().getMessage());
		} finally {
			target.addComponent(feedback);
		}
	}
		
}