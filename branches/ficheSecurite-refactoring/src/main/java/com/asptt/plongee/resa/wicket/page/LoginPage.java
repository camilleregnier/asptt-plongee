package com.asptt.plongee.resa.wicket.page;

import org.apache.wicket.PageParameters;
import org.apache.wicket.authentication.pages.SignInPage;
import org.apache.wicket.markup.html.basic.Label;

public class LoginPage extends SignInPage {

    public LoginPage() {
        super();
        add(new Label("cotisation", ""));
    }

    public LoginPage(PageParameters parameters) {
        super(parameters);
        if (null == parameters.getKey("cotisation")) {
            add(new Label("cotisation", ""));
        } else {
            add(new Label("cotisation", parameters.getString("cotisation")));
        }
    }
}
