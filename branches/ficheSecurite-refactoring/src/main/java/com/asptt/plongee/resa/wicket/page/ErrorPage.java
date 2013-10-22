package com.asptt.plongee.resa.wicket.page;

import com.asptt.plongee.resa.exception.ResaException;
import com.asptt.plongee.resa.model.Adherent;
import com.asptt.plongee.resa.wicket.ResaSession;
import org.apache.wicket.markup.html.basic.Label;

public class ErrorPage extends TemplatePage {

    public ErrorPage(ResaException re) {
        Adherent a = ResaSession.get().getAdherent();
        add(new Label("hello", "MESSAGE : D\u00e9sol\u00e9 " + a.getPrenom() + " mais : " + re.getKey()));
    }
}
