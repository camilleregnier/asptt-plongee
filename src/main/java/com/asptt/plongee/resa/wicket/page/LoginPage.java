package com.asptt.plongee.resa.wicket.page;

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
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.request.RequestParameters;

public class LoginPage extends SignInPage {
	
	
	
	public LoginPage() {
		super();
		add(new Label("cotisation", ""));
	}

	public LoginPage(PageParameters parameters) {
		super(parameters);
		if(null == parameters.getKey("cotisation")){
			add(new Label("cotisation", ""));
		} else {
			add(new Label("cotisation", parameters.getString("cotisation")));
		}
	}

}
