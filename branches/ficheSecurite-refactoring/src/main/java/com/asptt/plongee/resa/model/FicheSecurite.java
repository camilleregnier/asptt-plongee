package com.asptt.plongee.resa.model;

import com.asptt.plongee.resa.wicket.page.fichesecurite.ChoiceRenderPlongeur;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FicheSecurite implements Serializable {

    private static final long serialVersionUID = -8569233748603210161L;
    private Integer id;
    private int statut;
    private String nomDP;
    private String nomPilote;
    private String site;
    private String meteo;
    private Plongee plongee;
    private List<Palanque> palanques;
    private Map<String, String> plongeursAInscrireMap;

    public FicheSecurite(Plongee unePlongee) {
        //Récupération de la fiche de securite dans la session
        //init de la plongée
        this.plongee = unePlongee;

        plongeursAInscrireMap = new HashMap<String, String>();
        for (Adherent adh : plongee.getParticipants()) {
            plongeursAInscrireMap.put(adh.getNumeroLicense(),
                    adh.getNom() + "--" + adh.getPrenom() + "--" + adh.getPrerogative());
        }

    }

    public int getNombrePalanque() {
        if (null == palanques) {
            return 0;
        } else {
            return palanques.size();
        }
    }

    public Map<String, String> getPlongeursAInscrireMap() {
        return plongeursAInscrireMap;
    }

    public void setPlongeursAInscrireMap(Map<String, String> plongeursInscritsMap) {
        this.plongeursAInscrireMap = plongeursInscritsMap;
    }

    public void suppPlongeursAInscrireMap(List<ChoiceRenderPlongeur> plongeursASupprimerMap) {
        for (ChoiceRenderPlongeur unPlongeur : plongeursASupprimerMap) {
            if (plongeursAInscrireMap.containsKey(unPlongeur.getKey())) {
                plongeursAInscrireMap.remove(unPlongeur.getKey());
            }
        }
    }

    public void majPlongeursAInscrireMap(Plongee plongee) {
        plongeursAInscrireMap.clear();
        for (Adherent adh : plongee.getParticipants()) {
            plongeursAInscrireMap.put(adh.getNumeroLicense(),
                    adh.getNom() + "--" + adh.getPrenom() + "--" + adh.getPrerogative());
        }
    }

    public int getNbPlongeurs() {
        return plongee.getParticipants().size();
    }

    public int getNbEncadrants() {
        List<Adherent> encadrants = new ArrayList<Adherent>();
        for (Adherent plongeur : plongee.getParticipants()) {
            if (plongeur.isEncadrent()) {
                encadrants.add(plongeur);
            }
        }
        return encadrants.size();
    }

    public int getNbPalanques() {
        return palanques.size();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNomDP() {
        return nomDP;
    }

    public void setNomDP(String nomDP) {
        this.nomDP = nomDP;
    }

    public String getNomPilote() {
        return nomPilote;
    }

    public void setNomPilote(String nomPilote) {
        this.nomPilote = nomPilote;
    }

    public Plongee getPlongee() {
        return plongee;
    }

    public void setPlongee(Plongee plongee) {
        this.plongee = plongee;
        majPlongeursAInscrireMap(this.plongee);
    }

    public List<Palanque> getPalanques() {
        if (null == palanques) {
            palanques = new ArrayList<Palanque>();
        }
        return palanques;
    }

    public void setPalanques(List<Palanque> palanques) {
        this.palanques = palanques;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public String getMeteo() {
        if(null == meteo){
            meteo="";
        }
        return meteo;
    }

    public void setMeteo(String meteo) {
        this.meteo = meteo;
    }

    public int getStatut() {
        return statut;
    }

    public void setStatut(int statut) {
        this.statut = statut;
    }
    
}
