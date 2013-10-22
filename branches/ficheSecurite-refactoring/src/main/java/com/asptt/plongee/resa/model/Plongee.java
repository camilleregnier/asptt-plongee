package com.asptt.plongee.resa.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class Plongee implements Serializable {

    private static final long serialVersionUID = -2548032640337298221L;

    public static enum Type {

        MATIN, APRES_MIDI, SOIR, NUIT
    }
    private Integer id;
    private String type;
    private NiveauAutonomie niveauMinimum;
    private int nbMaxPlaces;
    private String niveauDP;
    private Date date;
    private Date dateVisible;
    private Boolean ouvertureForcee;
    private Adherent dp;
    private Adherent pilote;
    private List<Adherent> participants;
    private List<Adherent> participantsEnAttente;
    private String motifAnnulation;

    public Plongee() {
        niveauMinimum = NiveauAutonomie.BATM;
        ouvertureForcee = true;
        nbMaxPlaces = 20;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type.toString();
    }

    public void setType(String type) {
        setType(Type.valueOf(type));
    }

    public NiveauAutonomie getEnumNiveauMinimum() {
        return niveauMinimum;
    }

    public String getNiveauMinimum() {
        if (null == niveauMinimum) {
            return NiveauAutonomie.P0.toString();
        } else {
            return niveauMinimum.toString();
        }
    }

    public void setEnumNiveauMinimum(NiveauAutonomie niveauMinimum) {
        this.niveauMinimum = niveauMinimum;
    }

    public void setNiveauMinimum(String niveau) {
        this.niveauMinimum = NiveauAutonomie.valueOf(niveau);
    }

    public int getNbMaxPlaces() {
        return nbMaxPlaces;
    }

    public String getMaxPlaces() {
        return String.valueOf(nbMaxPlaces);
    }

    public void setNbMaxPlaces(int nbPlaces) {
        this.nbMaxPlaces = nbPlaces;
    }

    public void setMaxPlaces(String nbPlaces) {
        this.nbMaxPlaces = Integer.valueOf(nbPlaces);
    }

    public String getNiveauDP() {
        return niveauDP;
    }

    public void setNiveauDP(String niveauDP) {
        this.niveauDP = niveauDP;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Date getDateVisible() {
        return dateVisible;
    }

    public void setDateVisible(Date date) {
        this.dateVisible = date;
    }

    public Boolean getOuvertureForcee() {
        return ouvertureForcee;
    }

    public void setOuvertureForcee(Boolean ouvertureForcee) {
        this.ouvertureForcee = ouvertureForcee;
    }

    public String getMotifAnnulation() {
        return motifAnnulation;
    }

    public void setMotifAnnulation(String motifAnnulation) {
        this.motifAnnulation = motifAnnulation;
    }

    public Adherent getDp() {
        return dp;
    }

    public Adherent getNiveauMaxEncadrant(List<Adherent> encadrants) {
        if (!encadrants.isEmpty()) {
            Collections.sort(encadrants, new AdherentComparatorDP());
            return encadrants.get(0);
        } else {
            return null;
        }
    }

    public Adherent setDp() {
        List<Adherent> participants = getParticipants();
        List<Adherent> lesDPs = new ArrayList<Adherent>();
        for (Adherent adherent : participants) {
            if (adherent.isDp()) {
                lesDPs.add(adherent);
            }
        }
        if (lesDPs.size() > 0) {
            this.dp = getNiveauMaxEncadrant(lesDPs);
        }
        return dp;
    }

    public void setDp(Adherent dp) {
        this.dp = dp;
    }

    public Adherent getPilote() {
        return pilote;
    }

    public void setPilote(Adherent pilote) {
        this.pilote = pilote;
    }

    public List<Adherent> getParticipants() {
        if (null == participants) {
            return new ArrayList<Adherent>();
        }
        return participants;
    }

    public void setParticipants(List<Adherent> participants) {
        this.participants = participants;
    }

    public List<Adherent> getParticipantsEnAttente() {
        if (null == participantsEnAttente) {
            return new ArrayList<Adherent>();
        }
        return participantsEnAttente;
    }

    public void setParticipantsEnAttente(List<Adherent> participantsEnAttente) {
        this.participantsEnAttente = participantsEnAttente;
    }

    public boolean isExistDP() {
        if (null != getDp()) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isExistPilote() {
        if (null != getPilote()) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isNbMiniAtteint(int nbMini) {
        if (getParticipants().size() >= nbMini) {
            return true;
        } else {
            return false;
        }
    }
}
