package com.asptt.plongee.resa.wicket.page.admin.adherent;

import com.asptt.plongee.resa.wicket.page.TemplatePage;
import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.panel.FeedbackPanel;

@AuthorizeInstantiation("ADMIN")
public class CreerAdherent extends TemplatePage {

    FeedbackPanel feedback = new FeedbackPanel("feedback");

    public CreerAdherent() {
        setPageTitle("Creer adherent");
        // Constructeur du formulaire et du feedback panel pour renvoyer des messages sur la page
        feedback.setOutputMarkupId(true);
        add(feedback);
        add(new CreerAdherentForm("creerAdherentForm"));
    }
}
