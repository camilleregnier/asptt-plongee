package com.asptt.plongee.resa.model;

import java.util.ArrayList;
import java.util.List;

public class Adherent {

	public static enum Encadrement {E1, E2, E3, E4 }
	
	private String numeroLicense; // ID
	private String nom;
	private String prenom;
	private String encadrement;
	private NiveauAutonomie n = NiveauAutonomie.P0;
	private String niveau = NiveauAutonomie.P0.toString();
	private boolean pilote;
	private boolean dp;
	private Byte[] photo; // au format jpeg ??
	private String telephone;
	private String mail;
	private List<String> roles;

	
	public Adherent() {
		// valeurs par défaut
		pilote = false;
		dp = false;
	}

	public String getPrenom() {
		return prenom;
	}

	public void setPrenom(String prenom) {
		this.prenom = prenom;
	}

	public String getNumeroLicense() {
		return numeroLicense;
	}

	public void setNumeroLicense(String numeroLicense) {
		this.numeroLicense = numeroLicense;
	}

	public String getNom() {
		return nom;
	}

	public void setNom(String nom) {
		this.nom = nom;
	}

	public String getEncadrement() {
		return encadrement;
	}

	public void setEncadrement(Encadrement encadrement) {
		if(null == encadrement){
			this.encadrement = null;
		}else{
			this.encadrement = encadrement.toString();
		}
	}
	
	public void setEncadrement(String encadrement) {
		setEncadrement(Encadrement.valueOf(encadrement));
	}

	public String getNiveau() {
		return niveau;
	}

	public void setNiveau(NiveauAutonomie niveau) {
		if(niveau.equals(niveau.P5)){
			setDp(true);
		}
		this.niveau = niveau.toString();
	}
	
	public void setNiveau(String niveau) {
		setNiveau(NiveauAutonomie.valueOf(niveau));
	}

	public boolean isPilote() {
		return pilote;
	}

	public void setPilote(boolean pilote) {
		this.pilote = pilote;
	}

	public boolean isDp() {
		if(getNiveau().equals(NiveauAutonomie.P5)){
			return true;
		}else{
			return false;
		}
	}

	public void setDp(boolean dp) {
		this.dp = dp;
	}

	public Byte[] getPhoto() {
		return photo;
	}

	public void setPhoto(Byte[] photo) {
		this.photo = photo;
	}

	public String getTelephone() {
		return telephone;
	}

	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}

	public String getMail() {
		return mail;
	}

	public void setMail(String mail) {
		this.mail = mail;
	}

	public List<String> getRoles() {
		if(null == roles){
			roles = new ArrayList<String>();
		}
		return roles;
	}

	public void setRoles(List<String> roles) {
		this.roles = roles;
	}

}
