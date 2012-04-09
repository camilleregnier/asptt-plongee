package com.asptt.plongee.resa.util;

public interface CatalogueMessages {
	/**
	 * AccueilPage + InscriptionPlongeePage
	 * msg dans PlongeeApplication.properties  inscription.impossible
	 */
	//Certificat medical bientôt perimé msg affiche le nombre de jours restant
	public final String CM_A_RENOUVELER= "cm.renouveler";
	//Certificat medical perimé 
	public final String CM_PERIME= "cm.perime";
	//desinscription impossible à moins de x heure de la plongée 
    public final String DESINSCRIPTION_IMPOSSIBLE = "desinscrire.impossible";
	//desinscription impossible à moins de x heure de la plongée 
    public final String INSCRIPTION_IMPOSSIBLE = "inscrire.impossible";
	
	/**
	 * AccueilPage 
	 */

	//Message de bienvenue 
    public final String ACCUEIL_BIENVENUE= "message.bienvenue";
	//Cotisation perimée 
    public final String ACCUEIL_COTISATION_PERIME = "cotisation.perime";
    
	/**
	 * InscriptionPlongeePage
	 */
	//message de haut de page
    public final String INSCRIPTION_MSG_ADHERENT = "msg.adherent";
    public final String INSCRIPTION_MSG_SECRETARIAT = "msg.secretariat";
    //attendre l'heure d'ouverture de la plongée
    public final String INSCRIPTION_ATTENDRE_HO = "attendre.ho";
    public final String INSCRIPTION_ATTENDRE_J_HO = "attendre.j.ho";
	//confirmation de reservation
    public final String INSCRIPTION_CONFIRM_RESA = "confirm.resa";
	//inscription impossible : le nombre max de plongeur est atteint
    public final String INSCRIPTION_KO_NB_MAX_PLONGEUR = "nb.max.plongeur.atteint";
	//inscription impossible : plongee encore fermee
    public final String INSCRIPTION_KO_PLONGEE_FERMEE = "plongee.fermee";
	//inscription impossible : DP P5 niveau insuffisant P0 ou BATM
    public final String INSCRIPTION_KO_DP_P5 = "dp.p5";
	//inscription impossible : niveau minimum requis
    public final String INSCRIPTION_KO_NIVEAU_MINI = "niveau.mini";
	//inscription impossible : adherent déjà en liste d'attente
    public final String INSCRIPTION_KO_DEJA_EN_ATTENTE = "deja.en.attente";
	//inscription impossible : adherent déjà inscrit 
    public final String INSCRIPTION_KO_DEJA_INSCRIT = "deja.inscrit";
	//inscription impossible en liste d'attente : adherent déjà inscrit en liste d'attente 
    public final String INSCRIPTION_LISTE_ATTENTE_KO = "liste.att.ko";
    //attendre l'heure d'ouverture de la plongée pour les encadrants si il y en a deja 7 inscrits
    public final String INSCRIPTION_ATTENDRE_VR_HO = "attendre.vr.ho";
    public final String INSCRIPTION_ATTENDRE_VR_J_HO = "attendre.vr.j.ho";
	//inscription sur liste d'attente : nombre max de plongeurs atteint plongée complete 
    public final String INSCRIPTION_ATT_MAX_ATTEINT = "plongee.full";

    /**
	 * InscriptionPlongeePage$ConfirmSelectionModal
	 */
    //inscription sur liste d'attente : par manque d'encadrant 
    public final String INSCRIPTION_ATT_MANQUE_ENCADRANT = "manque.encadrant";
	//inscription sur liste d'attente : il existe deja une liste d'attente à gerer 
    public final String INSCRIPTION_ATT_A_GERER = "att.a.gerer";
	//inscription sur liste d'attente : information donnee au plongeur 
    public final String INSCRIPTION_ATT_INFO_PLONGEUR = "info.plongeur";
	//inscription sur liste d'attente : information sur la plongee 
    public final String INSCRIPTION_ATT_INFO_PLONGEE = "info.plongee";

    /**
	 * DesInscriptionPlongeePage
	 */
	//message de haut de page
    public final String DESINSCRIPTION_MSG_ADHERENT = "msg.adherent";
	//confirmation de desinscription
    public final String DESINSCRIPTION_CONFIRMATION = "confirmation";
    // confirmation de desincription pour les encadrants
    public final String DESINSCRIPTION_CONFIRMATION_PLONGEUR = "confirmation.vr.info.plongeur";
    public final String DESINSCRIPTION_CONFIRMATION_PLONGEE = "confirmation.vr.info.plongee";

    /**
	 * CreerPlongee
	 */
    public final String CREATION_PLONGEE_DATE_INCOMPATIBLE = "date.incompatible";
    public final String CREATION_PLONGEE_EXISTE_DEJA = "existe.deja";

    /**
	 * ConsulterPlongee
	 */
	//message de haut de page
    public final String CONSULTER_MSG_ADHERENT = "msg.adherent";
    
    /**
	 * CreerMessage + MessagePanel -> dans PlongeeApplication.properties
	 */
	//date de fin et debut incompatible
    public final String DATE_INCOMPATIBLE = "date.incompatible";
    
    /**
	 * AnnulerPlongee
	 */
	//message de haut de page
    public final String ANNULER_MSG = "msg.adherent";
    // confirmation d'annulation de la plongée
    public final String ANNULATION_CONFIRMATION_PLONGEE = "annulation.info.plongee";
    public final String ANNULATION_CONFIRMATION_PLONGEUR = "annulation.info.plongeur";

    /**
	 * ContactPanel$ConfirmSuppContact (inner class) 
	 */
	//Confirmation de la suppression du contactUrgent de l'adherent
    public final String CONTACT_CONFIRME_SUPP = "confirme.supp";

    /**
	 * GererAdherent$ConfirmSuppAdherent (inner class) 
	 */
	//Confirmation de la suppression du contactUrgent de l'adherent
    public final String ADHERENT_CONFIRME_SUPP = "confirme.supp";

    /**
	 * Gerer Liste attente ONE 
	 */
	//message de haut de page
    public final String LISTE_ATTENTE_ONE_MSG = "message";
    
    /**
	 * Gerer Liste attente TWO 
	 */
	//messages pour manipuler 1 seule personnes à la fois dans la liste d'attente
    public final String LISTE_ATTENTE_TWO_1 = "message1";
    public final String LISTE_ATTENTE_TWO_2 = "message2";

    /**
	 * GererMessage 
	 */
	//message de haut de page
    public final String GERER_MESSAGE_MSG = "message";
    
    /**
	 * Gerer Plongee A Ouvrir ONE 
	 */
	//message de haut de page
    public final String PLONGEE_A_OUVRIR_ONE_MSG = "message";
    
    /**
	 * InfoAdherent 
	 */
	//message de haut de page
    public final String INFO_ADHERENT_MSG = "message";
}
