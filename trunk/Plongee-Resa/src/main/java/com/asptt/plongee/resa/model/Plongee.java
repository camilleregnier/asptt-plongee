package com.asptt.plongee.resa.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.asptt.plongee.resa.model.Adherent.Encadrement;

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
	
	public Plongee(){
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
	public void setType(String type){
		setType(Type.valueOf(type));
	}
	public NiveauAutonomie getEnumNiveauMinimum() {
		return niveauMinimum;
	}
	public String getNiveauMinimum() {
		if(null == niveauMinimum){
			return NiveauAutonomie.P0.toString();
		}else{
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
		List<Adherent> participants = getParticipants();
		for(Adherent adherent : participants){
			if(adherent.isDp()){
				if(adherent.getNiveau().equalsIgnoreCase("P5")){
					dp=adherent;
				} else if(adherent.getEnumEncadrement().equals(Encadrement.E2)){
					dp=adherent;
				} else if(adherent.getEnumEncadrement().equals(Encadrement.E3)){
					dp=adherent;
				}
			}
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
	
	/**
	 * Une plongée est ouverte s'il existe 
	 *  - au moins un DP et un pilote (ça peut-être la même personne)
	 *  reunion du 2709/2010
	 *  - que le nombre de participants (inscrit + liste d'attente)
	 *  soit inferieure au nombre de plongeurs max
	 *  ceci pour bloquer l'inscription en cas de liste d'attente sur plongée pleine
	public boolean isOuverte(){
		if(  isExistDP() && isExistPilote()
				&& ((getParticipants().size() + getParticipantsEnAttente().size()) < getNbMaxPlaces()) 
				){
			return true;
		} else{
			return false;
		}
	}
	 */
}
