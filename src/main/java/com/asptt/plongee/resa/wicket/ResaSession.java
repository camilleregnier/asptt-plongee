package com.asptt.plongee.resa.wicket;

import com.asptt.plongee.resa.model.Adherent;
import com.asptt.plongee.resa.model.FicheSecurite;
import com.asptt.plongee.resa.model.Plongee;
import com.asptt.plongee.resa.service.AdherentService;
import com.asptt.plongee.resa.service.PlongeeService;
import java.util.Date;
import org.apache.log4j.Logger;
import org.apache.wicket.Request;
import org.apache.wicket.Session;
import org.apache.wicket.authentication.AuthenticatedWebSession;
import org.apache.wicket.authorization.strategies.role.Roles;
import org.springframework.context.ApplicationContext;

public class ResaSession extends AuthenticatedWebSession {

    private static final long serialVersionUID = -807646178959365061L;
    private Adherent adherent;
    private Plongee plongee;
    private ApplicationContext ctx;
    private final Logger logger = Logger.getLogger(getClass());
    private static final String DATE_DEBUT_FS = "date_debut";
    private static final String DATE_FIN_FS = "date_fin";
    private static final String FICHE_SECURITE = "fs";


    public ResaSession(Request request, ApplicationContext ctx) {
        super(request);

        this.ctx = ctx;
    }

    public static ResaSession get() {
        return (ResaSession) Session.get();
    }

    public Adherent getAdherent() {
        return adherent;
    }

    public Plongee getPlongee() {
        return plongee;
    }

    public AdherentService getAdherentService() {
        return (AdherentService) ctx.getBean("adherentService");
    }

    public PlongeeService getPlongeeService() {
        return (PlongeeService) ctx.getBean("plongeeService");
    }

    @Override
    public Roles getRoles() {
        if (isSignedIn()) {
            logger.debug("adhérent authentifié : isSignedIn = true");
            logger.debug("adherent.getRoles().size() : " + adherent.getRoles().size());
            return adherent.getRoles();
        }
        logger.info("adhérent pas authentifié !");
        return null;
    }

    @Override
    public boolean authenticate(String username, String password) {
        logger.debug("avant l'appel du service");
        // FIXME a changer des que la clé primaire de l'adhérent aura été choisie
        adherent = getAdherentService().authentifierAdherent(username, password);
        logger.debug("après l'appel du service");
        if (null == adherent) {
            logger.debug("adhérent null");
            return false;
        } else {
            logger.debug("adhérent non null");
            return true;
        }
    }
    
    public void setDateDebutFS(Date dateDebut) {
        setAttribute(DATE_DEBUT_FS, dateDebut);
    }
 
    public Date getDateDebutFS() {
        Date result = (Date) getAttribute(DATE_DEBUT_FS);
        return result;
    }    

    public void setDateFinFS(Date dateFin) {
        setAttribute(DATE_FIN_FS, dateFin);
    }
 
    public Date getDateFinFS() {
        Date result = (Date) getAttribute(DATE_FIN_FS);
        return result;
    }    

    public void setFicheSecurite(FicheSecurite fs) {
        setAttribute(FICHE_SECURITE, fs);
    }
 
    public FicheSecurite getFicheSecurite() {
        FicheSecurite result = (FicheSecurite) getAttribute(FICHE_SECURITE);
        return result;
    }    

}
