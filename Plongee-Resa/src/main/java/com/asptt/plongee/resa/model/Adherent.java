package com.asptt.plongee.resa.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.wicket.authorization.strategies.role.Roles;

public class Adherent implements Serializable {

	private static final long serialVersionUID = -8569233748603210161L;

	public static enum Encadrement {E2, E3, E4 }
	
	private String numeroLicense; // ID
	private String nom;
	private String prenom;
	private Encadrement encadrement;
	private NiveauAutonomie niveau;
	private boolean pilote;
	private boolean dp;
	private boolean actif;
	private int intActif = 1;
	private Byte[] photo; // au format jpeg ??
	private String telephone;
	private String mail;
	private Roles roles;
	private String role;
	private String password;
	
	private Date dateCM;
	private Integer anneeCotisation;
	private List<ContactUrgent> contacts= null;


	
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Adherent() {
		// valeurs par défaut
		pilote = false;
		dp = false;
		actif = false;
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

	public boolean isEncadrent() {
		if(null == getEncadrement()){
			return false;
		} else {
			return true;
		}
	}

	public String getEncadrement() {
		if(null == encadrement){
			return null;
		}else{
			return encadrement.toString();
		}
	}

	public String getPrerogative() {
		if(isEncadrent()){
			return getEncadrement();
		}else{
			return getNiveau();
		}
	}

	public void setEnumEncadrement(Encadrement encadrement) {
		if(null == encadrement){
			this.encadrement = null;
		}else{
			if(encadrement.equals(Encadrement.E3)
					|| encadrement.equals(Encadrement.E4)){
				setDp(true);
			}
			this.encadrement = encadrement;
		}
	}
	
	public void setEncadrement(String encadrement) {
		if(encadrement.equals("E3")
				|| encadrement.equals("E4")){
			setDp(true);
		}
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
	
	public boolean isActif() {
		return actif;
	}

	public int getActifInt() {
		return intActif;
	}

	public void setActif(boolean actif) {
		this.actif = actif;
		if (actif) this.intActif = 1;
		else this.intActif = 0;
	}
	
	public void setActifInt(int actif) {
		this.intActif = actif;
		if (actif == 1) this.actif = true;
		else this.actif = false;
	}

	public boolean isDp() {
		if(getEnumNiveau().equals(NiveauAutonomie.P5)){
			return true;
		}else{
			if(getEncadrement() != null){
				if(getEnumEncadrement().equals(Encadrement.E3)
						|| getEnumEncadrement().equals(Encadrement.E4) ){
					return true;
				}
			}
			return false;
		}
	}

	/**
	 * Une veste rouge peut-être : 
	 * Un encadrant, un DP ou u Pilote
	 * Mais pas un externe => le test : et actif =1
	 * @return
	 */
	public boolean isVesteRouge() {
		return ( (getEncadrement()!= null || isDp() || isPilote())&& getActifInt()==1 );
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

	public String getNomComplet(){
		// Dès que le plongeur est encadrant, on affiche son niveau d'encadrement
		String niveauAffiche;
		if (getEncadrement() != null)
			niveauAffiche = getEncadrement();
		else niveauAffiche = getNiveau();
		
		// Pour les externes, le niveau est suffixé par (Ext.)
		if (getActifInt() == 2){
			niveauAffiche = niveauAffiche + " (Ext.)";
		}
		
		return nom + " " + prenom + " " + niveauAffiche + " (" + telephone + ")";
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

	public Date getDateCM() {
		return dateCM;
	}

	public void setDateCM(Date dateCM) {
		this.dateCM = dateCM;
	}

	public Integer getAnneeCotisation() {
		return anneeCotisation;
	}

	public void setAnneeCotisation(Integer anneeCotisation) {
		this.anneeCotisation = anneeCotisation;
	}

	public List<ContactUrgent> getContacts() {
		if(null == contacts){
			contacts = new ArrayList<ContactUrgent>();
		}
		return contacts;
	}

	public void setContacts(List<ContactUrgent> contacts) {
		this.contacts = contacts;
	}
}
