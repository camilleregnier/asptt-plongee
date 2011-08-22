package com.asptt.plongee.resa.ui.web.wicket.page.consultation;

import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.datetime.StyleDateConverter;
import org.apache.wicket.datetime.markup.html.form.DateTextField;
import org.apache.wicket.extensions.yui.calendar.DatePicker;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.tree.AbstractTree;
import org.apache.wicket.markup.html.tree.BaseTree;
import org.apache.wicket.markup.html.tree.LabelTree;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.validation.validator.EmailAddressValidator;
import org.apache.wicket.validation.validator.PatternValidator;
import org.apache.wicket.validation.validator.StringValidator.ExactLengthValidator;

import com.asptt.plongee.resa.exception.ResaException;
import com.asptt.plongee.resa.exception.TechnicalException;
import com.asptt.plongee.resa.mail.PlongeeMail;
import com.asptt.plongee.resa.model.Adherent;
import com.asptt.plongee.resa.model.ContactUrgent;
import com.asptt.plongee.resa.ui.web.wicket.ResaSession;
import com.asptt.plongee.resa.ui.web.wicket.page.AccueilPage;
import com.asptt.plongee.resa.ui.web.wicket.page.TemplatePage;

public class InfoAdherent extends TemplatePage {
	
	FeedbackPanel feedback = new FeedbackPanel("feedback");
	//private TreeTable tree;
	private BaseTree tree;
	
	public InfoAdherent() {
		add(new Label("message", getResaSession().getAdherent().getPrenom() + ", ci-dessous tes infos perso"));
		feedback.setOutputMarkupId(true);
		add(feedback);
		
		Adherent adherent = getResaSession().getAdherent(); 
		add(new InfoAdherentForm("inputForm", adherent));
		
		
	}

	class InfoAdherentForm extends  Form<Adherent> {

		private static final long serialVersionUID = 5374674730458593314L;

		public InfoAdherentForm(String id, Adherent adherent) {
			super(id);
			
			CompoundPropertyModel<Adherent> model = new CompoundPropertyModel<Adherent>(adherent);
			setModel(model);
			
			add(new Label("nom",adherent.getNom()));
			add(new Label("prenom",adherent.getPrenom()));
			add(new Label("numeroLicense",adherent.getNumeroLicense()));

			// numéro de téléphone au bon format (10 caractères numériques)
			RequiredTextField<String> telephone = new RequiredTextField<String>("telephone", String.class);
			telephone.add(ExactLengthValidator.exactLength(10));
			telephone.add(new PatternValidator("\\d{10}"));
			add(telephone);
			
			add(new RequiredTextField<String>("mail").add(EmailAddressValidator.getInstance()));

			add(new Label("niveau",adherent.getNiveau()));
			
			// Ajout de la checkbox pilote
			if(adherent.isPilote()){
				add(new Label("pilote","oui"));
			}else{
				add(new Label("pilote","non"));
			}
			

			// Ajout de la liste des niveaux d'encadrement
			add(new Label("encadrement",adherent.getEncadrement()));

			
			//Ajout des nouveaux champs date du certificat medical et de l'année de cotisation
			DateTextField dateCMTextFiled = new DateTextField("dateCM", new PropertyModel<Date>(model, "dateCM"), new StyleDateConverter("S-", true));
			dateCMTextFiled.setRequired(false);
			dateCMTextFiled.setEnabled(false);
			add(dateCMTextFiled);
			dateCMTextFiled.add(new DatePicker());
			
			/**
			add(new AjaxButton("validInfo") {
				@Override
				// La validation doit se faire en Ajax car le formulaire de la
				// fenêtre principal n'y a pas accés
				// http://yeswicket.com/index.php?post/2010/04/26/G%C3%A9rer-facilement-les-fen%C3%AAtres-modales-avec-Wicket
				protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
					Adherent adherent = (Adherent) form.getModelObject();
					
//					// Mise au format des noms et prénom
//					adherent.setNom(adherent.getNom().toUpperCase());
//					adherent.setPrenom((adherent.getPrenom().substring(0, 1).toUpperCase()) + (adherent.getPrenom().substring(1).toLowerCase()));

					// Mise à jour de l'adhérent
					try {
						ResaSession resaSession = (ResaSession) getApplication()
								.getSessionStore().lookup(getRequest());
						resaSession.getAdherentService().updateAdherent(adherent, PlongeeMail.MAIL_MODIF_INFO_ADHERENT);

						setResponsePage(AccueilPage.class);
						
					} catch (TechnicalException e) {
						e.printStackTrace();
						error(e.getKey());
					} catch (ResaException e) {
						e.printStackTrace();
						error(e.getKey());
						setResponsePage(new InfoAdherent());
					}

				}
				// L'implémentation de cette méthode est nécessaire pour voir
				// les messages d'erreur dans le feedBackPanel
				protected void onError(AjaxRequestTarget target, Form<?> form) {
					target.addComponent(feedback);
				}

			});
			*/

			add(new Link("cancel") {
				@Override
				public void onClick() {
					setResponsePage(AccueilPage.class);
				}
			});
			
			// L'arbre des contacts urgents
			/** 
			tree = new LabelTree("tree", createTreeModel(adherent));
			add(tree);
			tree.getTreeState().collapseAll();
			*/
		}

	}
	
	protected TreeModel createTreeModel(Adherent adherent)
	{
		return convertToTreeModel(adherent.getContacts());
	}
	
	private TreeModel convertToTreeModel(List<ContactUrgent> l1)
	{
		TreeModel model = null;
		DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode("Contact(s) urgent(s)");
		add(rootNode, l1);
		model = new DefaultTreeModel(rootNode);
		return model;
	}
	
	private void add(DefaultMutableTreeNode parent, List<ContactUrgent> l1)
	{
		for (Iterator<ContactUrgent> i = l1.iterator(); i.hasNext();)
		{
			ContactUrgent contactU = i.next();

			DefaultMutableTreeNode child = new DefaultMutableTreeNode(contactU.toString());
			parent.add(child);
		}
	}
	
	protected AbstractTree getTree()
	{
		return tree;
	}
}
