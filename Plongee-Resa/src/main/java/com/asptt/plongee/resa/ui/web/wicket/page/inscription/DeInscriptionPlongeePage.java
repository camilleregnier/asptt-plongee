package com.asptt.plongee.resa.ui.web.wicket.page.inscription;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import javax.mail.MessagingException;
import javax.smartcardio.ATR;

import org.apache.commons.mail.Email;
import org.apache.commons.mail.SimpleEmail;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxLink;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow.WindowClosedCallback;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
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
import com.asptt.plongee.resa.ui.web.wicket.page.AccueilPage;
import com.asptt.plongee.resa.ui.web.wicket.page.ErreurTechniquePage;
import com.asptt.plongee.resa.ui.web.wicket.page.TemplatePage;
import com.asptt.plongee.resa.ui.web.wicket.page.admin.AdherentPanel;

public class DeInscriptionPlongeePage extends TemplatePage {

	private Adherent adh = null;
	private List<Plongee> plongees;
	private final FeedbackPanel feedback;
	
	private ModalWindow modalConfirm;
	
	public DeInscriptionPlongeePage() {

		this.adh = getResaSession().getAdherent(); 
		
		add(new Label("message", adh.getPrenom() + ", voici les plongées auxquelles tu es inscrit(e)"));
		
		feedback = new FeedbackPanel("feedback");
		feedback.setOutputMarkupId(true);
		add(feedback);
		
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
		
		init();
	}

