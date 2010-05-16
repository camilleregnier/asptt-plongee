package com.asptt.plongee.resa.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Plongee implements Serializable {

	private static final long serialVersionUID = 1L;

	public static enum Type {MATIN, APRES_MIDI, SOIR, NUIT }
	
	private Integer id;
	private String type;
	private NiveauAutonomie niveauMinimum;
	private int nbMaxPlaces;
	private String niveauDP;
	private String lieux;
	private Date date;
	private Boolean ouvertureForcee;
	private Adherent dp;
	private Adherent pilote;
	private List<Adherent> participants;
	private List<Adherent> participantsEnAttente;
	
	
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
	public void setType(String type){
		setType(Type.valueOf(type));
	}
	public NiveauAutonomie getNiveauMinimum() {
		return niveauMinimum;
	}
	public void setNiveauMinimum(NiveauAutonomie niveauMinimum) {
		this.niveauMinimum = niveauMinimum;
	}
	public int getNbMaxPlaces() {
		return nbMaxPlaces;
	}
	public void setNbMaxPlaces(int nbPlaces) {
		this.nbMaxPlaces = nbPlaces;
	}
	public String getNiveauDP() {
		return niveauDP;
	}
	public void setNiveauDP(String niveauDP) {
		this.niveauDP = niveauDP;
	}
	public String getLieux() {
		return lieux;
	}
	public void setLieux(String lieux) {
		this.lieux = lieux;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public Boolean getOuvertureForcee() {
		return ouvertureForcee;
	}
	public void setOuvertureForcee(Boolean ouvertureForcee) {
		this.ouvertureForcee = ouvertureForcee;
	}
	public Adherent getDp() {
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
		if(null == participants){
			return new ArrayList<Adherent>();
		}
		return participants;
	}
	public void setParticipants(List<Adherent> participants) {
		this.participants = participants;
	}
	public List<Adherent> getParticipantsEnAttente() {
		if(null == participantsEnAttente){
			return new ArrayList<Adherent>();
		}
		return participantsEnAttente;
	}
	public void setParticipantsEnAttente(List<Adherent> participantsEnAttente) {
		this.participantsEnAttente = participantsEnAttente;
	}

	public boolean isExistDP(){
		if(null != getDp()){
			return true;
		} else{
			return false;
		}
	}
	
	public boolean isExistPilote(){
		if(null != getPilote()){
			return true;
		} else{
			return false;
		}
	}

}
