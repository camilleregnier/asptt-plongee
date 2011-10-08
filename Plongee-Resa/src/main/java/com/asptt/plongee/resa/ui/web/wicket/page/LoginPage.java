package com.asptt.plongee.resa.ui.web.wicket.page;

import java.io.ObjectInputStream.GetField;

import javax.swing.DefaultFocusManager;

import org.apache.wicket.Component;
import org.apache.wicket.IComponentBorder;
import org.apache.wicket.PageParameters;
import org.apache.wicket.Response;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.authentication.pages.SignInPage;
import org.apache.wicket.behavior.AbstractBehavior;
import org.apache.wicket.markup.Markup;
import org.apache.wicket.markup.MarkupElement;
import org.apache.wicket.markup.MarkupStream;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.request.RequestParameters;

public class LoginPage extends SignInPage {
	
	
	
	public LoginPage() {
		super();
//		getPageParameters();
//		Component toto = getPage().get("signInPanel");
//		toto.getClassRelativePath();
//		toto.getRequest().getRequestParameters();
//		MarkupStream ms = getMarkupStream();
//		PasswordTextField ptf = new PasswordTextField("password");
//		ptf.add(new FocusOnLoadBehavior());
//		add(ptf);
//		getPage().remove("password");
//		PasswordTextField toto = new PasswordTextField("password");
//		getPage().add(toto);
		
//		MarkupStream ms = getAssociatedMarkupStream(true);
//		ms.getWicketNamespace();
//		int index = ms.findComponentIndex(getPageRelativePath(), "signInPanel");
		
		// TODO Auto-generated constructor stub
	}

	public LoginPage(PageParameters parameters) {
		super(parameters);
//		RequestParameters rp = getRequest().getRequestParameters();
//		getRequest().getRequestParameters().getComponentPath();
//		MarkupStream ms = getAssociatedMarkupStream(true);
		
		// TODO Auto-generated constructor stub
	}

	@Override
	public MarkupStream getAssociatedMarkupStream(boolean throwException) {
		// TODO Auto-generated method stub
		return super.getAssociatedMarkupStream(throwException);
	}

	public class FocusOnLoadBehavior extends AbstractBehavior
	{
		@Override
		public void bind(Component component) {
			super.bind(component);
			component.setOutputMarkupId(true);
			component.setComponentBorder(new IComponentBorder() {
				public void renderBefore(Component component) {
				}

				public void renderAfter(Component component) {
					final Response response = component.getResponse();
					response.write(
							"<script type=\"text/javascript\" language=\"javascript\">document.getElementById(\"" +
							component.getMarkupId() +
							"\").focus()</script>");
				}
			});
		}

		@Override
		public boolean isTemporary() {
			return true;
		}
	}	
	
}