	private void init() {
		try{
//			plongees = getResaSession().getPlongeeService().rechercherPlongeesAdherentInscrit( 	
//					getResaSession().getAdherent(), 24);
			plongees = getResaSession().getPlongeeService().rechercherPlongeesAdherentInscrit( 	
					getResaSession().getAdherent(), 0);
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
						deInscrire(target, item.getModel());
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

	public void deInscrire(AjaxRequestTarget target, IModel<Plongee> iModel) {
		
		final Plongee plongee = iModel.getObject();
		try {
			Adherent adherent = getResaSession().getAdherent();
			
			//SI c'est un encadrant il faut verifier s'il en reste assez
			//et sinon envoyer un mail 
			if(adherent.getEncadrement() == null){	
				//Ce n'est pas un encadrant : on desinscrit
				getResaSession().getPlongeeService().deInscrireAdherent(
						plongee, 
						getResaSession().getAdherent());
				
				setResponsePage(new InscriptionConfirmationPlongeePage(plongee));
				//S'il y a des personnes en liste d'attente => mail aux ADMIN
				if(getResaSession().getPlongeeService().rechercherListeAttente(plongee).size() > 0){
					// Mise en forme de la date
					SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
					String dateAffichee = sdf.format(plongee.getDate());
					//ENVOI d'un Mail
					Email eMail = new SimpleEmail();
					eMail.setSubject("Gestion de file d'attente - plongée du : "+dateAffichee);
					StringBuffer sb = new StringBuffer("Bonjour,\n");
					sb.append("Des personnes sont en file d'attente sur la plongée du "+dateAffichee+" du "+plongee.getType()+"\n");
					sb.append("\n");
					sb.append("Une place vient de se libérer.\n");
					sb.append("\n");
					sb.append("Cordialement\n");
					
					eMail.setMsg(sb.toString());
					List<String> destis = new ArrayList<String>();
					destis.add("eric.simon28@orange.fr");
					destis.add("camille.regnier@gmail.com");
					
					PlongeeMail pMail = new PlongeeMail(eMail);
					pMail.sendMail("ADMIN");
				}
				setResponsePage(new InscriptionConfirmationPlongeePage(plongee));
			} else {
				//C'est un encadrant : on regarde s'il en reste assez
				if(getResaSession().getPlongeeService().isEnoughEncadrant(plongee)){
					getResaSession().getPlongeeService().deInscrireAdherent(
							plongee, 
							getResaSession().getAdherent());
					//S'il y a des personnes en liste d'attente => mail
					if(getResaSession().getPlongeeService().rechercherListeAttente(plongee).size() > 0){
						// Mise en forme de la date
						SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
						String dateAffichee = sdf.format(plongee.getDate());
						//ENVOI d'un Mail
						Email eMail = new SimpleEmail();
						eMail.setSubject("Gestion de file d'attente - plongée du : "+dateAffichee);
						
						StringBuffer sb = new StringBuffer("Bonjour,\n");
						sb.append("Des personnes sont en file d'attente sur la plongée du "+dateAffichee+" du "+plongee.getType()+"\n");
						sb.append("\n");
						sb.append("Une place vient de se libérer.\n");
						sb.append("\n");
						sb.append("Cordialement\n");
						
						eMail.setMsg(sb.toString());
						List<String> destis = new ArrayList<String>();
						destis.add("eric.simon28@orange.fr");
						destis.add("camille.regnier@gmail.com");
						
						PlongeeMail pMail = new PlongeeMail(eMail);
						pMail.sendMail("ADMIN");
					}
					setResponsePage(new InscriptionConfirmationPlongeePage(plongee));
				} else {
					// Il en reste pas assez
					// Fenêtre de confirmation
					replaceModalWindow(target, plongee);
					modalConfirm.show(target);
				}
			}
		} catch (TechnicalException e) {
			e.printStackTrace();
			error(e.getKey());
		}  catch (MessagingException e) {
			e.printStackTrace();
			error(e.getMessage());
		} catch (ResaException e) {
			e.printStackTrace();
			error(e.getKey());
		} finally {
			target.addComponent(feedback);
		}
	}
	
	private void replaceModalWindow(AjaxRequestTarget target, Plongee plongee) {
		modalConfirm.setContent(new ConfirmSelectionModal(modalConfirm.getContentId(), plongee));
		modalConfirm.setTitle("Modifiez les informations à mettre à jour");
		modalConfirm.setUseInitialHeight(true);
		
		// Pour éviter le message de disparition de la fenetre lors de la validation
		target.appendJavascript( "Wicket.Window.unloadConfirmation  = false;");
	}

	public class ConfirmSelectionModal extends Panel
	{
		public ConfirmSelectionModal(String id, final Plongee plongee)
		{
			super(id);
			
			// Informations précisant la plongeur concerné et la plongée
			// dans la fenêtre de confirmation de désinscription
			add(new Label("infoPlongeur", "Etes-vous sûr de vouloir vous désinscrire : il ne reste plus assez d'encadrant"));
			add(new Label("infoPlongee", " à la plongée du " + plongee.getDate() + " " + plongee.getType() + " ?"));
			
			// Le lien qui va fermer la fenêtre de confirmation
			// et appeler la méthode de désinscription de la page principale (DesInscriptionPlongeePage)
			add(new IndicatingAjaxLink("yes")
			{
				private static final long serialVersionUID = 1L;

				@Override
				public void onClick(AjaxRequestTarget target)
				{
					// On desinscrit
					getResaSession().getPlongeeService().deInscrireAdherent(
							plongee, 
							getResaSession().getAdherent());
					try{
						// Mise en forme de la date
						SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
						String dateAffichee = sdf.format(plongee.getDate());
						//Envoi du mail
						Email eMail = new SimpleEmail();
						eMail.setSubject("Encadrement de la plongée du : "+dateAffichee);
						StringBuffer sb = new StringBuffer("Bonjour,\n");
						sb.append("Un encadrant vient de se désinscrire de la plongée du "+dateAffichee+" du "+plongee.getType()+"\n");
						sb.append("\n");
						sb.append("Il n'y a plus assez d'encadrant pour assurer la plongée.\n");
						sb.append("\n");
						sb.append("Cordialement\n");
						
						eMail.setMsg(sb.toString());
						List<String> destis = new ArrayList<String>();
						destis.add("eric.simon28@orange.fr");
						destis.add("camille.regnier@gmail.com");
	
						PlongeeMail pMail = new PlongeeMail(eMail);
						pMail.sendMail("ADMIN");
						setResponsePage(DeInscriptionPlongeePage.class);
					} 
						catch (ResaException e) {
						e.printStackTrace();
						error(e.getMessage());
					} 
					catch (MessagingException e) {
						e.printStackTrace();
						error(e.getMessage());
					} finally {
						target.addComponent(feedback);
					}

				}
			});
			
			add(new IndicatingAjaxLink("no")
			{
				private static final long serialVersionUID = 1L;

				@Override
				public void onClick(AjaxRequestTarget target)
				{
					modalConfirm.close(target);
				}
			});

		}

	}


}
