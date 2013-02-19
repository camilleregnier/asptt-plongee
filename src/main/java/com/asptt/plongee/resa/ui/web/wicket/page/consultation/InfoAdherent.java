package com.asptt.plongee.resa.ui.web.wicket.page.consultation;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.datetime.StyleDateConverter;
import org.apache.wicket.datetime.markup.html.form.DateTextField;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxLink;
import org.apache.wicket.extensions.yui.calendar.DatePicker;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.navigation.paging.PagingNavigator;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.tree.AbstractTree;
import org.apache.wicket.markup.html.tree.BaseTree;
import org.apache.wicket.markup.html.tree.LabelTree;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.validation.validator.EmailAddressValidator;
import org.apache.wicket.validation.validator.PatternValidator;
import org.apache.wicket.validation.validator.StringValidator.ExactLengthValidator;

import com.asptt.plongee.resa.exception.ResaException;
import com.asptt.plongee.resa.exception.TechnicalException;
import com.asptt.plongee.resa.mail.PlongeeMail;
import com.asptt.plongee.resa.model.Adherent;
import com.asptt.plongee.resa.model.ContactUrgent;
import com.asptt.plongee.resa.model.ContactUrgentDataProvider;
import com.asptt.plongee.resa.ui.web.wicket.ResaSession;
import com.asptt.plongee.resa.ui.web.wicket.page.AccueilPage;
import com.asptt.plongee.resa.ui.web.wicket.page.TemplatePage;
import com.asptt.plongee.resa.ui.web.wicket.page.admin.ContactPanel;
import com.asptt.plongee.resa.util.CatalogueMessages;

public class InfoAdherent extends TemplatePage {
	
	public InfoAdherent() {
		setPageTitle("Information adherent");
		add(new Label("message",new StringResourceModel(CatalogueMessages.INFO_ADHERENT_MSG, this,new Model<Adherent>(getResaSession().getAdherent()))));
		setOutputMarkupId(true);
		init();
	}

	private void init() {

//		private static final long serialVersionUID = 5374674730458593314L;
		
		Adherent adherent = getResaSession().getAdherentService().rechercherAdherentParIdentifiant(getResaSession().getAdherent().getNumeroLicense());

			
			add(new Label("nom",adherent.getNom())); 
			add(new Label("prenom", adherent.getPrenom()));
			add(new Label("numeroLicense", adherent.getNumeroLicense()));
			add(new Label("telephone", adherent.getTelephone()));
			add(new Label("mail", adherent.getMail()));
			add(new Label("niveau",adherent.getNiveau()));
			
			// Ajout de la checkbox pilote
			if(adherent.isPilote()){
				add(new Label("pilote","oui"));
			}else{
				add(new Label("pilote","non"));
			}
			// Ajout de la liste des niveaux d'encadrement
			add(new Label("encadrement",adherent.getEncadrement()));
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			add(new Label("dateCM", sdf.format(adherent.getDateCM())));

			List<ContactUrgent> contactUrgents = adherent.getContacts();
			DataView cuView = new DataView<ContactUrgent>("cuView", new ContactUrgentDataProvider(contactUrgents), 20) {
		        @Override
		        protected void populateItem(final Item<ContactUrgent> item) {
		        	final ContactUrgent contact = (ContactUrgent) item.getModelObject();

		        	item.add(new Label("titre", contact.getTitre()));
		        	item.add(new Label("nom", contact.getNom()));
		        	item.add(new Label("prenom", contact.getPrenom()));
		        	item.add(new Label("telephone", contact.getTelephone()));
		        	item.add(new Label("telephtwo", contact.getTelephtwo()));

					item.add(new AttributeModifier("class", true,
							new AbstractReadOnlyModel<String>() {
								private static final long serialVersionUID = 5259097512265622750L;

								@Override
								public String getObject() {
									return (item.getIndex() % 2 == 1) ? "even": "odd";
								}
							})
					);
		        
		        }
	        };
		    cuView.setOutputMarkupId(true);    
	        add(cuView);
		
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
	
}
