package com.asptt.plongee.resa.model;

import java.io.Serializable;

public class ContactUrgent implements Serializable {
	

	private Integer id;
	private String titre;
	private String nom;
	private String prenom;
	private String telephone;
	private String telephtwo;

	public ContactUrgent() {
		super();
		// TODO Auto-generated constructor stub
	}

	private static final long serialVersionUID = -8569233748603210161L;

	public Integer getId() {
		return id;
	}
	
	public void setId(Integer id) {
		this.id = id;
	}

	public String getTitre() {
		return titre;
	}

	public void setTitre(String titre) {
		this.titre = titre;
	}

	public String getNom() {
		return nom;
	}

	public void setNom(String nom) {
		this.nom = nom;
	}

	public String getPrenom() {
		return prenom;
	}

	public void setPrenom(String prenom) {
		this.prenom = prenom;
	}

	public String getTelephone() {
		return telephone;
	}

	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}

	public String getTelephtwo() {
		return telephtwo;
	}

	public void setTelephtwo(String telephone) {
		this.telephtwo = telephone;
	}

	@Override
	public boolean equals(Object obj) {
		ContactUrgent objContact = (ContactUrgent) obj;
		if (this.getNom().equalsIgnoreCase(objContact.getNom()) 
				&& this.getPrenom().equalsIgnoreCase(objContact.getPrenom()) ){
			return true;
		} else {
			return false;
		}
	}
	
	public String toString(){
		return getTitre() + " | " + getPrenom() + " | " + getNom() + " | " + getTelephone() + (getTelephtwo() == null ? "" : " | " + getTelephtwo());
	}


}
