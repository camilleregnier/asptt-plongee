package com.asptt.plongee.resa.ui.web.wicket.page.admin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormChoiceComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.datetime.StyleDateConverter;
import org.apache.wicket.datetime.markup.html.form.DateTextField;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxLink;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.extensions.yui.calendar.DatePicker;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.ListMultipleChoice;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.navigation.paging.PagingNavigator;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.validation.IValidator;
import org.apache.wicket.validation.validator.EmailAddressValidator;
import org.apache.wicket.validation.validator.PatternValidator;
import org.apache.wicket.validation.validator.StringValidator.ExactLengthValidator;

import com.asptt.plongee.resa.exception.ResaException;
import com.asptt.plongee.resa.exception.TechnicalException;
import com.asptt.plongee.resa.mail.PlongeeMail;
import com.asptt.plongee.resa.model.Adherent;
import com.asptt.plongee.resa.model.AdherentDataProvider;
import com.asptt.plongee.resa.model.ContactUrgent;
import com.asptt.plongee.resa.model.ContactUrgentDataProvider;
import com.asptt.plongee.resa.model.NiveauAutonomie;
import com.asptt.plongee.resa.ui.web.wicket.ResaSession;
import com.asptt.plongee.resa.ui.web.wicket.page.AccueilPage;

public class ContactPanel extends Panel {
	
	private static final long serialVersionUID = 1L;
	private CompoundPropertyModel<Adherent> modelAdherent;
	private DataView<ContactUrgent> cuView;
	
	private ModalWindow modalContactModif;
	private ModalWindow modalContactCreer;
	private ModalWindow modalConfirmSupp;
	
	public ContactPanel(String id, IModel<Adherent> adherent) {
		super(id, adherent);
		setOutputMarkupId(true);
		
		modalContactModif = new ModalWindow("modalContactModif");
		modalContactModif.setTitle("This is modal window with panel content.");
		modalContactModif.setCookieName("modal-modif-contact");
		add(modalContactModif);
		
		modalContactCreer = new ModalWindow("modalContactCreer");
		modalContactCreer.setTitle("This is modal window with panel content.");
		modalContactCreer.setCookieName("modal-creer-contact");
		add(modalContactCreer);
		
		modalConfirmSupp = new ModalWindow("modalConfirmSupp");
		modalConfirmSupp.setCookieName("modal-supp-contact");
		modalConfirmSupp.setTitle("Confirmation");
		modalConfirmSupp.setResizable(false);
		modalConfirmSupp.setInitialWidth(30);
		modalConfirmSupp.setInitialHeight(15);
		modalConfirmSupp.setWidthUnit("em");
		modalConfirmSupp.setHeightUnit("em");
		modalConfirmSupp.setCssClassName(ModalWindow.CSS_CLASS_BLUE);		
		add(modalConfirmSupp);
		
		add(new ContactForm("inputForm", adherent));
	}

	class ContactForm extends Form {

		private static final long serialVersionUID = 5374674730458593314L;

