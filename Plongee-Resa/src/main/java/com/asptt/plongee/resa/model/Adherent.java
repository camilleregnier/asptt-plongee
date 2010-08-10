package com.asptt.plongee.resa.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.authorization.strategies.role.Roles;

public class Adherent implements Serializable {

	public static enum Encadrement {E2, E3, E4 }
	
	private String numeroLicense; // ID
	private String nom;
	private String prenom;
	private Encadrement encadrement;
//	private NiveauAutonomie n = NiveauAutonomie.P0;
//	private String niveau = NiveauAutonomie.P0.toString();
	private NiveauAutonomie niveau;
	private boolean pilote;
	private boolean dp;
	private int actif = 1;
	private Byte[] photo; // au format jpeg ??
	private String telephone;
	private String mail;
	private Roles roles;
	private String role;

	
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

	public Encadrement getEnumEncadrement() {
		return encadrement;
	}

	public String getEncadrement() {
		if(null == encadrement){
			return null;
		}else{
			return encadrement.toString();
		}
	}

	public void setEnumEncadrement(Encadrement encadrement) {
		if(null == encadrement){
			this.encadrement = null;
		}else{
			this.encadrement = encadrement;
		}
	}
	
	public void setEncadrement(String encadrement) {
		setEnumEncadrement(Encadrement.valueOf(encadrement));
	}

	public NiveauAutonomie getEnumNiveau() {
		if(null == niveau){
			return NiveauAutonomie.P0;
		}else{
			return niveau;
		}
	}

	public String getNiveau() {
		if(null == niveau){
			return NiveauAutonomie.P0.toString();
		}else{
			return niveau.toString();
		}
	}

	public void setEnumNiveau(NiveauAutonomie niveau) {
		if(niveau.equals(niveau.P5)){
			setDp(true);
		}
		this.niveau = niveau;
	}
	
	public void setNiveau(String niveau) {
		setEnumNiveau(NiveauAutonomie.valueOf(niveau));
	}

	public boolean isPilote() {
		return pilote;
	}

	public void setPilote(boolean pilote) {
		this.pilote = pilote;
	}

	public int getActif() {
		return actif;
	}

	public void setActif(int actif) {
		this.actif = actif;
	}

	public boolean isDp() {
		if(getEnumNiveau().equals(NiveauAutonomie.P5)){
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

	public String getRole(){
		if(null == roles){
			return Roles.USER;
		}
		return roles.iterator().next();
	}
	
	public Roles getRoles() {
		if(null == roles){
			roles = new Roles("USER");
		}
		return roles;
	}

//	public String[] getTabRoles() {
//		if(null == roles){
//			roles = new Roles();
//		}
//		String[] tabRoles = new String[roles.size()];
//		for(int i =0; i< roles.size(); i++){
//			tabRoles[i] = roles.get(i);
//		}
//		return tabRoles;
//	

	public String getNomComplet(){
		return nom + " " + prenom + " " + niveau + " (" + telephone + ")";
	}

	public void setRoles(List<String> roles) {
		this.roles = new Roles();
		for(String role : roles){
			this.roles.add(role);
		}
	}
	
	public void setRole(String role) {
		this.role = role;
		if (null == this.roles) {
			this.roles = new Roles(role);
		}
		else {
			this.roles.add(role) ;
		}
	}
}
