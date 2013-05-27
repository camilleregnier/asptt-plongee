package com.asptt.plongee.resa.wicket;

import com.asptt.plongee.resa.wicket.page.AccueilPage;
import com.asptt.plongee.resa.wicket.page.LoginPage;
import com.asptt.plongee.resa.wicket.page.LogoutPage;
import com.asptt.plongee.resa.wicket.page.ModifPasswordPage;
import com.asptt.plongee.resa.wicket.page.admin.adherent.CreerAdherent;
import com.asptt.plongee.resa.wicket.page.admin.adherent.GererAdherents;
import com.asptt.plongee.resa.wicket.page.admin.plongee.AnnulerPlongee;
import com.asptt.plongee.resa.wicket.page.admin.plongee.CreerPlongee;
import com.asptt.plongee.resa.wicket.page.admin.plongee.GererPlongeeAOuvrirOne;
import com.asptt.plongee.resa.wicket.page.admin.plongee.attente.GererListeAttenteOne;
import com.asptt.plongee.resa.wicket.page.consultation.ConsulterPlongees;
import com.asptt.plongee.resa.wicket.page.inscription.DeInscriptionPlongeePage;
import com.asptt.plongee.resa.wicket.page.inscription.InscriptionPlongeePage;
import com.asptt.plongee.resa.wicket.page.secretariat.DesInscriptionPlongeePage;
import com.asptt.plongee.resa.wicket.page.secretariat.InscriptionAdherentPlongeePage;
import com.asptt.plongee.resa.wicket.page.secretariat.InscriptionExterieurPlongeePage;
import org.apache.log4j.Logger;
import org.apache.wicket.Request;
import org.apache.wicket.Response;
import org.apache.wicket.Session;
import org.apache.wicket.authentication.AuthenticatedWebApplication;
import org.apache.wicket.authentication.AuthenticatedWebSession;
import org.apache.wicket.authentication.pages.SignInPage;
import org.apache.wicket.markup.html.WebPage;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class PlongeeApplication extends AuthenticatedWebApplication {

    private ApplicationContext ctx;
    private final Logger logger = Logger.getLogger(getClass().getName());

    protected void init() {
        super.init();

        logger.info("init() : Début");

        // démarrage du contexte Spring (injection des dépendances)
        ctx = new ClassPathXmlApplicationContext("/spring/spring-service-impl.xml", "/spring/spring-dao-jdbc.xml", "/spring/spring-datasource.xml");

        // setting page that Wicket will display if user has no rights to access
        // a page
        getApplicationSettings().setAccessDeniedPage(LoginPage.class);

        // mounting login page so that it can be referred to in the security
        // constraint
        mountBookmarkablePage("/login", LoginPage.class);
        mountBookmarkablePage("/logout", LogoutPage.class);
        mountBookmarkablePage("/consulter", ConsulterPlongees.class);
        mountBookmarkablePage("/inscrire", InscriptionPlongeePage.class);
        mountBookmarkablePage("/desinscrire", DeInscriptionPlongeePage.class);
        mountBookmarkablePage("/accueil", AccueilPage.class);
        mountBookmarkablePage("/modifpassword", ModifPasswordPage.class);
        mountBookmarkablePage("/creeradherent", CreerAdherent.class);
        mountBookmarkablePage("/creerplongee", CreerPlongee.class);
        mountBookmarkablePage("/annulerplongee", AnnulerPlongee.class);
        mountBookmarkablePage("/gereradherents", GererAdherents.class);
        mountBookmarkablePage("/gererplongee", GererPlongeeAOuvrirOne.class);
        mountBookmarkablePage("/gererlisteattente", GererListeAttenteOne.class);
        mountBookmarkablePage("/inscriptionadherent", InscriptionAdherentPlongeePage.class);
        mountBookmarkablePage("/inscriptionexterieur", InscriptionExterieurPlongeePage.class);
        mountBookmarkablePage("/desinscription", DesInscriptionPlongeePage.class);

        logger.info("init() : Fin");

    }

    public Session newSession(Request request, Response response) {
        return new ResaSession(request, ctx);
    }

    public Class<? extends WebPage> getHomePage() {
        return AccueilPage.class;
    }

    @Override
    protected Class<? extends SignInPage> getSignInPageClass() {
        return LoginPage.class;
    }

    @Override
    protected Class<? extends AuthenticatedWebSession> getWebSessionClass() {
        return ResaSession.class;
    }
}