		public ContactForm(String id, IModel<Adherent> adherent) {
			super(id,adherent);
			modelAdherent = new CompoundPropertyModel<Adherent>(adherent);
			setModel(modelAdherent);
			
			add(new IndicatingAjaxLink("create")
			{
				@Override
				public void onClick(AjaxRequestTarget target)
				{
						replaceModalContactCreer(target);
						modalContactCreer.show(target);
					
				}
			});

			List<ContactUrgent> contactUrgents = adherent.getObject().getContacts();
			cuView = new DataView<ContactUrgent>("cuView", new ContactUrgentDataProvider(contactUrgents), 20) {
		        @Override
		        protected void populateItem(final Item<ContactUrgent> item) {
		        	final ContactUrgent contact = (ContactUrgent) item.getModelObject();
		        	item.add(new IndicatingAjaxLink("modif")
		        	{
		        		@Override
		        		public void onClick(AjaxRequestTarget target)
		        		{
		        			replaceModalContactModif(target, item.getModel());
		        			modalContactModif.show(target);
		        		}
		        	});

		        	item.add(new Label("titre", contact.getTitre()));
		        	item.add(new Label("nom", contact.getNom()));
		        	item.add(new Label("prenom", contact.getPrenom()));
		        	item.add(new Label("telephone", contact.getTelephone()));
		        	item.add(new Label("telephtwo", contact.getTelephtwo()));

		        	item.add(new IndicatingAjaxLink("supp")
					{
						@Override
						public void onClick(AjaxRequestTarget target)
						{
							replaceModalSuppContact(target, contact);
							modalConfirmSupp.show(target);
						}
					});
		        
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
	}

	private void replaceModalContactModif(AjaxRequestTarget target, IModel<ContactUrgent> contact) {
		modalContactModif.setContent(new ModifContactPanel(modalContactModif.getContentId(), contact));
		modalContactModif.setTitle("Modifiez les contacts \u00e0 mettre \u00e0 jour");
		modalContactModif.setUseInitialHeight(true);
		// Pour éviter le message de disparition de la fenetre lors de la validation
		target.appendJavascript( "Wicket.Window.unloadConfirmation  = false;");
	}

	public class ModifContactPanel extends Panel {
		
		private CompoundPropertyModel<ContactUrgent> modelContactModif;
		
		public ModifContactPanel(String id, IModel<ContactUrgent> contact) {
			super(id, contact);
			setOutputMarkupId(true);
			add(new ModifContactForm("modifForm", contact));
		}

		class ModifContactForm extends Form {

			private static final long serialVersionUID = 5374674730458593314L;

			public ModifContactForm(String id, IModel<ContactUrgent> contact) {
				super(id,contact);
				modelContactModif = new CompoundPropertyModel<ContactUrgent>(contact);
				setModel(modelContactModif);
				
				final FeedbackPanel feedback = new FeedbackPanel("feedback");
				feedback.setOutputMarkupId(true);
				add(feedback);
				// Ajout de la liste des titres
				List<String> titres = new ArrayList<String>();
					titres.add("Mr");
					titres.add("Mme");
				add(new DropDownChoice("titre", titres).setRequired(true));
				add(new RequiredTextField<String>("nom"));
				add(new RequiredTextField<String>("prenom"));
				
				// numéro de téléphone au bon format (10 caractères numériques)
				RequiredTextField<String> telephone = new RequiredTextField<String>("telephone", String.class);
				telephone.add(ExactLengthValidator.exactLength(10));
				telephone.add(new PatternValidator("\\d{10}"));
				add(telephone);
				
				// numéro de téléphone au bon format (10 caractères numériques)
				TextField<String> telephtwo = new TextField<String>("telephtwo", String.class);
				add(telephtwo);

				add(new AjaxButton("modifContact") {

					@Override
					protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
						ContactUrgent contact = (ContactUrgent) form.getModelObject();
						Adherent adh = (Adherent) modelAdherent.getObject();
						try {
							//Pour rafraichir les contact dans la fenetre parent
							List<ContactUrgent> l_cu = adh.getContacts();
							adh.setContacts(l_cu);
							target.addComponent(cuView.getParent());
							modalContactModif.close(target);
						}  catch (TechnicalException e) {
							e.printStackTrace();
							error(e.getKey());
						}

					}
					// L'implémentation de cette méthode est nécessaire pour voir
					// les messages d'erreur dans le feedBackPanel
					protected void onError(AjaxRequestTarget target, Form<?> form) {
						target.addComponent(feedback);
					}
				});
				
				add(new AjaxButton("cancelModif") {
					@Override
					public void onSubmit(AjaxRequestTarget target, Form<?> form) {
						modalContactModif.close(target);
					}
				});
				
			}

		}
	}
	
	private void replaceModalContactCreer(AjaxRequestTarget target) {
		modalContactCreer.setContent(new CreerContactPanel(modalContactCreer.getContentId()));
		modalContactCreer.setTitle("Creation des contacts");
		modalContactCreer.setUseInitialHeight(true);
		// Pour éviter le message de disparition de la fenetre lors de la validation
		target.appendJavascript( "Wicket.Window.unloadConfirmation  = false;");
	}

	public class CreerContactPanel extends Panel {
		
		private CompoundPropertyModel<ContactUrgent> modelContactCreer;
		
		public CreerContactPanel(String id) {
			super(id);
			setOutputMarkupId(true);
			add(new CreerContactForm("creerForm"));
		}

		class CreerContactForm extends Form {

			private static final long serialVersionUID = 5374674730458593314L;

			public CreerContactForm(String id) {
				super(id);
				modelContactCreer = new CompoundPropertyModel<ContactUrgent>(new ContactUrgent());
				setModel(modelContactCreer);
				
				final FeedbackPanel feedback = new FeedbackPanel("feedback");
				feedback.setOutputMarkupId(true);
				add(feedback);
				// Ajout de la liste des titres
				List<String> titres = new ArrayList<String>();
					titres.add("Mr");
					titres.add("Mme");
					
				add(new DropDownChoice("titre", titres).setRequired(true));
				add(new RequiredTextField<String>("nom"));
				add(new RequiredTextField<String>("prenom"));
				
				// numéro de téléphone au bon format (10 caractères numériques)
				RequiredTextField<String> telephone = new RequiredTextField<String>("telephone", String.class);
				telephone.add(ExactLengthValidator.exactLength(10));
				telephone.add(new PatternValidator("\\d{10}"));
				add(telephone);
				
				// numéro de téléphone au bon format (10 caractères numériques)
				TextField<String> telephtwo = new TextField<String>("telephtwo", String.class);
				add(telephtwo);

				add(new AjaxButton("creerContact") {
					@Override
					protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
						ContactUrgent contact = (ContactUrgent) form.getModelObject();
						Adherent adh = (Adherent) modelAdherent.getObject();
						try {
							//Pour rafraichir les contacts dans la fenetre parent 
							List<ContactUrgent> l_cu = adh.getContacts();
							l_cu.add(contact);
							adh.setContacts(l_cu);
							target.addComponent(cuView.getParent());
							modalContactCreer.close(target);
						}  catch (TechnicalException e) {
							e.printStackTrace();
							error(e.getKey());
						}

					}
					// L'implémentation de cette méthode est nécessaire pour voir
					// les messages d'erreur dans le feedBackPanel
					protected void onError(AjaxRequestTarget target, Form<?> form) {
						target.addComponent(feedback);
					}
				});
				
//				add(new AjaxButton("cancelCreer") {
//					@Override
//					public void onSubmit(AjaxRequestTarget target, Form<?> form) {
//						modalContactCreer.close(target);
//						setResponsePage(GererAdherents.class);
//					}
//				});
				
			}

		}
	}
	
