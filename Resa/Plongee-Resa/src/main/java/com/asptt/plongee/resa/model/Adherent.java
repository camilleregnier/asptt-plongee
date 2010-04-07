package com.asptt.plongee.resa.model;

public class Adherent {

	public static enum Encadrement {E1, E2, E3, E4 }
	
	private String numeroLicense; // ID
	private String nom;
	private String prenom;
	private Encadrement encadrement;
	private NiveauAutonomie niveau;
	private boolean pilote;
	private boolean dp;
	private Byte[] photo; // au format jpeg ??

	
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
}
