package com.asptt.plongee.resa.wicket.page;

import com.asptt.plongee.resa.wicket.ResaSession;
import org.apache.log4j.Logger;
import org.apache.wicket.authentication.pages.SignOutPage;
import org.apache.wicket.markup.html.basic.Label;

public class LogoutPage extends SignOutPage {

    private final Logger logger = Logger.getLogger(getClass());

    public LogoutPage() {
        super();
        ResaSession sess = (ResaSession) getSession();

        add(new Label("byebye", ""));
        if (null != sess.getAdherent()) {
            logger.info("L'adherent " + sess.getAdherent().getNom() + " / " + sess.getAdherent().getPrenom() + " vient de se deconnecter");
        }
    }
}