	private void replaceModalSuppContact(AjaxRequestTarget target, ContactUrgent contact) {
		modalConfirmSupp.setContent(new ConfirmSuppContact(modalConfirmSupp.getContentId(), contact));
		modalConfirmSupp.setTitle("Confirmation de suppression d'un contact");
//		modalConfirmSupp.setUseInitialHeight(true);
		
		// Pour éviter le message de disparition de la fenetre lors de la validation
		target.appendJavascript( "Wicket.Window.unloadConfirmation  = false;");
	}
	
	public class ConfirmSuppContact extends Panel
	{
		private static final long serialVersionUID = 196724625616748115L;

		@SuppressWarnings("unchecked")
		public ConfirmSuppContact(String id, final ContactUrgent contact)
		{
			super(id);
			 
			// Informations précisant que le plongeur est en liste d'attente  plong\u00e9e
			add(new Label("info", " Confirmez-vous la suppression du contact : " + contact.getNom() + " " + contact.getPrenom() + " ?"));
			
			// Le lien qui va fermer la fenêtre de confirmation
			// et appeler la méthode de d'inscription en liste d'attente si nécessaire
			add(new IndicatingAjaxLink("yes")
			{
				private static final long serialVersionUID = 1L;
				@Override
				public void onClick(AjaxRequestTarget target)
				{
					// On supprime le contact
					Adherent adh = (Adherent) modelAdherent.getObject();
					List<ContactUrgent> l_cu = adh.getContacts();
					l_cu.remove(contact);
					adh.setContacts(l_cu);
					target.addComponent(cuView.getParent());
					modalConfirmSupp.close(target);
				}
			});
			
			// Le lien qui va juste fermer la fenêtre de confirmation
			add(new IndicatingAjaxLink("no")
			{
				private static final long serialVersionUID = 1L;

				@Override
				public void onClick(AjaxRequestTarget target)
				{
					modalConfirmSupp.close(target);
				}
			});

		}

	}

}
